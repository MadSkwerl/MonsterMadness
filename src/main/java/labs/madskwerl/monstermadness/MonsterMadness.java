package labs.madskwerl.monstermadness;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public final class MonsterMadness extends JavaPlugin {

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        LivingEntityBank livingEntityBank = new LivingEntityBank();
        NSA nsa  = new NSA(this, livingEntityBank);
        //register commands
        this.getCommand("WOP").setExecutor(new SpawnWeaponOfPowerCommand());
        //populate PlayerBank & WOPVault
        for (Player player : this.getServer().getOnlinePlayers())
        {
            LivingEntityData livingEntityData = new PlayerData(); //to be replaced with config file logic to create the livingEntityData object
            livingEntityBank.addLivingEntityData(player.getUniqueId(), livingEntityData);
            player.setCustomName("WOP_" + player.getUniqueId());
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
