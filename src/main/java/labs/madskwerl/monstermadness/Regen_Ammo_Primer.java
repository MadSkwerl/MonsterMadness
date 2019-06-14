package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo_Primer extends BukkitRunnable
{
    private Player player;
    public Regen_Ammo_Primer(Player player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        MonsterMadness.NSA.fireRegenAmmo(player);
    }
}
