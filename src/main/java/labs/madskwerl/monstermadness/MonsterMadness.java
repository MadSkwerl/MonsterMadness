package labs.madskwerl.monstermadness;

import org.bukkit.plugin.java.JavaPlugin;


public final class MonsterMadness extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("WOP").setExecutor(new SpawnWeaponofPowerCommand());
        NSA nsa  = new NSA(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
