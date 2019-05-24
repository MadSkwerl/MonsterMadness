package labs.madskwerl.monstermadness;

import org.bukkit.plugin.java.JavaPlugin;


public final class MonsterMadness extends JavaPlugin {

    WOPVault wopVault = new WOPVault();
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("WOP").setExecutor(new SpawnWeaponOfPowerCommand(wopVault));
        NSA nsa  = new NSA(this, wopVault);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
