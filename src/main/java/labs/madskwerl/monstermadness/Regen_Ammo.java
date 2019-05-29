package labs.madskwerl.monstermadness;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo extends BukkitRunnable
{
    NSA nsa;
    ItemStack itemStack;
    int regenPowerLevel;
    public Regen_Ammo(NSA nsa, ItemStack itemStack)
    {
        this.nsa = nsa;
        this.itemStack = itemStack;
    }
    @Override
    public void run()
    {
        nsa.regenAmmo(this.itemStack);
    }
}
