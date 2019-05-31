package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Ammo_Primer extends BukkitRunnable
{
    private NSA nsa;
    private Player player;
    public Regen_Ammo_Primer(NSA nsa, Player player)
    {
        this.nsa = nsa;
        this.player = player;
    }

    @Override
    public void run()
    {
        nsa.fireAmmoRegen(player);
    }
}
