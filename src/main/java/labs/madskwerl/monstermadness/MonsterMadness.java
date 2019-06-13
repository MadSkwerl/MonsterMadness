package labs.madskwerl.monstermadness;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class MonsterMadness extends JavaPlugin
{

    public double wopMonsterLevel = 0;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        //LivingEntityBank livingEntityBank = new LivingEntityBank();
        NSA nsa  = new NSA(this);
        //register commands
        this.getCommand("WOP").setExecutor(new SpawnWeaponOfPowerCommand(nsa));
        //populate PlayerBank & WOPVault
        for (Player player : this.getServer().getOnlinePlayers())
        {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.setHealthScale(20);
            LivingEntityData livingEntityData = new PlayerData(); //to be replaced with config file logic to create the livingEntityData object
            LivingEntityBank.addLivingEntityData(player.getUniqueId(), livingEntityData);
            nsa.initPlayer(player);
            wopMonsterLevel = (this.wopMonsterLevel + livingEntityData.getLevel())/2.0;
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
