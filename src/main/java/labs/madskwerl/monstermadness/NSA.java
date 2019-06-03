package labs.madskwerl.monstermadness;


import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class NSA implements Listener
{
    private JavaPlugin plugin;
    private Random random = new Random();
    private LivingEntityBank livingEntityBank;

    public NSA(JavaPlugin plugin,  LivingEntityBank livingEntityBank)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.livingEntityBank = livingEntityBank;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        String customName = player.getCustomName();
        Block blockClicked = e.getClickedBlock();
        Action action = e.getAction();
        EquipmentSlot hand = e.getHand();
        System.out.println(action + " with " + hand);
        // If the player left clicks
        if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))
        {
            ItemStack itemStackInMainHand =  e.getPlayer().getEquipment().getItemInMainHand();
            if(!WOP.isWOP(itemStackInMainHand))
                return;

            ItemMeta itemMeta = itemStackInMainHand.getItemMeta();

            try //to handle null pointer exceptions
            {
                //============================== Interact: Cool-Down/Durability =======================================
                long currentTime = System.currentTimeMillis();
                LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
                if ((currentTime - livingEntityData.getLastAttackTime())   > livingEntityData.getAttackDelay() &&//if player's cool-down is finished
                    (currentTime - livingEntityData.getLastWOPRegenTime()) > 100)//and 100ms since last WOP regen (to prevent making too many)
                {
                    livingEntityData.setLastAttackTime(currentTime);//then reset player cool-down period

                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = WOP.getPowerLevel(customName, 28); //PowerID:28 = FRAGILE
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
                        if(currentDamage == 0 && WOP.getPowerLevel(customName, 3) != 0) //PowerID:3 = REGEN
                        {    System.out.println("Regen Timer Started From Player Interact.");
                            new Regen_Ammo(this, itemStackInMainHand, livingEntityData).runTaskLater(this.plugin, 20);
                        }
                    }
                    //======= Regen ====

                    //==================================== Interact: Jamming ==========================================
                    //roll for power, will either be jamming or volatile, not both
                    int roll = this.random.nextInt(5);
                    //Handle Jamming power
                    if (WOP.getPowerLevel(customName, 0) * -1 > roll)//PowerID:0 = JAMMING
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
                    int boomLevel = WOP.getPowerLevel(customName, 8);
                    if (boomLevel * -1 > roll) //PowerID:8 = volatile/boom. * -1 inverts the neg to pos
                        location = player.getLocation(); //explode on player
                    else if (boomLevel > roll)
                        location = blockClicked.getLocation(); //explode where the player is looking
                    //note this only handles melee atm
                    Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                    fireball.setCustomName(customName + ":" + player.getUniqueId()); //provides way to track entity
                    fireball.setYield(2);
                    fireball.setIsIncendiary(false);
                    fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
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
            String customName = player.getCustomName();
            LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
            ItemStack itemStack = e.getItem().getItemStack();
            if(WOP.isWOP(itemStack))
            {
                if(WOP.getPowerLevel(customName, 1) != 0) //PowerID:1 = AMMO_REGEN
                {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLocalizedName(itemMeta.getLocalizedName()+"Ammo_Regen");//add temporary tag for primer
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo_Primer(this, player).runTaskLater(this.plugin, 1);
                }
                else if(WOP.getPowerLevel(customName, 3) != 0 && !livingEntityData.isRegenHealth()) //PowerID:3 = REGEN
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
            }

        }catch(Exception err){}
    }


    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {
        //e.getPlayer().getInventory().setHeldItemSlot(2);
        try
        {
            String customName = e.getPlayer().getCustomName();
            if(customName.contains("WOP"))
            {
                LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(e.getPlayer().getUniqueId());
                int regenLevel = WOP.getPowerLevel(customName, 3); //PowerID:3=REGEN
                if (livingEntityData != null && !livingEntityData.isRegenHealth() && regenLevel != 0)
                    new Regen_Health(this, e.getPlayer()).runTaskLater(this.plugin, 20);
            }
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


        try
        {
            LivingEntity attacker = (LivingEntity) this.plugin.getServer().getEntity(UUID.fromString(e.getDamager().getCustomName().substring(4)));
            String attackerCustomName = attacker.getCustomName();
            LivingEntity target = (LivingEntity) e.getEntity();
            String targetCutomName = target.getCustomName();


            //================================= Attacker is Living Entity ===================================
            ItemStack itemStackInMainHand = attacker.getEquipment().getItemInMainHand();
            //================================= Entity vs Entity: Boom ======================================

            int roll = this.random.nextInt(5);

            Location location = null;
            if (WOP.getPowerLevel(attackerCustomName, 8) > roll)//PowerID:8 = BOOM
                location = target.getLocation(); //explode where the player is looking
            //note this only handles melee atm
            Fireball fireball = (Fireball) attacker.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
            fireball.setCustomName("WOP_" + attacker.getUniqueId()); //provides way to track entity
            fireball.setYield(2);
            fireball.setIsIncendiary(false);
            fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
            //======= End Volatile/Boom ====

        }catch(Exception err){
            System.out.println("entity v entity error");
        }

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
                    String localizedName = this.plugin.getServer().getLivingEntityData(customName.substring(4)).getEquipment().getItemInMainHand().getItemMeta().getLocalizedName();
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
    public void regenAmmo(ItemStack itemStack, LivingEntityData livingEntityData)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String localizedName = itemMeta.getLocalizedName();
            Damageable damageable = (Damageable) itemMeta;
            if (itemStack.getAmount() > 0 && itemMeta != null)//if item exists and has meta
            {
                int powerLevel = WOP.getPowerLevel(localizedName, 1);//PowerID:1 = AMMO REGEN
                int maxDamage = WOP.getMaxDurability(localizedName);//check max damage
                int currentDamage = damageable.getDamage();            //and current damage
                boolean hasRegenAndIsDamaged = (powerLevel > 0 && currentDamage > 0);
                boolean hasRobbingAndIsNotFullyDamaged = (powerLevel < 0 && currentDamage < maxDamage - 1);
                if (hasRegenAndIsDamaged || hasRobbingAndIsNotFullyDamaged) //isDamaged(for regen) or isNotFullyDamaged(for robbing)
                {
                    int newDamage = currentDamage - (powerLevel * 2);
                    if (newDamage < 0)
                        newDamage = 0;//catches underflow of durability (going over 100%)
                    if (newDamage > maxDamage - 1)
                        newDamage = maxDamage - 1;//and durability from going to 0%

                    livingEntityData.setLastWOPRegenTime(System.currentTimeMillis());//timestamp to prevent creating too many regen_ammo tasks
                    damageable.setDamage(newDamage);
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo(this, itemStack, livingEntityData).runTaskLater(this.plugin, 20);

                    System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                    System.out.println("Regen Timer Started From Timer.");
                }

            }
        }catch(Exception e){System.out.println("Regen timer fail");}
    }

    public void fireRegenAmmo(Player player)
    {
        LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
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
                    this.regenAmmo(itemStack , livingEntityData);
                    break;
                }
        }
    }

    public void regenHealth(Player player)
    {
        LivingEntityData livingEntityData = livingEntityBank.getLivingEntityData(player.getUniqueId());
        if(livingEntityData != null)
        {
            try
            {
                String customName = player.getCustomName();
                double maxHP = livingEntityData.getMaxHP();
                double currentHP = player.getHealth();
                int regenLevel = WOP.getPowerLevel(customName, 3); //PowerID:3 = REGEN
                if (regenLevel != 0 && currentHP != maxHP)
                {
                    double newHP = currentHP + regenLevel;
                    if (newHP > maxHP)
                        newHP = maxHP;
                    else if(newHP < 0.5)
                        newHP = 0.5;
                    player.setHealth(newHP);
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
                } else
                    livingEntityData.setRegenHealth(false);
            } catch (Exception e)
            {
                livingEntityData.setRegenHealth(false);
            }
        }
    }


}
