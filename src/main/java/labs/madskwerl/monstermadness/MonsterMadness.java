package labs.madskwerl.monstermadness;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;


public final class MonsterMadness extends JavaPlugin
{

    public static double MONSTER_LEVEL_AVERAGE = 0;
    public static NSA NSA;
    public static MonsterMadness PLUGIN;
    public static Random RANDOM = new Random();

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        MonsterMadness.PLUGIN = this;
        MonsterMadness.NSA  = new NSA();
        //register commands
        this.getCommand("WOP").setExecutor(new SpawnWeaponOfPowerCommand());
        this.getCommand("KIT").setExecutor(new SpawnKitCommand());
        this.getCommand("PU").setExecutor(new SpawnPowerupCommand());
        //populate PlayerBank & WOPVault
        for (Player player : this.getServer().getOnlinePlayers())
        {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.setHealthScale(20);
            LivingEntityData livingEntityData = new PlayerData(player.getUniqueId()); //to be replaced with config file logic to create the livingEntityData object
            LivingEntityBank.addLivingEntityData(player.getUniqueId(), livingEntityData);
            NSA.initPlayer(player);
            MONSTER_LEVEL_AVERAGE = (MONSTER_LEVEL_AVERAGE + livingEntityData.getLevel())/2.0;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void addToMonsterLevelAverage(int level)
    {
        MONSTER_LEVEL_AVERAGE = (MONSTER_LEVEL_AVERAGE + level) / 2.0;
    }

    public void removeFromMonsterLevelAverage(int level)
    {   //removes level from average
        MONSTER_LEVEL_AVERAGE = MONSTER_LEVEL_AVERAGE * 2 - level;
    }

    public void recalculateMonsterLevelAverage()
    {
        int playerNum = 0;
        for (Player player : this.getServer().getOnlinePlayers())
        {
            MONSTER_LEVEL_AVERAGE += LivingEntityBank.getLivingEntityData(player.getUniqueId()).getLevel();
            playerNum ++;
        }

        if(playerNum == 0)
            MONSTER_LEVEL_AVERAGE = 1;
        else
            MONSTER_LEVEL_AVERAGE /= playerNum;
    }

}
