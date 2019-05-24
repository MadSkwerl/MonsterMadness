package labs.madskwerl.monstermadness;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class WOP  extends ItemStack
{
    private int powerID;
    private int powerLevel;
    private int powerSlots = 0;
    private int activeSlotNumber = 0;
    private final int baseSlots = 10;
    private int[] powerLevels = new int[30];
/*
    private int infiniteLevel = 0;
    private int ammoRegenLevel = 0;
    private int lifeStealLevel = 0;
    private int lifeRegenLevel = 0;
    private int damageLevel = 0;
    private int protectionLevel = 0;
    private int poisonLevel = 0;
    private int poisonProtectionLevel = 0;
    private int explosiveLevel = 0;
    private int explosiveProtectionLevel = 0;
    private int slowLevel = 0;
    private int slowProtectionLevel;
    private int speedBoostLevel = 0;
    private int fireChanceLevel = 0;
    private int fireProtectionLevel = 0;
    private int instagibChanceLevel = 0;
    private int instagibProtectionLevel = 0;
    private int stunChanceLevel = 0;
    private int stunProtectionLevel = 0;
    private int atkSpeedBoostLevel = 0;
    private int atkSpeedProtectionLevel = 0;
    private int jumpBoostLevel = 0;
    private int fallProtectionLevel = 0;
    private int charmChanceLevel = 0;
    private int invisibilityLevel = 0;
    private int knockBackLevel = 0;
    private int drawInLevel = 0;
    private int sturdyLevel = 0;
    private int splashLevel = 0;
    private int retentionLevel = 0;
*/
    public WOP(int powerID, int powerLevel, Material material, int amount)
    {
        super(material,amount);
        this.powerID = powerID;
        this.powerLevel = powerLevel;

        //If power is power
        if(this.powerID == -1 && this.powerLevel > -1)
            this.powerSlots  = this.baseSlots + this.powerLevel;
        else
            this.modPower(this.powerID, this.powerLevel);
        this.setDisplayName();
        this.updateLore();

        //note power will apply when main hand is switch to it
        this.applyEnchantPowers();
    }

    public void setDisplayName()
    {
        ItemMeta itemMeta = this.getItemMeta();
        if(itemMeta != null)
        {
            itemMeta.setDisplayName(Powers.getPrefix(this.powerID, this.powerLevel) + this.getType().name() + Powers.getSuffix(this.powerID, this.powerLevel));
            this.setItemMeta(itemMeta);
        }
    }

    public void updateLore()
    {
        ItemMeta itemMeta = this.getItemMeta();
        if(itemMeta != null)
        {
            List<String> loreList = new ArrayList<>();
            String lore;
            for (int i = 0; i < this.powerLevels.length; i++)
            {
                if (this.powerLevels[i] != 0)
                {
                    lore = Powers.getName(i, this.powerLevels[i]) + " " + this.powerLevels[i];
                    loreList.add(lore);
                }
            }
            itemMeta.setLore(loreList);
        }
        this.setItemMeta(itemMeta);
    }

    //converts a wop to power weapon
    public boolean makeSlotted(Player player)
    {
        if(this.powerID == -1 || this.powerLevel < 0)
            return false;
        else
        {
            //change powerID to power and update displayName
            this.powerID = 0;
            this.setDisplayName();

            //make powerSlots reduce all levels back to 0, apply powers to reset and update lore
            this.powerSlots = this.baseSlots + this.powerLevel;
            this.powerLevels = new int[this.powerLevels.length];
            this.applyPowers(player);
            this.applyEnchantPowers();
            this.updateLore();
            return true;
        }
    }

    public boolean addPowerUp(int powerID, Player player)
    {
        if(this.activeSlotNumber + 1 > this.powerSlots)
            return false;
        else
        {
            activeSlotNumber ++;
            this.modPower(powerID, Powers.getBaseMagnitude(powerID));
            this.applyPowers(player);
            return true;
        }
    }

    public boolean removePowerUps(Player player)
    {
        if(this.activeSlotNumber == 0)
            return false;
        else
        {
            //reset all
            this.powerLevels = new int[this.powerLevels.length];
            this.activeSlotNumber = 0;
            return true;
        }
    }

    //modifies power of weapon either by weapon properties or powerUps (if power weapon)
    public void modPower(int powerID, int powerLevel)
    {
        this.powerLevels[powerID] += powerLevel;
        this.updateLore();
    }

    //called on equip
    public void applyPowers(Player player)
    {
        for(int i = 0; i < this.powerLevels.length; i++)
            Powers.apply(i, this.powerLevels[i], player);
    }

    public void applyEnchantPowers()
    {
        for(int i = 0; i < this.powerLevels.length; i++)
            Powers.enchant(i, this.powerLevels[i], this);
    }
}
