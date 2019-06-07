package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WOP
{
    public static void newWOP(String wopName, ItemStack itemStack, int powerID, int powerLevel)
    {
        WOP.setDisplayName(itemStack, powerID, powerLevel);

        ItemMeta itemMeta = itemStack.getItemMeta();
        String localizedName = "WOP:" + wopName;
        for (int i = 0; i < 31; i++)
            localizedName += ":" + "0";
        itemMeta.setLocalizedName(localizedName);
        itemStack.setItemMeta(itemMeta);

        WOP.setPower(itemStack, powerID, (int) Math.ceil(powerLevel / 2.0));
    }

    public static boolean setDisplayName(ItemStack itemStack, int powerID, int powerLevel)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();

            String materialName = itemStack.getType().toString();
            materialName = materialName.replace("_", " "); //               Example 1:    Example 2:
            itemMeta.setDisplayName(Powers.getPrefix(powerID, powerLevel) + //set prefix                     BROKEN
                    materialName + //material name  IRON SWORD     CROSSBOW
                    Powers.getSuffix(powerID, powerLevel) + //suffix          OF POWER
                    " " + powerLevel);  //power level        3             -2
            itemStack.setItemMeta(itemMeta);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean setLocalizedName(ItemStack itemStack, int powerID, int powerLevel)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String[] powersArray = itemMeta.getLocalizedName().split(":");
            String hexString = Integer.toHexString(powerLevel);
            if(hexString.length() > 2)
                hexString = hexString.substring(hexString.length() - 2, hexString.length());
            powersArray[powerID + 2] = hexString;
            itemMeta.setLocalizedName(String.join(":", powersArray));
            itemStack.setItemMeta(itemMeta);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean setPower(ItemStack itemStack, int powerID, int powerLevel)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        try
        {
            List<String> loreList = itemMeta.getLore();//get the current lore list
            if (loreList == null)//if there was no lore
                loreList = new ArrayList<>();//then make a new list of lore
            String powerName = Powers.getName(powerID, powerLevel);
            boolean loreSet = false;
            for (int i = 0; i < loreList.size(); i++)//go through each existing lore in this weapon
            {
                String lore = loreList.get(i);
                if (lore.substring(lore.length() - 3).equals(powerName))//if there is an existing lore with the same name
                {
                    loreList.set(i, powerName + String.format("%" + 3 + "s", powerLevel));//then set lore with new power
                    loreSet = true;//and indicate that you set the lore
                    break;
                }
            }
            if (!loreSet)//if the lore was not set (i.e. the weapon does not have this lore)
                loreList.add(powerName + String.format("%" + 3 + "s", powerLevel));//then append it to the list

            itemMeta.setLore(loreList);
            itemStack.setItemMeta(itemMeta);


            return WOP.setLocalizedName(itemStack, powerID, powerLevel);
        } catch (Exception e)
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
                    } catch (Exception e)
                    {
                        return 0;
                    }
                }
            }
        } catch (Exception e)
        {
        }
        return 0;
    }

    public static int getPowerLevel(String customName, int powerID)
    {
        String[] powersArray = customName.split(":");
        return (byte)Integer.parseInt(powersArray[powerID + 2], 16);
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

    public static boolean isWOP(String customName)
    {
        return customName.contains("WOP");
    }

    public static int getMaxDurability(String localizedName)
    {
        String[] localizedNameArray = localizedName.split(":");
        switch(localizedNameArray[1])
        {
            case "IRON_SWORD":
            case "BOW":
            case "SNOWBALL":
               return  99;

            default:
               return 0;
        }
    }

    public static String getType(String customName)
    {
        return customName.split(":")[1];
    }
}
