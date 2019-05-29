package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Powers
{
    final static int NumberOfPowers = 30;
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

    //key:  NA - Not Applicable
    //      EW - Enchant WOP.  Effect applies directly to the wop. After this is done the effect will only work when the wop is used. Requires manual disenchant
    //      OH - On Hit.       Effect is calculated using level On Hit. Only changing the level is required to obtain the effect
    //      WH - WHen Hit      Effect is calculated using level when hit. " "
    //      GT - Global Timer  Effect is applied via a timer that runs even when the wop is not equipped
    //      ET - Equip Timer   Effect is applied via a timer that run ONLY when the wop is equipped
    //      OU - On Use        Effect is applied when the wop is used.
    //      OE - On Equip      Effect is applied when the wop is equipped. This usually requires modifying the players NBT tags
    static int getID(String powerName)
    {

        switch(powerName)                                                                                             //<-- x == implemented (or if nothing is there b/c i layz, ! == not implemented, $ = implemented and tested
        {                                                                                                             //<-- "~" means partial implementation
            case "BROKEN":      //NA - just wop with a neg level                                                            <-- x
            case "POWER":       //EW - WOP has slots                                                                            <-- ~
                return -1;
            case "JAMMING":     //OU - chance for damage to not apply on hit     --implemented
            case "INFINITY":    //EW - wop becomes unbreakable                  --implemented
                return 0;
            case "ROBBING":     //GT - durability decreases with time
            case "AMMO REGEN":  //GT - durability increases with time
                return 1;
            case "CHARITY":     //OH - gives life to enemy on hit (percentage most likely)
            case "VAMP":        //OH - takes life from enemy on hit (percentage?)
                return 2;
            case "DYING":       //ET - life drains over time (uses player poison level)
            case "YOUTH":       //ET - life regens over time
                return 3;
            case "FEEBLE":      //OH - reduces damage dealt
            case "DAMAGE":      //OH - increases damage dealt
                return 4;
            case "WEAKNESS":    //WH - increases damage taken
            case "PROTECTION":  //WH - decreases damage taken
                return 5;
            case "TAINTED":     //OU - chance to be poisoned for 5 seconds when firing
            case "TOXIC":       //OH - poisons on hit
                return 6;
            case "DRUGGED":     //WH - chance to be poisoned for 5 seconds when hit
            case "ANTIDOTE":    //WH - protects/reduces being poisoned
                return 7;
            case "VOLATILE":    //OU - chance to explode when using (damage dealt may be unhealable?)           --implemented
            case "BOOM":        //OH - level 1 50% chance to cause small explosion, 2 100%, 3 larger explosion  --implemented
                return 8;
            case "CRUMBLE":     //WH - explosions do more damage to player
            case "BLAST PRUF":  //WH - explosions do less damage. if possible they reduce the explosion knockback
                return 9;
            case "QUICKEN":     //OH - speeds enemies movement on hit
            case "TRIPPY":      //OH - slows enemies movement on hit
                return 10;
            case "SHACKLE":     //WH - increases slowing of player
            case "OIL":         //WH - reduces slowing of the player
                return 11;
            case "TURTLE":      //OE - slows player movement speed
            case "CAFFEINE":    //OE - speeds player movement speed
                return 12;
            case "COMBUSTIBLE": //OU - chance to catch fire when used
            case "HEATED":      //OH - chance to cause fire on hit
                return 13;
            case "DRY":         //WH  - increases fire damage taken
            case "MOIST":       //WH  - decreases fire damage taken
                return 14;
            case "INSTAGIB":    //OH  - increases chance to insta kill
                return 15;
            case "INSTA PRUF":  //WH  - decreases insta kill damage 1 = 50% hp 2 = immune
                return 16;
            case "HALTING":     //WH  - chance to be stunned when hit
            case "CONFOUNDING": //OH  - chance (smaller that other power ups) to stun on hit
                return 17;
            case "DAZZELMENT":  //WH  - increases time player is stunned
            case "UNLEASHED":   //WH  - level 0 = 100% stun chance lvl1 = 50% lvl2 = 0% (immune). only applies to mobs that hit with stunning
                return 18;
            case "TIRING":      //OE - Slows atk speed  (player generic atk speed nbt tag)
            case "FLURRY":      //OE - speeds atk speed
                return 19;
            case "FATIGUE":     //WH  - increases the amount of atk speed reduction when taking dam from mob with atk spd reduction
            case "ENERGY":      //WH  - decreases " ". lvl0=0% lvl1=50% lvl2 = immune
                return 20;
            case "SHORTY":      //OE  - decreases jumping distance (maybe speed a little) Note this may have to be done on jump event instead
            case "SPRING":      //OE  - increases jumping distance
                return 21;
            case "AERODYNAMIC": //WH - increases fall damage (when hit by fall damage)
            case "FEATHER":     //WH - decreases fall damage
                return 22;
            case "ANGERING":    //chance to greatly increase speed, atk speed and take aggro of mob
            case "CHARMING":    //chance to pacify mob
                return 23;
            case "NOISY":       //increases the chance to be detected by a mob
            case "STEALTH":     //increases invisibility level
                return 24;
            case "UNSTABLE":    //WH - increases the amount an enemy pushes the player on hit
            case "PUSH":        //OH - increases the amount an enemy is knocked back
                return 25;
            case "LEANING":     //WH - increases the amount an enemy pulls the player on hit
            case "PULL":        //OH - increases the amount an enemy is drawn in on hit. will cancel out push 1:1 = 0
                return 26;
            case "BALANCE":     //WH - increases the amount of knock around from all sources and player experiences
            case "STURDY":      //OH - decreases the amount a player can be pushed or pulled on hit. decreases explosive knock back
                return 27;
            case "FRAGILE":    //OU - increases the amount on durability uses per hit
            case "SPLASH":     //EW - increases splash range on hit pos only //possibly OH
                return 28;
            case "RETENTION":  //OD - (on Death) lvl1 = weapon is saved it death & equipped. lvl2 weapon is always saved
            default:
                return -2;


        }
    }

    static String getName(int powerID, int powerLevel)
    {
        switch(powerID)
        {
            case -1:
                return powerLevel > 0 ? "POWER" : "BROKEN";
            case 0:
                return powerLevel > 0 ? "INFINTE" : "JAMMING";
            case 3:
                return powerLevel > 0 ? "YOUTH" : "DYING";
            case 4:
                return powerLevel > 0 ? "DAMAGE" : "FEEBLE";
            case 5:
                return powerLevel > 0 ? "PROTECTION" : "WEAKNESS";
            case 8:
                return powerLevel > 0 ? "BOOM" : "VOLATILE";
            case 12:
                return powerLevel > 0 ?  "SPEED" : "SLOW";
        }
        return "";
    }

    static String getPrefix(int powerID, int powerLevel)
    {
        switch(powerID)
        {

            case -1:
                return powerLevel > 0 ? "" : "BROKEN ";
            case 0:
                return powerLevel > 0 ? "INFINITE " : "";
            case 4:
                return powerLevel > 0 ? "" : "FEEBLE ";
            case 8:
                return powerLevel > 0 ? "" : "VOLATILE ";
            case 12:
                return powerLevel > 0 ? "CAFFEINATED " : "";
        }
        return "";
    }

    static String getSuffix(int powerID, int powerLevel)
    {

            switch(powerID)
            {
                case -1:
                    return powerLevel > 0 ? "OF POWER" : "";
                case 0:
                    return powerLevel > 0 ? "" : " OF JAMMING";
                case 3:
                    return powerLevel > 0 ? " OF YOUTH" : " OF DYING";
                case 4:
                    return powerLevel > 0 ? " OF DAMAGE" : "";
                case 5:
                    return powerLevel > 0 ? " OF PROTECTION" : " OF WEAKNESS";
                case 8:
                    return powerLevel > 0 ? " OF BOOM" : "";
                case 12:
                    return powerLevel > 0 ? "" : " OF THE TURTLE";
            }
            return "";
    }

    static void apply(int powerID, int powerLevel, Player player)
    {
        if(powerLevel !=0)
        {
            switch (powerID)
            {
                /*
                case 3:
                    if(powerLevel > 0)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1000000, powerLevel));
                    else
                        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000000, powerLevel * -50));
                    break;
                */
                case 4:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, powerLevel));
                    break;
                case 5:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, powerLevel));
                    break;
                case 12:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, powerLevel));
                    break;
                default:
            }
        }
    }

    static void enchant(int powerID, int powerLevel, ItemStack itemStack)
    {

        ItemMeta itemMeta = itemStack.getItemMeta();
        switch (powerID)
        {
            case 0:
                if (powerLevel > 0)
                {
                    if (itemMeta instanceof Damageable)
                    {
                        ((Damageable) itemMeta).setDamage(0);
                        itemMeta.setUnbreakable(true);
                    }
                }
                else
                    if(itemMeta != null){itemMeta.setUnbreakable(false);}
                itemStack.setItemMeta(itemMeta);
                break;

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
        //player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        //player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        //player.removePotionEffect(PotionEffectType.SPEED);

    }
}
