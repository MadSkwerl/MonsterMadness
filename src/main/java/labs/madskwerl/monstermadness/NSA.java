package labs.madskwerl.monstermadness;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Random;

public class NSA implements Listener
{
    private JavaPlugin plugin;
    private Random random = new Random();
    private PlayerBank playerBank;

    public NSA(JavaPlugin plugin,  PlayerBank playerBank)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.playerBank = playerBank;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        Block blockClicked = e.getClickedBlock();
        Action action = e.getAction();
        EquipmentSlot hand = e.getHand();
        System.out.println(action + " with " + hand);
        // If the player left clicks
        if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))
        {
            ItemStack itemStackInMainHand =  e.getPlayer().getInventory().getItemInMainHand();
            if(!WOP.isWOP(itemStackInMainHand))
                return;

            ItemMeta itemMeta = itemStackInMainHand.getItemMeta();
            System.out.println("localizedName: " + itemMeta.getLocalizedName());
            System.out.println("lore: " + itemMeta.getLore());

            try //to handle null pointer exceptions
            {
                //============================== Interact: Cool-Down/Durability =======================================
                long currentTime = System.currentTimeMillis();
                PlayerData playerData = this.playerBank.getPlayer(player.getName());
                if ((currentTime - playerData.lastAttackTime)   > playerData.attackDelay &&//if player's cool-down is finished
                    (currentTime - playerData.lastWOPRegenTime) > 100)//and 100ms since last WOP regen (to prevent making too many)
                {
                    playerData.lastAttackTime = currentTime;//then reset player cool-down period

                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = WOP.getPowerLevel(itemStackInMainHand, "FRAGILE");
                    int damageAmount   = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                    int maxDamage      = itemStackInMainHand.getType().getMaxDurability();
                    int currentDamage  = damageable.getDamage();
                    int newDamage      = currentDamage + (5 + damageAmount * 5);

                    if (newDamage >= maxDamage)//if durability is too low
                    {
                        e.setCancelled(true);//cancel the event
                        return;
                    } else //otherwise, remove durability (ammo)
                    {
                        System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                        damageable.setDamage(newDamage);

                        itemStackInMainHand.setItemMeta(itemMeta);

                    //===================================== Interact: Regen ===========================================
                        //only start a new regen ammo recursion if current damage is 0 and item has appropriate lore
                        if(currentDamage == 0 && (WOP.getPowerLevel(itemStackInMainHand, "REGEN")   > 0 ||
                                                  WOP.getPowerLevel(itemStackInMainHand, "ROBBING") < 0))
                        {    System.out.println("Regen Timer Started From Player Interact.");
                            new Regen_Ammo(this, itemStackInMainHand, playerData).runTaskLater(this.plugin, 20);
                        }
                    }
                    //======= Regen ====

                    //==================================== Interact: Jamming ==========================================
                    //roll for power, will either be jamming or volatile, not both
                    int roll = this.random.nextInt(5);
                    //Handle Jamming power
                    if (WOP.getPowerLevel(itemStackInMainHand, "JAMMING") * -1 > roll)
                    {
                        System.out.println("InteractCanceled");
                        e.setCancelled(true);
                        return;
                    }
                    //======== End Jamming ====
                    if (blockClicked == null)
                        return;
                    //================================= Interact: Volatile/Boom =======================================
                    Location location = null;
                    if (WOP.getPowerLevel(itemStackInMainHand, "VOLATILE") * -1 > roll) //powerID:8 = volatile/boom. * -1 inverts the neg to pos
                        location = player.getLocation(); //explode on player
                    else if (WOP.getPowerLevel(itemStackInMainHand, "BOOM") > roll)
                        location = blockClicked.getLocation(); //explode where the player is looking
                    //note this only handle melee atm
                    if (location != null)
                    {
                        Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                        fireball.setCustomName("WOP_" + player.getName()); //provides way to track entity
                        fireball.setYield(2);
                        fireball.setIsIncendiary(false);
                        fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
                    }
                    //======= End Volatile/Boom ====
                }
                //========= End Cool-Down =======
            }catch(Exception err){System.out.println("InteractErrorCaught");}
        }
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e)
    {
        try
        {
            Player player = (Player) e.getEntity();
            PlayerData playerData = this.playerBank.getPlayer(player.getName());
            ItemStack itemStack = e.getItem().getItemStack();
            if(WOP.isWOP(itemStack))
            {
                if(WOP.getPowerLevel(itemStack, "AMMO REGEN") > 0 || //if item picked up is regen or robbing
                   WOP.getPowerLevel(itemStack, "ROBBING") < 0)
                {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLocalizedName(itemMeta.getLocalizedName()+"Ammo_Regen");//add temporary tag for primer
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo_Primer(this, player).runTaskLater(this.plugin, 1);
                }
                else if(WOP.getPowerLevel(itemStack, "YOUTH") > 0 || WOP.getPowerLevel(itemStack, "DYING") < 0 && !playerData.isRegenHealth)
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
            }

        }catch(Exception err){}
    }


    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {
        try
        {
            PlayerData playerData = this.playerBank.getPlayer(e.getPlayer().getName());
            ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
            int youthLevel = WOP.getPowerLevel(itemStack, "YOUTH");
            int dyingLevel = WOP.getPowerLevel(itemStack, "DYING");
            if (playerData != null && !playerData.isRegenHealth && (youthLevel > 0 || dyingLevel < 0))
                new Regen_Health(this, e.getPlayer()).runTaskLater(this.plugin, 20);
        }
        catch(Exception err){}
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e)
    {
        //debug stuff atm
        System.out.println(e.getEntity() + " was hit by " + e.getCause() + " for " + e.getDamage() + ". Orig: " + e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE));
        if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
        {
            //e.setDamage(1.0);
            //e.setCancelled(true);
            //EntityDamageEvent event = new EntityDamageEvent(e.getEntity(), EntityDamageEvent.DamageCause.CUSTOM, e.getDamage());
            //this.plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
    {
        try{
            int roll = this.random.nextInt(5);
            Player player = (Player) e.getDamager();
            ItemStack itemStackInMainHand = player.getInventory().getItemInMainHand();
            Entity target = e.getEntity();
            //================================= Entity vs Entity: Volatile/======================================
            //note this only handles melee atm
           if (WOP.getPowerLevel(itemStackInMainHand, "BOOM") > roll)
           {
                Location location = target.getLocation(); //explode where the player is looking
                Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                fireball.setCustomName("WOP_" + player.getName()); //provides way to track entity
                fireball.setYield(2);
                fireball.setIsIncendiary(false);
                fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
            }


            //======= End Volatile/Boom ====

        }catch(Exception err){}

        /*
        EntityDamageEvent.DamageCause damageCause = e.getCause();
        if(damageCause == EntityDamageEvent.DamageCause.PROJECTILE) //retrieves wop from projectile
        {
            Projectile projectile = (Projectile) e.getDamager();
            ProjectileSource source = projectile.getShooter();
            try
            {
                String localizedName = ((LivingEntity)source).getEquipment().getItemInMainHand().getItemMeta().getLocalizedName();
                weapon = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
            }
            catch (Exception err){}
            System.out.println( weapon.getUID() + "was used to hit " + e.getEntity().toString() + " for " + e.getDamage());
        }
        else if(damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) //retrieves wop from entity attack
        {
            try
            {
                String localizedName = ((LivingEntity)e.getDamager()).getEquipment().getItemInMainHand().getItemMeta().getLocalizedName();
                weapon = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
            }
            catch (Exception err){}
            System.out.println( weapon.getUID() + "was used to hit " + e.getEntity().toString() + " for " + e.getDamage());
        }
        else if(damageCause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) //retrieves wop from custom fireball explosion
        {
            String customName = e.getDamager().getCustomName();
            if(customName != null && customName.contains("WOP"))
            {
                try
                {
                    String localizedName = this.plugin.getServer().getPlayer(customName.substring(4)).getEquipment().getItemInMainHand().getItemMeta().getLocalizedName();
                    weapon = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
                }
                catch (Exception err){}
                System.out.println( weapon.getUID() + "was used to hit " + e.getEntity().toString() + " for " + e.getDamage());
            }
        }
        else
        {
            System.out.println( e.getDamager() + " hit " + e.getEntity().toString() + " with " + e.getCause() + " for " + e.getDamage());
        }
        */

    }

    @EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent e)
    {
        //================= Cancel Durability Loss ===========================
        try
        {   if (WOP.isWOP(e.getItem()))//if the damaged item is a WOP
                e.setCancelled(true);//cancel the durability loss
        }catch (Exception err){System.out.println("Error onPlayerItemDamageEvent");}
        //======= End Durability Loss ====
    }

    //called by regen_ammo BukkitRunnable (initially onPlayerInteract, recursively through regenAmmo)
    public void regenAmmo(ItemStack itemStack, PlayerData playerData)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;
        if (itemStack.getAmount() > 0 && itemMeta != null)//if item exists and has meta
        {
            int powerLevel = WOP.getPowerLevel(itemStack, "AMMO REGEN");//check to see if it is ammo regen
            if(powerLevel == 0)
                powerLevel = WOP.getPowerLevel(itemStack, "ROBBING");//or if it is robbing
            int maxDamage = itemStack.getType().getMaxDurability();//check max damage
            int currentDamage = damageable.getDamage();            //and current damage
            boolean hasRegenAndIsDamaged = (powerLevel > 0 && currentDamage > 0);
            boolean hasRobbingAndIsNotFullyDamaged = (powerLevel < 0 && currentDamage < maxDamage-1);
            if( hasRegenAndIsDamaged || hasRobbingAndIsNotFullyDamaged ) //isDamaged(for regen) or isNotFullyDamaged(for robbing)
            {
                    int newDamage = currentDamage - (powerLevel * 2);
                    if(newDamage < 0)
                        newDamage = 0;//catches underflow of durability (going over 100%)
                    if(newDamage > maxDamage - 1)
                        newDamage = maxDamage - 1;//and durability from going to 0%

                    playerData.lastWOPRegenTime = System.currentTimeMillis();//timestamp to prevent creating too many regen_ammo tasks
                    damageable.setDamage(newDamage);
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo(this, itemStack, playerData).runTaskLater(this.plugin, 20);

                    System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                    System.out.println("Regen Timer Started From Timer.");
            }

        }
    }

    public void fireRegenAmmo(Player player)
    {
        PlayerData playerData = this.playerBank.getPlayer(player.getName());
        for (ItemStack itemStack: player.getInventory())
        {
                ItemMeta itemMeta = itemStack.getItemMeta();
                String localizedName = "";
                if(itemMeta != null)
                    localizedName = itemMeta.getLocalizedName();
                if(localizedName.contains("Ammo_Regen"))
                {
                    itemMeta.setLocalizedName(localizedName.replace("Ammo_Regen", ""));//remove temp tag
                    itemStack.setItemMeta(itemMeta);
                    this.regenAmmo(itemStack , playerData);
                    break;
                }
        }
    }

    public void regenHealth(Player player)
    {
        PlayerData playerData = playerBank.getPlayer(player.getName());
        if(playerData != null)
        {
            try
            {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                double maxHP = playerData.maxHP;
                double currentHP = player.getHealth();
                int youthLevel = WOP.getPowerLevel(itemStack, "YOUTH");
                if (itemStack.getAmount() > 0 && youthLevel > 0 && currentHP != maxHP)
                {
                    double newHP = currentHP + youthLevel;
                    if (newHP > maxHP)
                        newHP = maxHP;
                    player.setHealth(newHP);
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
                } else
                {
                    int dyingLevel = WOP.getPowerLevel(itemStack, "DYING");
                    if (itemStack.getAmount() > 0 && dyingLevel < 0 && currentHP > 0.5)
                    {
                        double newHP = currentHP + dyingLevel;
                        if (newHP < 0.5)
                            newHP = 0.5;
                        player.setHealth(newHP);
                        new Regen_Health(this, player).runTaskLater(this.plugin, 20);
                    } else
                        playerData.isRegenHealth = false;
                }
            } catch (Exception e)
            {
                playerData.isRegenHealth = false;
            }
        }
    }


}
