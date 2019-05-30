package labs.madskwerl.monstermadness;


public class PlayerData
{
    private String  uid;
    private int[]   currentPowerLevels = new int[Powers.NumberOfPowers];
    public  long interactTime = 0;

    int vampLevel;
    int lifeRegenLevel;         //Used by poison modifiers as well. ei poison = anti-regen
    int damageLevel;
    int defenseLevel;
    int poisonLevel;            //OnHit when pos, OnUse chance when neg
    int poisonDef;              //Chance to gain poison WhenHit when neg
    int poisonDefense;
    int moveSpeedLevel;
    int moveSpeedDefenceLevel;
    int moveSpeedOnHitLevel;
    int fireChanceOnHitLevel;   //Chance to catch fire OnUse when neg
    int fireDefenceLevel;
    int gibChanceOnHitLevel;
    int gibDefenceLevel;
    int stunChanceLevel;        //OnHit when pos, WhenHit when neg
    int stunDefense;            //Decreases chance WhenHit when pos. Increases time WhenHit neg
    int atkSpeedLevel;
    int atkSpeedDefence;
    int jumpLevel;
    int fallLevel;
    int charmLevel;
    int stealthLevel;
    int knockbackLevel;        //knockback OnHit when pos, pullin OnHit neg
    int knockbackDefenceLevel;
    int pullInDefenceLevel;
    int splashLevel;          //consumptionLevel OnUse when neg

    public PlayerData(String uid)
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

    public void updatePowers()
    {
        //updates on Equip powers
    }

}
