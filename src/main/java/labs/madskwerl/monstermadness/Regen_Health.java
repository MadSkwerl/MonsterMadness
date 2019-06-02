package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Regen_Health extends BukkitRunnable
{
    private NSA nsa;
    private Player player;
    public Regen_Health(NSA nsa, Player player)
    {
        this.nsa = nsa;
        this.player = player;
    }

    @Override
    public void run()
    {
        nsa.regenHealth(player);
    }

}
