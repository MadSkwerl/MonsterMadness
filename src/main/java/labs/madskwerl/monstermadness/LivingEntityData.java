package labs.madskwerl.monstermadness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

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

    /* Power Inventory Variables */
    private ArrayList<ItemStack> powerUps;
    private ItemStack[] backupInventory;
    private ItemStack[] powerInventory;
    private int powerIndex;
    private int backupCursor;
    private long scrollTime;



    public LivingEntityData()
    {
        powerInventory = new ItemStack[9];
        powerUps = new ArrayList<>();
        powerUps.add(new ItemStack(Material.ACACIA_BOAT, 1));
        powerUps.add(new ItemStack(Material.COMPARATOR, 1));
        powerUps.add(new ItemStack(Material.DIAMOND_PICKAXE, 1));
        powerUps.add(new ItemStack(Material.BLACK_BANNER, 1));
        powerUps.add(new ItemStack(Material.GOLD_ORE, 10));
        powerUps.add(new ItemStack(Material.LIGHT_BLUE_BED, 1));
        powerUps.add(new ItemStack(Material.CHEST, 2));
        powerUps.add(new ItemStack(Material.LIGHT_GRAY_TERRACOTTA, 5));
        powerUps.add(new ItemStack(Material.FLINT, 11));
        powerUps.add(new ItemStack(Material.COAL_ORE, 3));
        powerUps.add(new ItemStack(Material.DARK_OAK_BOAT, 1));
        powerUps.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
        powerUps.add(new ItemStack(Material.BIRCH_SLAB, 1));
        powerUps.add(new ItemStack(Material.HOPPER, 1));
        powerUps.add(new ItemStack(Material.LAPIS_ORE, 13));
        powerUps.add(new ItemStack(Material.LIGHT_GRAY_CARPET, 2));
        powerUps.add(new ItemStack(Material.COD_BUCKET, 5));
        powerUps.add(new ItemStack(Material.BRICK, 17));
        updatePowerInventory();
    }


    private  void updatePowerInventory()
    {
        int powerSize = powerUps.size();
        for (int i = 0; i < 9; i++)
        {
            int j = (powerIndex + (i - 4))%powerSize;
            if(j<0)
                j+=powerSize;
            powerInventory[i] = getPowerUp(j);
        }
    }

    private void powerIndexPlus()
    {
        if (powerIndex == powerUps.size() - 1)
            powerIndex = 0;
        else
            powerIndex++;
    }
    private void powerIndexMinus()
    {
        if (powerIndex == 0)
            powerIndex = powerUps.size() - 1;
        else
            powerIndex--;
    }

    private void addPowerIndex(int dIndex)
    {
        int powerSize = powerUps.size();
        powerIndex=(powerIndex+dIndex)%powerSize;
        if(powerIndex<0)
            powerIndex+=powerSize;
    }
    public void scrollPowerInventory(int dIndex)
    {
        Player player = Bukkit.getPlayer(LivingEntityBank.getUUID(this));
       // if (System.currentTimeMillis() - scrollTime > 20)
       // {
            scrollTime = System.currentTimeMillis();
            addPowerIndex(dIndex);
            updatePowerInventory();
            player.getInventory().setContents(powerInventory);
            player.updateInventory();
        //}
    }

    public int getPowerIndex()
    {
        return powerIndex;
    }

    public ItemStack getPowerUp(int index)
    {
        return powerUps.get(index);
    }

    public void backupInventory(ItemStack[] backupInventory)
    {
        this.backupInventory = backupInventory;
    }

    public ItemStack[] getBackupInventory()
    {
        return backupInventory;
    }

    public void backupCursor(int backupCursor) {this.backupCursor = backupCursor; }

    public int getBackupCursor(){return backupCursor;}

    public void setPowerInventory(ItemStack[] powerInventory){this.powerInventory = powerInventory;}

    public ItemStack[] getPowerInventory(){return powerInventory;}

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
