package labs.madskwerl.monstermadness;


public class PlayerData extends LivingEntityData
{
    public PlayerData()
    {
        super();
        this.setBaseATK(1);
        this.setBaseDEF(1);
        this.setAttackDelay(100);
        this.setMaxHP(20);
    }
}
