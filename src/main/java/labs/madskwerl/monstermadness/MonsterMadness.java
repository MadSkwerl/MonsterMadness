package labs.madskwerl.monstermadness;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public final class MonsterMadness extends JavaPlugin {

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        PlayerBank playerBank = new PlayerBank();
        NSA nsa  = new NSA(this, playerBank);
        //register commands
        this.getCommand("WOP").setExecutor(new SpawnWeaponOfPowerCommand());
        //populate PlayerBank & WOPVault
        for (Player player : this.getServer().getOnlinePlayers())
        {
            playerBank.addPlayer(player.getName());
            for (ItemStack itemStack : player.getInventory().getContents())
            {
                //wopVault.addWOP(itemStack);
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
