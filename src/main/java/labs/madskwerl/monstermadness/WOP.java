package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WOP
{
    public static void newWOP(ItemStack itemStack, int powerID, int powerLevel)
    {
        WOP.setDisplayName(itemStack, powerID, powerLevel);
        WOP.setPower(itemStack, powerID, powerLevel);
        WOP.setLocalizedName(itemStack);
    }

    public static boolean setDisplayName(ItemStack itemStack, int powerID, int powerLevel)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();

            String materialName = itemStack.getType().toString();
            materialName = materialName.replace("_", " ");
            itemMeta.setDisplayName(Powers.getPrefix(powerID, powerLevel) + materialName + Powers.getSuffix(powerID, powerLevel) + " " + powerLevel);
            itemStack.setItemMeta(itemMeta);
            return true;
        }catch (Exception e){ return false; }
    }

    public static boolean setLocalizedName(ItemStack itemStack)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLocalizedName("WOP");
            itemStack.setItemMeta(itemMeta);
            return true;
        }catch (Exception e){return false;}
    }

    public static boolean setPower(ItemStack itemStack, int powerID, int powerLevel)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        try
        {
            List<String> loreList = itemMeta.getLore();
            if(loreList == null)
                loreList = new ArrayList<>();
            String powerName = Powers.getName(powerID, powerLevel);
            boolean loreSet = false;
            for(int i = 0; i < loreList.size(); i++)
            {
                String lore = loreList.get(i);
                if(lore.substring(lore.length() - 3).equals(powerName))
                {
                    loreList.set(i, powerName + String.format("%" + 3 + "s", powerLevel));
                    loreSet = true;
                    break;
                }
            }

            if(!loreSet)
                loreList.add(powerName + String.format("%" + 3 + "s", powerLevel));

            itemMeta.setLore(loreList);
            itemStack.setItemMeta(itemMeta);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static int getPowerLevel(ItemStack itemStack, String powerName)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> loreList = itemMeta.getLore();
            for (String lore : loreList)
            {
                if (lore.contains(powerName))
                {
                    String powerLevelStr = lore.substring(lore.length() - 3).trim();
                    try
                    {
                        return Integer.valueOf(powerLevelStr);
                    } catch (Exception e){ return 0; }
                }
            }
        }catch(Exception e){}
        return 0;
    }

    public static String getPowerName(ItemStack itemStack, int slotNum)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> loreList = itemMeta.getLore();
            String lore = loreList.get(slotNum);
            return lore.substring(0, lore.length() - 3).trim();
        } catch (Exception e){return "";}
    }

    public static boolean isWOP(ItemStack itemStack)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String localizedName = itemMeta.getLocalizedName();
            return localizedName.contains("WOP");
        }catch (Exception e){return false;}
    }
}
