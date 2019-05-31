package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo extends BukkitRunnable
{
    private NSA nsa;
    private ItemStack itemStack;
    private PlayerData playerData;
    public Regen_Ammo(NSA nsa, ItemStack itemStack, PlayerData playerData)
    {
        this.nsa = nsa;
        this.itemStack = itemStack;
        this.playerData = playerData;
    }
    @Override
    public void run()
    {
        nsa.regenAmmo(this.itemStack, this.playerData);
    }
}
