package labs.madskwerl.monstermadness;


import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Random;

public class NSA implements Listener
{
    public MonsterMadness plugin;
    public Random random = new Random();
    public LivingEntityBank livingEntityBank;

    public NSA(MonsterMadness plugin, LivingEntityBank livingEntityBank)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.livingEntityBank = livingEntityBank;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Action action = e.getAction();
        String customName = e.getPlayer().getCustomName();
        if (customName == null)
            customName = "";
        if (customName.contains("WOP") && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))
        {
            if (customName.contains("IRON_SWORD"))
                WOP_IRON_SWORD.onUse(e, this.plugin, this);
            else if (customName.contains("WOP_BOW"))
                WOP_BOW.onUse(e, this.plugin, this);

        }

    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
    {
        Entity source = e.getDamager();
        String sourceCustomName  = source.getCustomName();
        if(sourceCustomName == null)
            sourceCustomName = "";


        Entity defender = e.getEntity();
        String defenderCustomName  = defender.getCustomName();
        if (defenderCustomName == null)
            defenderCustomName = "";

        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)&&(sourceCustomName.contains("WOP") || defenderCustomName.contains("WOP")))
            WOP_EXPLOSION.onHit(e, this.plugin, this);
        else if (sourceCustomName.contains("IRON_SWORD"))
            WOP_IRON_SWORD.onHit(e, this.plugin, this);
        else if (sourceCustomName.contains("BOW"))
        {
            if (source instanceof Projectile)
                WOP_BOW.projectile_onHit(e, this.plugin, this);
            else
                WOP_BOW.onHit(e, this.plugin, this);
        }
        else //attacker is non-wop & non-explosive
        {
            if (defenderCustomName.contains("WOP"))
            {
                int protectionLevel = WOP.getPowerLevel(defenderCustomName, 5); //PowerID:5 = PROTECTION/WEAKNESS
                double protectionModifier = 1 - protectionLevel * .1;
                e.setDamage(e.getDamage() * protectionModifier);
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent e)
    {
        //================= Cancel Durability Loss ===========================
        try
        {
            if (WOP.isWOP(e.getItem()))//if the damaged item is a WOP
                e.setCancelled(true);//cancel the durability loss
        } catch (Exception err)
        {
            System.out.println("Error onPlayerItemDamageEvent");
        }
        //======= End Durability Loss ====
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e)
    {
        try
        {
            //set player customName to wop localizedName
            Player player = e.getPlayer();
            String localizedName = player.getInventory().getItem(e.getNewSlot()).getItemMeta().getLocalizedName();
            player.setCustomName(localizedName);

            //handle switching to a wop with health regen
            if (localizedName.contains("WOP"))
            {
                LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(e.getPlayer().getUniqueId());
                int regenLevel = WOP.getPowerLevel(localizedName, 3); //PowerID:3=REGEN
                if (livingEntityData != null && !livingEntityData.isRegenHealth() && regenLevel != 0)
                {
                    livingEntityData.setRegenHealth(true);
                    System.out.println("Regen HP Timer Started From ItemHeldEvent");
                    new Regen_Health(this, e.getPlayer()).runTaskLater(this.plugin, 20);
                }
            }
        } catch (Exception err)
        {
            System.out.println("ItemHeldEventError");
        }
        new Delayed_RefreshChargesArtifact(this, e.getPlayer()).runTaskLater(this.plugin, 1);
    }


    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e)
    {
        try
        {
            ItemMeta itemMeta = e.getItemDrop().getItemStack().getItemMeta();
            String localizedName = null;
            if(itemMeta != null)
                localizedName = itemMeta.getLocalizedName();
            if(localizedName == null)
                localizedName = "";

            LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(e.getPlayer().getUniqueId());
            if(localizedName.contains("CHARGES_ARTIFACT"))
            {
                e.getItemDrop().remove();
                livingEntityData.getChargesArtifact().setAmount(0);
                livingEntityData.setChargesArtifact(null);
                e.getPlayer().updateInventory();
            }
            else if(localizedName.contains("INV_ARTIFACT"))
            {
                e.getItemDrop().remove();
                livingEntityData.getChargesArtifact().setAmount(0);
                livingEntityData.setChargesArtifact(null);
            }

            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR))
                e.getPlayer().setCustomName("");
        }catch (Exception err){System.out.println("DroppedItemException");}
    }


    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e)
    {
        try
        {
            Player player = (Player) e.getEntity();
            LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
            ItemStack itemStack = e.getItem().getItemStack(); //Object is a copy of what will be put in the players inventory
            String localizedName = itemStack.getItemMeta().getLocalizedName();

            if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR))
                player.setCustomName(localizedName);

            if (WOP.isWOP(itemStack))
            {
                if (WOP.getPowerLevel(localizedName, 1) != 0) //PowerID:1 = AMMO_REGEN
                {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setLocalizedName(itemMeta.getLocalizedName() + "Ammo_Regen");//add temporary tag for primer
                    itemStack.setItemMeta(itemMeta);
                    new Regen_Ammo_Primer(this, player).runTaskLater(this.plugin, 1);
                } else if (WOP.getPowerLevel(player.getCustomName(), 3) != 0 && !livingEntityData.isRegenHealth()) //PowerID:3 = REGEN
                {
                    livingEntityData.setRegenHealth(true);
                    System.out.println("Regen HP Timer Started From ItemPickupEvent");
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
                }
            }

        } catch (Exception err)
        {
            System.out.println("PickEventError");
        }
    }


    //called by regen_ammo BukkitRunnable (initially onPlayerInteract, recursively through regenAmmo)
    public void regenAmmo(ItemStack itemStack, LivingEntityData livingEntityData)
    {
        try
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String localizedName = itemMeta.getLocalizedName();
            Damageable damageable = (Damageable) itemMeta;
            if (itemStack.getAmount() > 0)//if item exists
            {
                int powerLevel = WOP.getPowerLevel(localizedName, 1);//PowerID:1 = AMMO REGEN
                int maxDamage = WOP.getMaxDurability(localizedName);//check max damage
                int currentDamage = damageable.getDamage();            //and current damage
                boolean hasRegenAndIsDamaged = (powerLevel > 0 && currentDamage > 0);
                boolean hasRobbingAndIsNotFullyDamaged = (powerLevel < 0 && currentDamage < maxDamage - 1);
                if (hasRegenAndIsDamaged || hasRobbingAndIsNotFullyDamaged) //isDamaged(for regen) or isNotFullyDamaged(for robbing)
                {
                    int newDamage = currentDamage - powerLevel;
                    if (newDamage < 0)
                        newDamage = 0;//catches underflow of durability (going over 100%)
                    if (newDamage > maxDamage - 1)
                        newDamage = maxDamage - 1;//and durability from going to 0%

                    livingEntityData.setLastWOPRegenTime(System.currentTimeMillis());//timestamp to prevent creating too many regen_ammo tasks
                    damageable.setDamage(newDamage);
                    itemStack.setItemMeta(itemMeta);
                    this.refreshChargesArtifact((Player)(this.plugin.getServer().getEntity(livingEntityData.getUuid())));
                    new Regen_Ammo(this, itemStack, livingEntityData).runTaskLater(this.plugin, 20);

                    System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                    System.out.println("Regen Timer Started From Timer.");
                }

            }
        } catch (Exception e)
        {
            System.out.println("Regen timer fail");
        }
    }

    public void fireRegenAmmo(Player player)
    {
        LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
        for (ItemStack itemStack : player.getInventory())
        {
            ItemMeta itemMeta = itemStack.getItemMeta();
            String localizedName = "";
            if (itemMeta != null)
                localizedName = itemMeta.getLocalizedName();
            if (localizedName.contains("Ammo_Regen"))
            {
                itemMeta.setLocalizedName(localizedName.replace("Ammo_Regen", ""));//remove temp tag
                itemStack.setItemMeta(itemMeta);
                this.regenAmmo(itemStack, livingEntityData);
                break;
            }
        }
    }

    public void regenHealth(Player player)
    {
        LivingEntityData livingEntityData = livingEntityBank.getLivingEntityData(player.getUniqueId());
        if (livingEntityData != null)
        {
            try
            {
                String customName = player.getCustomName();
                double maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double currentHP = player.getHealth();
                int regenLevel = WOP.getPowerLevel(customName, 3); //PowerID:3 = REGEN
                if ((regenLevel > 0 && currentHP != maxHP) || (regenLevel < 0 && currentHP != 0.5))
                {
                    double newHP = currentHP + regenLevel;
                    if (newHP > maxHP)
                        newHP = maxHP;
                    else if (newHP < 0.5)
                        newHP = 0.5;
                    player.setHealth(newHP);
                    System.out.println("Regen HP started from timer");
                    new Regen_Health(this, player).runTaskLater(this.plugin, 20);
                } else
                    livingEntityData.setRegenHealth(false);
            } catch (Exception e)
            {
                livingEntityData.setRegenHealth(false);
            }
        }
    }

    public void initPlayer(Player player)
    {
        //Regen_Ammo init
        LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());

        try
        {
            player.setCustomName(player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName());
        }catch (Exception e)
        {
            System.out.println("initPlayer: CustomName not set");
            player.setCustomName("");
        }

        for(ItemStack itemStack : player.getInventory().getContents())
        {
            try
            {
                String localizedName = itemStack.getItemMeta().getLocalizedName();
                if (WOP.isWOP(itemStack))
                {
                    if (WOP.getPowerLevel(localizedName, 1) != 0) //PowerID:1 = AMMO_REGEN
                    {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setLocalizedName(itemMeta.getLocalizedName() + "Ammo_Regen");//add temporary tag for primer
                        itemStack.setItemMeta(itemMeta);
                        new Regen_Ammo(this, itemStack, livingEntityData).runTaskLater(this.plugin, 1);
                    }
                }
                else if(localizedName.contains("CHARGES_ARTIFACT"))
                {
                    livingEntityData.setChargesArtifact(itemStack);
                    refreshChargesArtifact(player);
                }

            }catch (Exception e){}
        }

        if (WOP.getPowerLevel(player.getCustomName(), 3) != 0 && !livingEntityData.isRegenHealth()) //PowerID:3 = REGEN
        {
            livingEntityData.setRegenHealth(true);
            System.out.println("Regen HP Timer Started From initPlayer");
            new Regen_Health(this, player).runTaskLater(this.plugin, 20);
        }

    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e)
    {
        Player player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealthScale(20);
        LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
        if (livingEntityData == null)
            livingEntityBank.addLivingEntityData(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        try
        {
            this.initPlayer(player);
        }catch(Exception err){}
    }

    @EventHandler
    public void onPlayerLogoffEvent(PlayerQuitEvent e)
    {
            this.livingEntityBank.removeLivingEntityData(e.getPlayer().getUniqueId());
    }

    public void bindChargesArtifact(Player player)
    {
        //Scans player inventory and and binds the first found (should only be 1) artifact to the player LivingEntityData
        //refreshCharges Artifact is then called (b/c this should only be called when add a charge object to play inv)
        LivingEntityData livingEntityData  = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
        for(ItemStack itemStack : player.getInventory().getContents())
        {
            if(itemStack != null)
            {
                ItemMeta itemMeta = itemStack.getItemMeta();
                String localizedNamed;
                if (itemMeta != null)
                    localizedNamed = itemMeta.getLocalizedName();
                else
                    localizedNamed = "";

                if (localizedNamed.contains("CHARGES_ARTIFACT"))
                {
                    livingEntityData.setChargesArtifact(itemStack);
                    break;
                }
            }
        }
        this.refreshChargesArtifact(player);
    }

    public void refreshChargesArtifact(Player player)
    {
        ItemStack chargesArtifact = this.livingEntityBank.getLivingEntityData(player.getUniqueId()).getChargesArtifact();
        //return if player is crouching or does not have charges artifact
        if(player.isSneaking() || chargesArtifact == null)
            return;

        ItemStack itemStackInMainHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMetaInMainHand = itemStackInMainHand.getItemMeta();
        String localizedName;
        if(itemMetaInMainHand == null)
            localizedName = "";
        else
            localizedName = itemMetaInMainHand.getLocalizedName();

        Material baseColor = Material.BLACK_BANNER;
        int charges = 1;

        if(localizedName.contains("WOP"))
        {
            //calc charges
            int currentDamage = ((Damageable) itemMetaInMainHand).getDamage();
            int maxDamage = WOP.getMaxDurability(localizedName);
            charges = maxDamage - currentDamage;

            //determine color, base on range of 100 charges
            if(charges > 75) //Green:100-76 Yellow:75-51 Orange:50-26 Red:25-1 Black:0
            {
                baseColor = Material.LIME_BANNER;
            }
            else if(charges > 50)
            {
                baseColor = Material.YELLOW_BANNER;
            }
            else if(charges > 25)
            {
                baseColor = Material.ORANGE_BANNER;
            }
            else if(charges > 0)
            {
                baseColor = Material.RED_BANNER;
            }
            else
                baseColor = Material.BLACK_BANNER;

            //handle under and over flow conditions
            if(charges > 100)
                charges = 100;
            else if(charges < 1)
                charges = 1;
        }
        //set amount and color
        chargesArtifact.setAmount(charges);
        chargesArtifact.setType(baseColor);
        player.updateInventory();
    }

    public void cleanChargesArtifact(Player player, int oldSlot, int newSlot)
    { //called primarily from onInventoryClickEvent when HotBar swap is used
        ItemStack newSlotItemStack = player.getInventory().getItem(newSlot);
        try
        {
            if(newSlotItemStack.getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
            {
                this.livingEntityBank.getLivingEntityData(player.getUniqueId()).setChargesArtifact(newSlotItemStack);
                this.refreshChargesArtifact(player);
                player.getInventory().getItem(oldSlot).setAmount(0);
                player.updateInventory();
            }
        }catch(Exception e){}
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e)
    {
        System.out.println("Inventory Click Event");
        System.out.println("Action: " + e.getAction());
        System.out.println("Click: " + e.getClick());
        System.out.println("Clicked Inventory: " + e.getClickedInventory());
        System.out.println("Current Item: " + e.getCurrentItem());
        System.out.println("Cursor: " + e.getCursor());
        System.out.println("Handlers: " + e.getHandlers());
        System.out.println("Hotbar Button: " + e.getHotbarButton());
        System.out.println("Raw Slot: " + e.getRawSlot());
        System.out.println("Slot: " + e.getSlot());
        try
        {
            InventoryAction action = e.getAction();
            if (action.equals(InventoryAction.PICKUP_ALL)  ||
                     action.equals(InventoryAction.PICKUP_HALF) ||
                     action.equals(InventoryAction.PICKUP_SOME) ||
                     action.equals(InventoryAction.PICKUP_ONE)  ||
                     action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
            {
                if (e.getCurrentItem().getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
                    e.getCurrentItem().setAmount(1);
                if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
                    new Delayed_BindChargesArtifact(this, (Player)e.getWhoClicked()).runTaskLater(this.plugin, 1);
            }
            else if(action.equals(InventoryAction.PLACE_ALL)    ||
                    action.equals(InventoryAction.PLACE_SOME)   ||
                    action.equals(InventoryAction.PLACE_ONE))
            {
                new Delayed_BindChargesArtifact(this, (Player)e.getWhoClicked()).runTaskLater(this.plugin, 1);
            }
            else if(action.equals(InventoryAction.HOTBAR_MOVE_AND_READD) || action.equals(InventoryAction.HOTBAR_SWAP))
            {
                ItemStack slotItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                if(slotItem.getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
                {
                    slotItem.setAmount(1);
                    new Delayed_CleanChargesArtifact(this, (Player)e.getWhoClicked(), e.getHotbarButton(), e.getSlot()).runTaskLater(this.plugin, 1);
                }
            }

        }catch (Exception err){}
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e)
    {
        System.out.println("Inventory Drag Event");
        System.out.println("Cursor: " + e.getCursor());
        System.out.println("Handlers: " + e.getHandlers());
        System.out.println("Inventory Slots: " + e.getInventorySlots());
        System.out.println("New Items: " + e.getNewItems());
        System.out.println("Old Cursor: " + e.getOldCursor());
        System.out.println("Raw Slots: " + e.getRawSlots());
        System.out.println("Type: " + e.getType());
        System.out.println("Who: " + e.getWhoClicked());
        try
        {
            if(e.getOldCursor().getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
                new Delayed_BindChargesArtifact(this, (Player)e.getWhoClicked()).runTaskLater(this.plugin, 1);
        }catch (Exception err){}
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e)
    {
        Player player = e.getPlayer();
        LivingEntityData playerData = livingEntityBank.getLivingEntityData(player.getUniqueId());
        if(player.isSneaking())
            playerData.swapMainInventory();
        else
            playerData.swapPowerInventory();
    }
}
