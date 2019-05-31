package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WOPVault
{
    private static int next_uid = 1;
    private List<WOP> wopVault = new ArrayList<>();

    public WOPVault()
    {
    }


    public void newWOP(ItemStack itemStack, int powerID, int powerLevel)
    {
        WOP wop = new WOP(itemStack, powerID, powerLevel, next_uid++);
        wop.link(itemStack);
        this.wopVault.add(wop);
    }

    public WOP getWop(int wop_uid)
    {
        for (WOP wop:wopVault)
        {
            if(wop.getUID() == wop_uid)
            {
                return wop;
            }
        }
        return null;
    }
/*
    public void addWOP(ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta != null && itemMeta.getLocalizedName().contains("WOP"))
        {
           itemMeta.setLocalizedName("WOP_" + next_uid++);
           int powerLevel = Integer.valueOf(itemMeta.getDisplayName().substring(-1));
           WOP wop = newWOP(itemStack, );
        }
    }

*/
    public boolean removeWOP(int wop_uid)
    {
        if (wopVault.contains(this.getWop(wop_uid)))
        {
            wopVault.remove(this.getWop(wop_uid));
            return true;
        }
        return false;
    }
}
