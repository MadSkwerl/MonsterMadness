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
import java.util.Random;

public class SpawnKitCommand implements CommandExecutor
{
    private Random random = new Random();
    private NSA nsa;

    public SpawnKitCommand(NSA nsa)
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

            //Cannot be don't while crouched (if it's even possible to send a command while crouched)
            //Note this is to avoid conflicts with the wop inv
            if(player.isSneaking())
                return false;

            //Scan inv for existing artifacts
            boolean spawnWOPInvArtifact = true;
            boolean spawnChargesArtifact = true;
            for (ItemStack itemStack : player.getInventory().getContents())
            {
                ItemMeta itemMeta = null;
                if(itemStack != null)
                    itemMeta = itemStack.getItemMeta();
                if(itemMeta != null)
                {
                    String localizedName = itemStack.getItemMeta().getLocalizedName();
                    if(localizedName.contains("INV_ARTIFACT"))
                        spawnWOPInvArtifact = false;
                    else if(localizedName.contains("CHARGES_ARTIFACT"))
                        spawnChargesArtifact = false;
                }
            }

            //spawn artifacts only if they are not present
            if(spawnWOPInvArtifact)
            {
                ItemStack itemStack = new ItemStack(Material.BLACK_BANNER, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLocalizedName("INV_ARTIFACT");
                itemStack.setItemMeta(itemMeta);
                this.nsa.livingEntityBank.getLivingEntityData(player.getUniqueId()).setInvArtifact(itemStack);
                //this.nsa.refreshInvArtifact(player); //just commented out b/c unimplemented and wanted to test refreshCharges
            }

            if(spawnChargesArtifact)
            {
                ItemStack itemStack = new ItemStack(Material.YELLOW_BANNER, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLocalizedName("CHARGES_ARTIFACT");
                ((BannerMeta)itemMeta).addPattern(new Pattern(DyeColor.GRAY, PatternType.FLOWER));
                itemStack.setItemMeta(itemMeta);
                this.nsa.livingEntityBank.getLivingEntityData(player.getUniqueId()).setChargesArtifact(itemStack);
                player.getInventory().addItem(itemStack);                //that number as the power level
            }
            new Delayed_BindChargesArtifact(this.nsa, player).runTaskLater(this.nsa.plugin, 1);
        }
        else
            return false;
        return true;
    }
}
