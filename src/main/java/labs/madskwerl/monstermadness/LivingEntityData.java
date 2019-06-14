package labs.madskwerl.monstermadness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class LivingEntityData
{
    private int attackDelay = 100;
    private long lastAttackTime = 0;
    private long lastWOPRegenTime = 0;
    private double maxHP = 20;
    private boolean isRegenHealth = false;
    private int level = 1;
    private int baseATK = 10;
    private int baseDEF = 10;
    private boolean onInteractCanceled = false;
    private ItemStack invArtifact = null;
    private ItemStack chargesArtifact = null;
    private UUID uuid;

    /* Power Inventory Variables */
    private ArrayList<ItemStack> powerUps = new ArrayList<>();
    private ItemStack[] backupInventory, powerInventory = new ItemStack[9];
    private int backupCursor, powerCursor;
    private boolean inventoryIsSwapped;
    private int powerIndex;
    public enum Scroll {LEFT, RIGHT}

    public boolean inventoryIsSwapped(){return inventoryIsSwapped;}
    /* swaps to powerInventory */
    public void swapPowerInventory()
    {

        Player player = Bukkit.getPlayer(uuid);
        backupInventory = player.getInventory().getStorageContents();
        backupCursor = player.getInventory().getHeldItemSlot();
        updatePowerInventory();
        player.getInventory().setContents(powerInventory);
        player.getInventory().setHeldItemSlot(powerCursor);
        player.updateInventory();
        inventoryIsSwapped = false;
    }
    /* Swaps to the backed up inventory and cursor */
    public void swapMainInventory()
    {

        Player player = Bukkit.getPlayer(uuid);
        powerCursor = player.getInventory().getHeldItemSlot();
        player.getInventory().setContents(backupInventory);
        player.getInventory().setHeldItemSlot(backupCursor);
        player.updateInventory();
        inventoryIsSwapped = true;
    }
    /* Public method to changing powerInventory: scroll LEFT or RIGHT and updates powerInventory */
    public void scrollPowerInventory(Scroll direction)
    {
        switch(direction)
        {
            case LEFT:
                addPowerIndex(-7);
                break;
            case RIGHT:
                addPowerIndex(7);
                break;
        }
    }
    /* Updates the contents of the powerInventory based around the current powerCursor */
    private  void updatePowerInventory()
    {
        int powerSize = powerUps.size();
        if (powerSize > 0)
        {
            for (int i = 1; i < 8; i++)
            {
                int j = (powerIndex + (i - 4)) % powerSize;
                if (j < 0)
                    j += powerSize;
                powerInventory[i] = powerUps.get(j);
            }
        }
    }
    /* Changes the current powerIndex by adding the dIndex, result will modulus around the number of power ups */
    /* Finally, updates the powerInventory */
    private void addPowerIndex(int dIndex)
    {
        int powerSize = powerUps.size();
        powerIndex=(powerIndex+dIndex)%powerSize;
        if(powerIndex<0)
            powerIndex+=powerSize;
        updatePowerInventory();
    }

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

    public double getMaxHP()
    {
        return maxHP;
    }

    public void setMaxHP(double maxHP)
    {
        this.maxHP = maxHP;
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

    public boolean isOnInteractCanceled()
    {
        return onInteractCanceled;
    }

    public void setOnInteractCanceled(boolean onInteractCanceled)
    {
        this.onInteractCanceled = onInteractCanceled;
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

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }
}
