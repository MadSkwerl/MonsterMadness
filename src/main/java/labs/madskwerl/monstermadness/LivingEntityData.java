package labs.madskwerl.monstermadness;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class LivingEntityData
{
    private int attackDelay = 100;
    private long lastAttackTime = 0;
    private long lastWOPRegenTime = 0;
    private boolean isRegenHealth = false;
    private boolean isPoisoned = false;
    private long[] poisonTime = new long[6]; // holds timer values for poison effect
    private int level = 1;
    private int baseATK = 10;
    private int baseDEF = 10;
    private ItemStack invArtifact = null;
    private ItemStack chargesArtifact = null;
    private UUID uuid;

    public LivingEntityData(UUID uuid)
    {
        this.uuid = uuid;
    }

    public int getAttackDelay()
    {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay)
    {
        this.attackDelay = attackDelay;
    }

    public long getLastAttackTime()
    {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime)
    {
        this.lastAttackTime = lastAttackTime;
    }

    public long getLastWOPRegenTime()
    {
        return lastWOPRegenTime;
    }

    public void setLastWOPRegenTime(long lastWOPRegenTime)
    {
        this.lastWOPRegenTime = lastWOPRegenTime;
    }

    public boolean isRegenHealth()
    {
        return isRegenHealth;
    }

    public void setRegenHealth(boolean regenHealth)
    {
        isRegenHealth = regenHealth;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getBaseATK()
    {
        return baseATK;
    }

    public void setBaseATK(int baseATK)
    {
        this.baseATK = baseATK;
    }

    public int getBaseDEF()
    {
        return baseDEF;
    }

    public void setBaseDEF(int baseDEF)
    {
        this.baseDEF = baseDEF;
    }

    public ItemStack getInvArtifact()
    {
        return invArtifact;
    }

    public void setInvArtifact(ItemStack invArtifact)
    {
        this.invArtifact = invArtifact;
    }

    public ItemStack getChargesArtifact()
    {
        return chargesArtifact;
    }

    public void setChargesArtifact(ItemStack chargesArtifact)
    {
        this.chargesArtifact = chargesArtifact;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
    }

    public void setPoisoned(boolean poisoned)
    {
        this.isPoisoned = poisoned;
    }

    public boolean isPoisoned()
    {
        return this.isPoisoned;
    }

    public void setPoisonTime(int poisonLevel, long time)
    {
        this.poisonTime[poisonLevel] = time;
    }

    public long getPoisonTime(int poisonLevel)
    {
        return this.poisonTime[poisonLevel];
    }
}
