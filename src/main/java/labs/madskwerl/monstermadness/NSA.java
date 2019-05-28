package labs.madskwerl.monstermadness;


import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NSA implements Listener
{
    private JavaPlugin plugin;
    private WOPVault wopVault;
    private Random random = new Random();

    public NSA(JavaPlugin plugin, WOPVault wopVault)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.wopVault = wopVault;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Action action = e.getAction();
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
        {
            // If the player right clicks
            WOP weapon = null;
            try
            {
                // If the player was holding diamond horse armour
                String localizedName  =  e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLocalizedName();
                weapon  = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
            }catch (Exception err){}
            if(weapon != null)
            {
                int roll  = this.random.nextInt(5);
                System.out.println("ExplodeRoll: " + roll);
                Player player;
                if(weapon.getPowerLevel(9) * -1 >  roll) //powerID:9 = explosion protection level. * -1 inverts the neg to pos
                {

                    player = e.getPlayer();
                    Fireball fireball = (Fireball) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREBALL);
                    fireball.setCustomName("WOP_" + player.getName());
                    fireball.setYield(2);
                    fireball.setIsIncendiary(false);
                    fireball.setVelocity(new Vector(0,-100,0));
                    /*
                    Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation(), EntityType.CREEPER);
                    creeper.setExplosionRadius(2);
                    */

                    //tnt code
                   /*
                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                    tnt.setCustomName("WOP_" + player.getName());
                    tnt.setFuseTicks(0);
                    */
                   //non entity explosion code
                    //player.getWorld().createExplosion(player.getLocation(), (float)(weapon.getPowerLevel(9) * -1), false);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {
        System.out.println("Item Change Event Fired.");
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

        System.out.println(e.getEntity() + " was hit by " + e.getCause() + " for " + e.getDamage() + ". Orig: " + e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE));
        if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
        {

           // e.setDamage(1.0);
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
        if(damageCause == EntityDamageEvent.DamageCause.PROJECTILE)
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
        else if(damageCause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
        {
            try
            {
                String localizedName = ((LivingEntity)e.getDamager()).getEquipment().getItemInMainHand().getItemMeta().getLocalizedName();
                weapon = wopVault.getWop(Integer.valueOf(localizedName.substring(4)));
            }
            catch (Exception err){}
            System.out.println( weapon.getUID() + "was used to hit " + e.getEntity().toString() + " for " + e.getDamage());
        }
        else if(damageCause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
        {
            if(e.getDamager().getCustomName().contains("WOP"))
            {

            }
        }
        else
        {
            System.out.println( e.getDamager() + " hit " + e.getEntity().toString() + " with " + e.getCause() + " for " + e.getDamage());
        }

    }
}
