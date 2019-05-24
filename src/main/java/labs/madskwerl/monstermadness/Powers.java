package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Powers
{
    static int getID(String powerName)
    {
        switch(powerName)
        {
            case "POWER":
                return -1;
            case "SLOW":
            case "SPEED":
                return 12;
            default:
                return -2;
        }
    }

    static String getName(int powerID, int powerLevel)
    {
        switch(powerID)
        {
            case -1:
                return "POWER";
            case 12:
                if(powerLevel < 0)
                    return "SLOW";
                else if(powerLevel > 0 )
                    return "SPEED";
                break;
        }
        return "";
    }

    static String getPrefix(int powerID, int powerLevel)
    {
        switch(powerID)
        {
        }
        return "";
    }

    static String getSuffix(int powerID, int powerLevel)
    {

            switch(powerID)
            {
                case 12:
                    if(powerLevel < 0)
                        return " OF SLOW";
                    else if(powerLevel > 0)
                        return " OF SPEED";
                    break;
            }
            return "";
    }

    static void apply(int powerID, int powerLevel, Player player)
    {
        System.out.println("Power Applied");
        switch (powerID)
        {
            case 12:
                if (powerLevel < 0)
                {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, powerLevel * -1 * 25));
                }
                else if (powerLevel > 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, powerLevel * 25));
            break;
        }
    }

    static void enchant(int powerID, int powerLevel, WOP wop )
    {
        switch (powerID)
        {
        }
    }
    static int getBaseMagnitude(int powerID)
    {
        switch(powerID)
        {
            default:
                return 1;
        }
    }

    static void removePowers(Player player)
    {
        System.out.println("Powers Removed");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 0, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0));
    }
}