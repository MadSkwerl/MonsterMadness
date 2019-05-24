package labs.madskwerl.monstermadness;


import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NSA implements Listener
{
    List<Integer> bullets = new ArrayList<>();
    private  WOPVault wopVault;

    public NSA(JavaPlugin plugin, WOPVault wopVault)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.wopVault = wopVault;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        System.out.println("Interact Event Triggered");
    }
        /*
        // If the player right clicks
        Action action = e.getAction();
        if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
        {
            // If the player was holding diamond horse armour
            ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if(itemMeta != null && itemMeta.getLocalizedName().contains("SnowballWOP"))
            {
                Player player = e.getPlayer();
                // Launch the snowball where the player is looking
                Snowball bullet = player.launchProjectile(Snowball.class, player.getLocation().getDirection());
                // Save the launched snowball's id
               // bullets.add(bullet.getEntityId());
            }

        }
    }
*/
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {
        System.out.println("Item Change Event Fired.");
        ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
        ItemMeta itemMeta = (itemStack != null) ? itemStack.getItemMeta(): null;

        String localizedName = (itemMeta != null) ? itemMeta.getLocalizedName() : "";
        if(localizedName.contains("WOP"))
        {
            String uid = localizedName.substring(3); //strips "WOP_"
            WOP wop = wopVault.getWop(Integer.valueOf(uid));
            wop.applyPowers(e.getPlayer());
        }
        else
            Powers.removePowers(e.getPlayer());
    }
}
