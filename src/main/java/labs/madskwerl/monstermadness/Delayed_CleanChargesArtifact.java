package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Delayed_CleanChargesArtifact extends BukkitRunnable
{
    private Player player;
    private int oldSlot;
    private int newSlot;

    public Delayed_CleanChargesArtifact(Player player, int oldSlot, int newSlot)
    {
        this.player = player;
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
    }

    @Override
    public void run()
    {
        MonsterMadness.NSA.cleanChargesArtifact(this.player, this.oldSlot, this.newSlot);
    }
}
