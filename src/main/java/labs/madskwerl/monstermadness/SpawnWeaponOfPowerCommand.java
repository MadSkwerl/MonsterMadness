package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
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


            System.out.println("Sender: " + ((Player) sender).getDisplayName());
            System.out.println("Command: " + command.toString());
            System.out.println("label: " + label);
            System.out.println("args: " + Arrays.deepToString(args));
            player = (Player) sender;

            //====================================Start Block: No Args==========================================
            if (args.length == 0)
            {
          /*  ItemStack itemStack = new ItemStack(Material.IRON_SWORD,1 );
            wopVault.newWOP(itemStack, powerID,(int)(random.nextDouble() * 21) -10);
            player.getInventory().addItem(itemStack);
            */
                ItemStack itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, -10);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, -8);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, -6);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, -4);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, -2);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 0);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 2);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 4);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 6);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 8);
                player.getInventory().addItem(itemStack);

                itemStack = new ItemStack(Material.IRON_SWORD, 1);
                wopVault.newWOP(itemStack, powerID, 10);
                player.getInventory().addItem(itemStack);
            }//=======================================End Block: No Args=========================================
            //========================================Start Block: Remove========================================
            else if (args[0].toLowerCase().equals("remove") ||
                    args[0].toLowerCase().equals("rm"))
            {
                if (args.length > 1)
                {
                    if (args[1].toLowerCase().equals("h") ||
                        args[1].toLowerCase().equals("hand"))
                    {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item != null && item.hasItemMeta() &&
                            item.getItemMeta().hasLocalizedName() &&
                            item.getItemMeta().getLocalizedName().toLowerCase().contains("wop")) //Remove item in hand/cursor if it is a WOP
                        {
                            item.setAmount(0);
                            wopVault.removeWOP(Integer.valueOf(item.getItemMeta().getLocalizedName().substring(4)));
                        }
                    }
                }else //Remove all weapons of power in player's inventory
                {
                    for (ItemStack item : player.getInventory().getContents())
                    {
                        if (item != null && item.hasItemMeta() &&
                            item.getItemMeta().hasLocalizedName() &&
                            item.getItemMeta().getLocalizedName().toLowerCase().contains("wop"))
                        {
                            item.setAmount(0);
                            wopVault.removeWOP(Integer.valueOf(item.getItemMeta().getLocalizedName().substring(4)));
                        }
                    }
                }
                player.updateInventory();
            }
            //=====================================End Block: Remove=============================================
        }
        else
            return false;


        return true;
    }
}
