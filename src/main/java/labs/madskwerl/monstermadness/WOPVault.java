package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;

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
}
