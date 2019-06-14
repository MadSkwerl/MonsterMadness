package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Delayed_RefreshChargesArtifact extends BukkitRunnable
{
    private Player player;
    public Delayed_RefreshChargesArtifact(Player player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        MonsterMadness.NSA.refreshChargesArtifact(this.player);
    }
}
