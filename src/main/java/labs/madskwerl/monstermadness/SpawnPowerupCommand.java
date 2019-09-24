package labs.madskwerl.monstermadness;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SpawnPowerupCommand implements CommandExecutor
{
    public enum COMMAND_SYNTAX {WOP, ID, VALID, REMOVE}

    public SpawnPowerupCommand(){}

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

            if (args.length == 0)
                sender.sendMessage("Usage: /SpawnPowerup [optional: loc/inv] [Powerup ID]" );
            else if (args[0].toLowerCase().equals("inv") || args[0].toLowerCase().equals("inventory"))
            {
                if (args[1].matches("-?\\d+"))//else if 2nd arg is a number
                {
                    ItemStack itemStack = Powers.generatePowerUp(Integer.parseInt(args[1]));
                    if(itemStack == null)
                        sender.sendMessage("Invalid powerup id." );
                    else
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
                else
                    sender.sendMessage("Usage: /SpawnPowerups [optional: loc/inv] [Powerup ID]" );

            }
            else
            {
                int argIndex = 0;
                if (args[0].toLowerCase().equals("loc") || args[0].toLowerCase().equals("location"))
                    argIndex = 1;

                if (args[argIndex].matches("-?\\d+"))//else if 2nd arg is a number
                {
                    ItemStack itemStack = Powers.generatePowerUp(Integer.parseInt(args[1]));
                    if(itemStack == null)
                        sender.sendMessage("Invalid powerup id." );
                    else
                        LivingEntityBank.getLivingEntityData(player.getUniqueId()).addPowerUp(itemStack);
                }
                else
                    sender.sendMessage("Usage: /SpawnPowerup [optional: loc/inv] [Powerup ID]" );
            }
        }
        else
            return false;
        return true;
    }
}
