package labs.madskwerl.monstermadness;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Random;

public class NSA implements Listener
{
    private JavaPlugin plugin;
    private WOPVault wopVault;
    private Random random = new Random();
    private PlayerBank playerBank = new PlayerBank();

    public NSA(JavaPlugin plugin, WOPVault wopVault)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.wopVault = wopVault;
        for (Player player : plugin.getServer().getOnlinePlayers())
            playerBank.addPlayer(player.getName());
    }

    @EventHandler
    public void onPlayerAnimationEvent(PlayerAnimationEvent e)
    {
        PlayerAnimationType playerAnimationType = e.getAnimationType();
        //System.out.println(e.getPlayer() + " did a " + playerAnimationType);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Action action = e.getAction();
        EquipmentSlot hand = e.getHand();
        System.out.println(action + " with " + hand);
        // If the player right clicks
        if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) && hand != null && hand.equals(EquipmentSlot.HAND))
        {
            WOP weapon = null;
            ItemStack itemStack =  e.getPlayer().getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();

            //get wop from currently held ItemStack
            try
            {
                String localizedName  = itemMeta.getLocalizedName();
                weapon  = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
            }catch (Exception err){}

            //if currently held item was a wop
            if(weapon != null)
            {
                long currentTime = System.currentTimeMillis();
                PlayerData playerData = this.playerBank.getPlayer(player.getName());
                if (playerData != null && (currentTime - playerData.lastAttackTime) > playerData.attackDelay && (currentTime - playerData.lastWOPRegenTime) > 100)
                {

                    playerData.lastAttackTime = currentTime;
                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = weapon.getPowerLevel(28);
                    int damageAmount = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                    int maxDamage = itemStack.getType().getMaxDurability();
                    int currentDamage = damageable.getDamage();
                    int newDamage = currentDamage + (5 + damageAmount * 5);

                    //Cancel if not enough durability
                    if (newDamage >= maxDamage)
                    {
                        e.setCancelled(true);
                        return;
                    } else//remove durability (ammo)
                    {
                        System.out.println("Damage Now: " + newDamage);
                        damageable.setDamage(newDamage);
                        itemStack.setItemMeta(itemMeta);
                        if(currentDamage == 0)
                            new Regen_Ammo(this, itemStack, playerData).runTaskLater(this.plugin, 20);

                    }

                    //roll for power, will either be jamming or volatile, not both
                    int roll = this.random.nextInt(5);
                    //Handle Jamming power
                    if (weapon.getPowerLevel(0) * -1 > roll)
                    {
                        e.setCancelled(true);
                        return;
                    }


                    //Handle volatile/boom power
                    Location location = null;
                    if (weapon.getPowerLevel(8) * -1 > roll) //powerID:8 = volatile/boom. * -1 inverts the neg to pos
                        location = player.getLocation(); //explode on player
                    else if (block != null && weapon.getPowerLevel(8) > roll)
                        location = block.getLocation(); //explode where the player is looking
                    //note this only handle melee atm
                    if (location != null)
                    {
                        player = e.getPlayer();
                        Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                        fireball.setCustomName("WOP_" + player.getName()); //provides way to track entity
                        fireball.setYield(2);
                        fireball.setIsIncendiary(false);
                        fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {   //applies powers when switching wop. was used with potionEffects(not being used anymore), may be removed or modified shortly
        ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
        ItemMeta itemMeta = (itemStack != null) ? itemStack.getItemMeta(): null;

        String localizedName = (itemMeta != null) ? itemMeta.getLocalizedName() : "";
        if(localizedName.contains("WOP"))
        {
            String uid = localizedName.substring(4); //strips "WOP_"
            WOP wop = wopVault.getWop(Integer.valueOf(uid));
            wop.applyPowers(e.getPlayer());
        }
        else
            Powers.removePowers(e.getPlayer());
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
        WOP weapon = null;
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

    }

    @EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent e)
    {
        //cancels regular durabiity lass for wop
        try
        {
            if (e.getItem().getItemMeta().getLocalizedName().contains("WOP"))
            {
                System.out.println("Item Dam Canceled");
                e.setCancelled(true);
            }
        }catch (Exception err){}
    }

    //call by regen_ammo BukkitRunnable
    public void regenAmmo(ItemStack itemStack, PlayerData playerData)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;
        if (itemMeta != null)// && itemStack.getAmount() > 0)
        {
            int currentDamage = damageable.getDamage();
            int maxDamage = itemStack.getType().getMaxDurability();
            if(currentDamage > 0 && itemStack.getAmount() > 0) //if wop is damaged and it exists
            {
                int powerLevel = this.wopVault.getWop(Integer.valueOf(itemMeta.getLocalizedName().substring(4))).getPowerLevel(2);
                if (powerLevel > 0)
                {
                    int newDamage = currentDamage - (powerLevel);
                    if(newDamage < 0)
                        newDamage = 0;
                    playerData.lastWOPRegenTime = System.currentTimeMillis();
                    System.out.println("Damage Now: " + newDamage);
                    damageable.setDamage(newDamage);
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo(this, itemStack, playerData).runTaskLater(this.plugin, 20);
                    System.out.println("Regen Timer Started From Timer.");
                }
            }

        }
    }
}
