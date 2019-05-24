package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Powers
{
    /*
   00 private int infiniteLevel = 0;
   01 private int ammoRegenLevel = 0;
   02 private int lifeStealLevel = 0;
   03 private int lifeRegenLevel = 0;
   04 private int damageLevel = 0;
   05 private int protectionLevel = 0;
   06 private int poisonLevel = 0;
   07 private int poisonProtectionLevel = 0;
   08 private int explosiveLevel = 0;
   09 private int explosiveProtectionLevel = 0;
   10 private int slowLevel = 0;
   11 private int slowProtectionLevel;
   12 private int speedBoostLevel = 0;
   13 private int fireChanceLevel = 0;
   14 private int fireProtectionLevel = 0;
   15 private int instagibChanceLevel = 0;
   16 private int instagibProtectionLevel = 0;
   17 private int stunChanceLevel = 0;
   18 private int stunProtectionLevel = 0;
   19 private int atkSpeedBoostLevel = 0;
   20 private int atkSpeedProtectionLevel = 0;
   21 private int jumpBoostLevel = 0;
   22 private int fallProtectionLevel = 0;
   23 private int charmChanceLevel = 0;
   24 private int invisibilityLevel = 0;
   25 private int knockBackLevel = 0;
   26 private int drawInLevel = 0;
   27 private int sturdyLevel = 0;
   28 private int splashLevel = 0;
   29 private int retentionLevel = 0;
*/

    static int getID(String powerName)
    {
        switch(powerName)
        {

            case "BROKEN":       //just wop with a neg level
            case "POWER":       //wop staple
                return -1;
            case "JAMMING":     //May misfire occasionally
            case "INFINITY":    //can only be pos, infinite durability
                return 0;
            case "ROBBING":     //durability decreases with time
            case "AMMO REGEN":  //durability increases with time
                return 1;
            case "CHARITY":     //gives life to enemy on hit (precentage most likley)
            case "VAMP":        //takes life from enemey on hit (precentage?)
                return 2;
            case "DYING":       //life drains over time
            case "YOUTH":       //life regens over time
                return 3;
            case "FEEBLE":      //reduces damage dealt
            case "DAMAGE":      //increases damage dealt
                return 4;
            case "WEAKNESS":    //increases damage taken
            case "PROTECTION":  //decreases damage taken
                return 5;
            case "TAINTED":     //chance to be poisoned on hit
            case "TOXIC":       //poisons on hit
                return 6;
            case "DRUGGED":     //increases damage from being poisioned
            case "ANTIDOTE":    //protects/reduces being poisoned
                return 7;
            case "VOLATILE":    //chance to explode when using (damage dealt may be unhealable?)
            case "BOOM":        //level 1 50% chance to cause small explosion, 2 100%, 3 larger explosion
                return 8;
            case "CRUMBLE":     //explosions do more damage
            case "BLAST PRUF":  //explosions do less damage. if possible they reduce the explosion knockback
                return 9;
            case "QUICKEN":     //speeds enemies movement on hit
            case "TRIPPY":      //slows enemies movement on hit
                return 10;
            case "DEADWEIGHT":  //increases
            case "OIL":         //reduces slowing of the player
                return 11;
            case "TURTLE":      //slows player movement speed
            case "CAFFEINE":    //speeds player movement speed
                return 12;
            case "COMBUSTIBLE":  //chance to catch fire when used
            case "HEATED":       //chance to cause fire on hit
                return 13;
            case "DRY":         //increases fire damage taken
            case "MOIST":       //decreases fire damage taken
                return 14;
            case "INSTAGIB":   //increases chnace to insta kill
                return 15;
            case "INSTA PRUF": //decreases insta kill damage 1 = 50% hp 2 = immune
                return 16;
            case "HALTING":
            case "CONFOUNDING":
                return 17;



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

        switch (powerID)
        {
            case 12:
                System.out.println("Power Applied");
                if (powerLevel < 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, powerLevel * -1));
                else if (powerLevel > 0)
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, powerLevel));
                break;
            default:
        }
    }

    static void enchant(int powerID, int powerLevel, ItemStack itemStack)
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
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.SLOW);
    }
}
