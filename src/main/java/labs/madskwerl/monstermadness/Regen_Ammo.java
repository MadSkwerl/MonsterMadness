package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo extends BukkitRunnable
{
    private ItemStack itemStack;
    private LivingEntityData livingEntityData;
    public Regen_Ammo(ItemStack itemStack, LivingEntityData livingEntityData)
    {
        this.itemStack = itemStack;
        this.livingEntityData = livingEntityData;
    }
    @Override
    public void run()
    {
        MonsterMadness.NSA.regenAmmo(this.itemStack, this.livingEntityData);
    }
}
