package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public class SnowballWOP extends WOP
{

    SnowballWOP(int powerID, int powerLevel)
    {
        super(powerID, powerLevel, Material.SNOWBALL, 64);
        ItemMeta itemMeta = this.getItemMeta();
        if(itemMeta != null)
            itemMeta.setLocalizedName("Snowball_WOP");
        this.setItemMeta(itemMeta);
    }
}
