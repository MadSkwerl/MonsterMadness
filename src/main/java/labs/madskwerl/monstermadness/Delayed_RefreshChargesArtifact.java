package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Delayed_RefreshChargesArtifact extends BukkitRunnable
{
    private NSA nsa;
    private Player player;
    public Delayed_RefreshChargesArtifact(NSA nsa, Player player)
    {
        this.nsa = nsa;
        this.player = player;
    }

    @Override
    public void run()
    {
        this.nsa.refreshChargesArtifact(this.player);
    }
}
