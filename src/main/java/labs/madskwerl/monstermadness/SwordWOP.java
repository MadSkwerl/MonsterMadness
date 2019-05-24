package labs.madskwerl.monstermadness;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public class SwordWOP extends WOP
{

    SwordWOP(int powerID, int powerLevel)
    {
        super(powerID, powerLevel, Material.IRON_SWORD, 1);
        ItemMeta itemMeta = this.getItemMeta();
        if(itemMeta != null)
            itemMeta.setLocalizedName("Sword_WOP");
        this.setItemMeta(itemMeta);
    }
}
