package labs.madskwerl.monstermadness;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Health extends BukkitRunnable
{
    private LivingEntity livingEntity;
    public Regen_Health(LivingEntity livingEntity)
    {
        this.livingEntity = livingEntity;
    }

    @Override
    public void run()
    {
        MonsterMadness.NSA.regenHealth(this.livingEntity);
    }

}
