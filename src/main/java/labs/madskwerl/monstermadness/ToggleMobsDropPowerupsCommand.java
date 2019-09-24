package labs.madskwerl.monstermadness;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ToggleMobsDropPowerupsCommand implements CommandExecutor
{
    public enum COMMAND_SYNTAX {WOP, ID, VALID, REMOVE}

    public ToggleMobsDropPowerupsCommand(){}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0)
        {
            NSA.MobsDropPowerups = !NSA.MobsDropPowerups;
        }
        else if(args[0].toLowerCase().equals("true") ||
                args[0].toLowerCase().equals("t") ||
                args[0].toLowerCase().equals("on"))
        {
            NSA.MobsDropPowerups = true;
        }
        else if(args[0].toLowerCase().equals("false") ||
                args[0].toLowerCase().equals("f") ||
                args[0].toLowerCase().equals("off"))
        {
            NSA.MobsDropPowerups = false;
        }
        else
            sender.sendMessage("Usage: /ToggleMobsDropPowerups [option]" );

        return true;
    }
}
