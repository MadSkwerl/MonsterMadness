package labs.madskwerl.monstermadness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LivingEntityData
{

    private final int POWER_INDEX_MAX = 3;
    private int attackDelay = 100;
    private long lastAttackTime = 0;
    private long lastWOPRegenTime = 0;
    private double maxHP = 20;
    private boolean isRegenHealth = false;
    private int level = 1;
    private int baseATK = 10;
    private int baseDEF = 10;
    private boolean onInteractCanceled = false;
    private ItemStack[][] powerUps = new ItemStack[POWER_INDEX_MAX][9];
    private int powerIndex;
    private ItemStack[] backupInventory;

    public LivingEntityData()
    {
        ItemStack[] stackOne = new ItemStack[9];
        stackOne[0] = new ItemStack(Material.ACACIA_BOAT, 1);
        stackOne[1] = new ItemStack(Material.COMPARATOR, 1);
        stackOne[2] = new ItemStack(Material.DIAMOND_PICKAXE, 1);
        stackOne[3] = new ItemStack(Material.BLACK_BANNER, 1);
        stackOne[4] = new ItemStack(Material.GOLD_ORE, 10);
        stackOne[5] = new ItemStack(Material.LIGHT_BLUE_BED, 1);
        stackOne[6] = new ItemStack(Material.CHEST, 2);
        stackOne[7] = new ItemStack(Material.LIGHT_GRAY_TERRACOTTA, 5);
        stackOne[8] = new ItemStack(Material.FLINT, 11);
        ItemStack[] stackTwo = new ItemStack[9];
        stackTwo[0] = new ItemStack(Material.COAL_ORE, 3);
        stackTwo[1] = new ItemStack(Material.DARK_OAK_BOAT, 1);
        stackTwo[2] = new ItemStack(Material.HEART_OF_THE_SEA, 1);
        stackTwo[3] = new ItemStack(Material.BIRCH_SLAB, 1);
        stackTwo[4] = new ItemStack(Material.HOPPER, 1);
        stackTwo[5] = new ItemStack(Material.LAPIS_ORE, 13);
        stackTwo[6] = new ItemStack(Material.LIGHT_GRAY_CARPET, 2);
        stackTwo[7] = new ItemStack(Material.COD_BUCKET, 5);
        stackTwo[8] = new ItemStack(Material.BRICK, 17);
        powerUps[1] = stackOne;
        powerUps[2] = stackTwo;
    }

    public int getPowerIndex()
    {
        return powerIndex;
    }
    public void powerIndexPlus()
    {
        if (powerIndex == POWER_INDEX_MAX - 1)
            powerIndex = 0;
        else
            powerIndex++;
    }
    public void powerIndexMinus()
    {
        if (powerIndex == 0)
            powerIndex = POWER_INDEX_MAX - 1;
        else
            powerIndex--;
    }
    public ItemStack[] getPowerUps(int index)
    {
        return powerUps[index];
    }

    public void backupInventory(ItemStack[] backupInventory)
    {
        this.backupInventory = backupInventory;
    }

    public ItemStack[] getBackupInventory()
    {
        return backupInventory;
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


}
