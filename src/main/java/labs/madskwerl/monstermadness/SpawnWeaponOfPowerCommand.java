package labs.madskwerl.monstermadness;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Random;

public class SpawnWeaponofPowerCommand implements CommandExecutor {
    private Random random = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player)
        {
            player = (Player) sender;
            SwordWOP swordWOP = new SwordWOP(12,(int)(random.nextDouble() * 21) - 10);
            player.getInventory().addItem(swordWOP);
        }
        else
            return false;


        return true;
    }
}
