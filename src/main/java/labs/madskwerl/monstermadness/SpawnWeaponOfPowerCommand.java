package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.Random;


public class SpawnWeaponOfPowerCommand implements CommandExecutor
{
    public enum WOP_SYNTAX {WOP, REMOVE}
    private Random random = new Random();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player;
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
                this.outputSyntax(WOP_SYNTAX.WOP, player);
            }//=======================================End Block: No Args=========================================
            else if (args[0].matches("-?\\d+"))
            {
                try
                {
                    int powerID = Integer.valueOf(args[0]);
                    if(!Powers.getName(powerID, 1).equals(""))
                    {
                        if(args.length == 1)
                        {
                            for(int i =  -10; i < 11; i +=2)
                            {
                                ItemStack itemStack = new ItemStack(Material.IRON_SWORD, 1);
                                WOP.newWOP(itemStack, powerID, i);
                                player.getInventory().addItem(itemStack);
                            }
                        }
                        else if(args.length == 2 && args[1].matches("-?\\d+"))
                        {
                            ItemStack itemStack = new ItemStack(Material.IRON_SWORD, 1);
                            WOP.newWOP(itemStack, powerID, Integer.valueOf(args[1]));
                            player.getInventory().addItem(itemStack);
                        }
                        else
                            this.outputSyntax(WOP_SYNTAX.WOP, player);
                    }
                }catch (Exception e){return false;}
            }
            //========================================Start Block: Remove========================================
            else if (args[0].toLowerCase().equals("remove") || args[0].toLowerCase().equals("rm"))
            {
                try
                {
                    if (args.length == 2 && (args[1].toLowerCase().equals("h") || args[1].toLowerCase().equals("hand")))
                    {       ItemStack item = player.getInventory().getItemInMainHand();
                            if (WOP.isWOP(item)) //Remove item in hand/cursor if it is a WOP
                                item.setAmount(0);
                    }else if (args.length == 1) //Remove all weapons of power in player's inventory
                    {   for (ItemStack item : player.getInventory().getContents())
                        {   if (WOP.isWOP(item))
                            item.setAmount(0);
                        }
                    }else
                    {   this.outputSyntax(WOP_SYNTAX.REMOVE, player);
                        //return false;
                    }
                    player.updateInventory();
                }catch (Exception e)
                {   this.outputSyntax(WOP_SYNTAX.REMOVE, player);
                }
                //=====================================End Block: Remove============================================
            } else
                return false;
        }
        return true;
    }

    private void outputSyntax(WOP_SYNTAX syntax, Player player)
    {
        switch(syntax)
        {
            case WOP:
                player.sendMessage("Usage: /wop <power_id> <power_level>");
                break;
            case REMOVE:
                player.sendMessage("Usage:\n/wop remove [hand]\n/wop rm [h]");
                break;
            default:
                player.sendMessage("Usage: /wop [options]" +
                        "\n<power_id> <power_level>" +
                        "\nremove [hand]");
                break;
        }

    }
}
