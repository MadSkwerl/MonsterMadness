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
    public boolean onCommand(CommandSender sender, Command  command, String label, String[] args) {
        Player player;
        int powerID = 8;
        if (sender instanceof Player)
        {
            player = (Player) sender;

          /*  ItemStack itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,(int)(random.nextDouble() * 21) -10);
            player.getInventory().addItem(itemStack);
            */
            ItemStack itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,-10);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,-8);
            player.getInventory().addItem(itemStack);

             itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,-6);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,-4);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,-2);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,0);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,2);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,4);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,6);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,8);
            player.getInventory().addItem(itemStack);

            itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,10);
            player.getInventory().addItem(itemStack);
        }
        else
            return false;


        return true;
    }
}
