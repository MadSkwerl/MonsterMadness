package labs.madskwerl.monstermadness;


import java.util.UUID;

public class PlayerData extends LivingEntityData
{
    public PlayerData(UUID uuid)
    {
        super(uuid);
        this.setBaseATK(1);
        this.setBaseDEF(1);
        this.setAttackDelay(100);
    }
}
