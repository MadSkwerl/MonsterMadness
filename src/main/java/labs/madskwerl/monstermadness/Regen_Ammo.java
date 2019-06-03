package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo extends BukkitRunnable
{
    private NSA nsa;
    private ItemStack itemStack;
    private LivingEntityData livingEntityData;
    public Regen_Ammo(NSA nsa, ItemStack itemStack, LivingEntityData livingEntityData)
    {
        this.nsa = nsa;
        this.itemStack = itemStack;
        this.livingEntityData = livingEntityData;
    }
    @Override
    public void run()
    {
        nsa.regenAmmo(this.itemStack, this.livingEntityData);
    }
}
