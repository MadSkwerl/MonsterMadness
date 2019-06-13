package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Delayed_CleanChargesArtifact extends BukkitRunnable
{
    private NSA nsa;
    private Player player;
    private int oldSlot;
    private int newSlot;

    public Delayed_CleanChargesArtifact(NSA nsa, Player player, int oldSlot, int newSlot)
    {
        this.nsa = nsa;
        this.player = player;
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
    }

    @Override
    public void run()
    {
        this.nsa.cleanChargesArtifact(this.player, this.oldSlot, this.newSlot);
    }
}
