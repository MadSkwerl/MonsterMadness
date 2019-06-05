package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Random;


public class SpawnWeaponOfPowerCommand implements CommandExecutor
{
    public enum WOP_SYNTAX {WOP, ID, VALID, REMOVE}
    private Random random = new Random();
    private NSA nsa;

    public SpawnWeaponOfPowerCommand(NSA nsa)
    {
        this.nsa = nsa;
    }

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

            //=================================== Start Block: No args ==========================================
            if (args.length == 0)
            {
                this.outputSyntax(WOP_SYNTAX.WOP, player);
            }
            //==================================== Start Block: Remove ==========================================
            else if (args[0].toLowerCase().equals("remove") ||
                     args[0].toLowerCase().equals("rm")     ||
                     args[0].toLowerCase().equals("rem"))
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
                        this.outputSyntax(WOP_SYNTAX.REMOVE, player);

                    player.updateInventory();
                }catch (Exception e)
                {   this.outputSyntax(WOP_SYNTAX.REMOVE, player);
                }
            }
            //================================ Start Block: Power and Level ======================================
            else
            {
                try
                {

                    int powerID;
                    if (args[0].matches("-?\\d+"))//if player sent the ID as a number
                        powerID = Integer.valueOf(args[0]);//use that number as the ID

                    else//otherwise, if the player sent a string name
                    {
                        args[0] = args[0].replace("_", " ");//format that name
                        powerID = Powers.getID(args[0].toUpperCase());//and get the ID for that name
                    }

                    if(!Powers.getName(powerID, 1).equals(""))//if the ID matches a valid Power
                    {
                        if (args.length == 1)//if no power level specified
                        {
                            for(int i =  -10; i < 11; i +=2)//then give player a WOP for each level
                            {

                                ItemStack itemStack = new ItemStack(Material.IRON_SWORD, 1);
                                WOP.newWOP("IRON_SWORD", itemStack, powerID, i);
                                player.getInventory().addItem(itemStack);
                            }
                            nsa.initPlayer(player);
                        }
                        else if (args.length == 2 && args[1].matches("-?\\d+"))//else if 2nd arg is a number
                        {
                            ItemStack itemStack = new ItemStack(Material.IRON_SWORD, 1);
                            WOP.newWOP("IRON_SWORD", itemStack, powerID, Integer.valueOf(args[1]));//give player a new WOP using
                            player.getInventory().addItem(itemStack);                //that number as the power level
                            nsa.initPlayer(player);
                        }
                        else
                            this.outputSyntax(WOP_SYNTAX.ID, player);//wrong amount of args, or 2nd arg is not a number
                    }
                    else
                        this.outputSyntax(WOP_SYNTAX.VALID, player);//name or ID was not a valid Power
                }catch (Exception e){return false;}
            }

        }
        return true;
    }


    private void outputSyntax(WOP_SYNTAX syntax, Player player)
    {
        switch(syntax)
        {
            case WOP:
                player.sendMessage("Usage: /wop [options]" +
                        "\n<power_id> <power_level>"       +
                        "\n<power_name> <power_level>"     +
                        "\nremove [hand]");
                break;
            case ID:
                player.sendMessage("Usage:\n/wop <power_id> <power_level>\n/wop <power_name> <power_level>");
                break;
            case VALID:
                player.sendMessage("Power Name or ID not found");
                player.sendMessage("Usage: /wop <power_name> <power_level>");
                break;
            case REMOVE:
                player.sendMessage("Usage:\n/wop remove [hand]\n/wop rm [h]");
                break;
            default:
                break;
        }

    }
}
