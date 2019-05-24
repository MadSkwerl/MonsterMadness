package labs.madskwerl.monstermadness;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WOP
{
    private int uid;
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
    public WOP(ItemStack itemStack, int powerID, int powerLevel, int uid)
    {
        this.uid = uid;
        this.powerID = powerID;
        this.powerLevel = powerLevel;

        //If power is power
        if(this.powerID == -1 && this.powerLevel > -1)
        {
            this.powerSlots = this.baseSlots + this.powerLevel;
            this.updateLore(itemStack);
        }
        else
            this.modPower(itemStack, this.powerID, this.powerLevel);
        this.setDisplayName(itemStack);
        //note power will apply when main hand is switch to it
        this.applyEnchantPowers(itemStack);
    }

    public void setDisplayName(ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null)
        {
            itemMeta.setDisplayName(Powers.getPrefix(this.powerID, this.powerLevel) + itemStack.getType().name() + Powers.getSuffix(this.powerID, this.powerLevel));
            itemStack.setItemMeta(itemMeta);
        }
    }

    public void updateLore(ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
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
        itemStack.setItemMeta(itemMeta);
    }

    //converts a wop to power weapon
    public boolean makeSlotted(ItemStack itemStack, Player player)
    {
        if(this.powerID == -1 || this.powerLevel < 0)
            return false;
        else
        {
            //change powerID to power and update displayName
            this.powerID = 0;
            this.setDisplayName(itemStack);

            //make powerSlots reduce all levels back to 0, apply powers to reset and update lore
            this.powerSlots = this.baseSlots + this.powerLevel;
            this.powerLevels = new int[this.powerLevels.length];
            this.applyPowers(player);
            this.applyEnchantPowers(itemStack);
            this.updateLore(itemStack);
            return true;
        }
    }

    public boolean addPowerUp(ItemStack itemStack, int powerID, Player player)
    {
        if(this.activeSlotNumber + 1 > this.powerSlots)
            return false;
        else
        {
            activeSlotNumber ++;
            this.modPower(itemStack, powerID, Powers.getBaseMagnitude(powerID));
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
    public void modPower(ItemStack itemStack, int powerID, int powerLevel)
    {
        this.powerLevels[powerID] += powerLevel;
        this.updateLore(itemStack);
    }

    //called on equip
    public void applyPowers(Player player)
    {
        Powers.removePowers(player);
        for(int i = 0; i < this.powerLevels.length; i++)
            Powers.apply(i, this.powerLevels[i], player);
    }

    public void applyEnchantPowers(ItemStack itemStack)
    {
        for(int i = 0; i < this.powerLevels.length; i++)
            Powers.enchant(i, this.powerLevels[i], itemStack);
    }

    public int getUID()
    {
        return this.uid;
    }

    public void link(ItemStack itemStack)
    {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLocalizedName("WOP_" + this.uid);
        itemStack.setItemMeta(itemMeta);
    }
}
