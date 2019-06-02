package labs.madskwerl.monstermadness;

public class MobData
{
    private String  uid;
    private int[]   currentPowerLevels = new int[Powers.NumberOfPowers];
    public  int attackDelay = 100;
    public long lastAttackTime = 0;
    public long lastWOPRegenTime = 0;
    public double maxHP = 20;
    public boolean isRegenHealth = false;
    public int level = 1;
    public int baseATK = 10;
    public int baseDEF = 10;

    public MobData(String uid)
    {
        this.uid  = uid;
    }

    public String getUID()
    {
        return this.uid;
    }

    public void setPowerLevels(int[] powerLevels)
    {
        if(powerLevels.length == this.currentPowerLevels.length)
            this.currentPowerLevels = powerLevels;
    }

}
