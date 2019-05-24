package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SpawnWeaponOfPowerCommand implements CommandExecutor {
    private Random random = new Random();
    private WOPVault wopVault;

    public SpawnWeaponOfPowerCommand(WOPVault wopVault)
    {
        this.wopVault = wopVault;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player)
        {
            player = (Player) sender;
            ItemStack itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, 12,(int)(random.nextDouble() * 21) - 10);
            player.getInventory().addItem(itemStack);
        }
        else
            return false;


        return true;
    }
}
