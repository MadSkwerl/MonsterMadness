package labs.madskwerl.monstermadness;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NSA implements Listener
{
    static boolean MobsDropPowerups = true;

    public NSA()
    {
        MonsterMadness.PLUGIN.getServer().getPluginManager().registerEvents(this, MonsterMadness.PLUGIN);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        //First check if the player is sneaking
        Player player = e.getPlayer();
        if(player.isSneaking())
        {
            int heldItemSlot = player.getInventory().getHeldItemSlot();
            if(heldItemSlot == 0)
                LivingEntityBank.getLivingEntityData(player.getUniqueId()).scrollPowerInventory(LivingEntityData.Scroll.LEFT);
            else if(heldItemSlot == 8)
                LivingEntityBank.getLivingEntityData(player.getUniqueId()).scrollPowerInventory(LivingEntityData.Scroll.RIGHT);
            e.setCancelled(true);
            return;
        }
        //Calls onUse for the specific wop
        Action action = e.getAction();
        String customName = player.getCustomName();
        if (customName == null)
            customName = "";
        if (customName.contains("WOP") && (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))
        {
            if (customName.contains("IRON_SWORD"))
                WOP_IRON_SWORD.onUse(e);
            else if (customName.contains("WOP_BOW"))
                WOP_BOW.onUse(e);
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
            WOP_EXPLOSION.onHit(e);
        else if (sourceCustomName.contains("IRON_SWORD"))
            WOP_IRON_SWORD.onHit(e);
        else if (sourceCustomName.contains("BOW"))
        {
            if (source instanceof Projectile)
                WOP_BOW.projectile_onHit(e);
            else
                WOP_BOW.onHit(e);
        }
        else //attacker is non-wop & non-explosive
        {
            if (defenderCustomName.contains("WOP"))
            {
                int protectionLevel = WOP.getPowerLevel(defenderCustomName, 5); //PowerID:5 = PROTECTION/WEAKNESS
                double protectionModifier = 1 - protectionLevel * .1;
                e.setDamage(e.getDamage() * protectionModifier);

                //handle defender hp regen
                LivingEntity livingEntity = (LivingEntity)defender;
                if(WOP.getPowerLevel(defenderCustomName, 3) > 0 && livingEntity.getHealth() - e.getFinalDamage() < livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                    new Regen_Health(livingEntity).runTaskLater(MonsterMadness.PLUGIN, 20);
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
        Player player = e.getPlayer();
        String localizedName = "";
        float speed = 0.2f;
        try
        {
            //set player customName to wop localizedName
            localizedName = player.getInventory().getItem(e.getNewSlot()).getItemMeta().getLocalizedName();
            //handle switching to a wop with health regen
            if (localizedName.contains("WOP"))
            {
                LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(e.getPlayer().getUniqueId());
                if (livingEntityData != null && !livingEntityData.isRegenHealth() && WOP.getPowerLevel(localizedName, 3) != 0)
                {
                    livingEntityData.setRegenHealth(true);
                    System.out.println("Regen HP Timer Started From ItemHeldEvent");
                    new Regen_Health(e.getPlayer()).runTaskLater(MonsterMadness.PLUGIN, 20);
                }
                int speedLevel = WOP.getPowerLevel(localizedName, 12);
                speed = speedLevel < 0 ? .2f + .02f * speedLevel : 0.2f + .05f * speedLevel;
            }
        } catch (Exception err)
        {
            System.out.println("ItemHeldEventError");
        }
        player.setCustomName(localizedName);
        player.setWalkSpeed(speed);
        new Delayed_RefreshChargesArtifact(e.getPlayer()).runTaskLater(MonsterMadness.PLUGIN, 1);
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

            LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(e.getPlayer().getUniqueId());
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
            LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(player.getUniqueId());
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
                    new Regen_Ammo_Primer(player).runTaskLater(MonsterMadness.PLUGIN, 1);
                } else if (WOP.getPowerLevel(player.getCustomName(), 3) != 0 && !livingEntityData.isRegenHealth()) //PowerID:3 = REGEN
                {
                    livingEntityData.setRegenHealth(true);
                    System.out.println("Regen HP Timer Started From ItemPickupEvent");
                    new Regen_Health(player).runTaskLater(MonsterMadness.PLUGIN, 20);
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
                    this.refreshChargesArtifact((Player)(MonsterMadness.PLUGIN.getServer().getEntity(livingEntityData.getUUID())));
                    new Regen_Ammo(itemStack, livingEntityData).runTaskLater(MonsterMadness.PLUGIN, 20);

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
        LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(player.getUniqueId());
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

    public void regenHealth(LivingEntity livingEntity)
    {
        LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(livingEntity.getUniqueId());
        if (livingEntityData != null)
        {
            try
            {
                String customName = livingEntity.getCustomName();
                double maxHP = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double currentHP = livingEntity.getHealth();
                int regenLevel = WOP.getPowerLevel(customName, 3); //PowerID:3 = REGEN
                if ((regenLevel > 0 && currentHP != maxHP) || (regenLevel < 0 && currentHP != 0.5))
                {
                    double newHP = currentHP + regenLevel;
                    if (newHP > maxHP)
                        newHP = maxHP;
                    else if (newHP < 0.5)
                        newHP = 0.5;
                    livingEntity.setHealth(newHP);
                    System.out.println("Regen HP started from timer");
                    new Regen_Health(livingEntity).runTaskLater(MonsterMadness.PLUGIN, 20);
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
        LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(player.getUniqueId());

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
                        new Regen_Ammo(itemStack, livingEntityData).runTaskLater(MonsterMadness.PLUGIN, 1);
                    }
                }
                else if(localizedName.contains("CHARGES_ARTIFACT"))
                {
                    livingEntityData.setChargesArtifact(itemStack);
                    refreshChargesArtifact(player);
                }

            }catch (Exception e){}
        }
        String customName = player.getCustomName();
        if (WOP.getPowerLevel(customName, 3) != 0 && !livingEntityData.isRegenHealth()) //PowerID:3 = REGEN
        {
            livingEntityData.setRegenHealth(true);
            System.out.println("Regen HP Timer Started From initPlayer");
            new Regen_Health(player).runTaskLater(MonsterMadness.PLUGIN, 20);
        }

        int speedLevel = WOP.getPowerLevel(customName, 12);
        float speed = speedLevel < 0 ? .2f + .02f * speedLevel : 0.2f + .05f * speedLevel;
        player.setWalkSpeed(speed);
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent e)
    {
        Player player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealthScale(20);
        LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(player.getUniqueId());
        if (livingEntityData == null)
            LivingEntityBank.addLivingEntityData(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        try
        {
            this.initPlayer(player);
        }catch(Exception err){}
    }

    @EventHandler
    public void onPlayerLogoffEvent(PlayerQuitEvent e)
    {
            LivingEntityBank.removeLivingEntityData(e.getPlayer().getUniqueId());
    }

    public void bindChargesArtifact(Player player)
    {
        //Scans player inventory and and binds the first found (should only be 1) artifact to the player LivingEntityData
        //refreshCharges Artifact is then called (b/c this should only be called when add a charge object to play inv)
        LivingEntityData livingEntityData  = LivingEntityBank.getLivingEntityData(player.getUniqueId());
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
        ItemStack chargesArtifact = LivingEntityBank.getLivingEntityData(player.getUniqueId()).getChargesArtifact();
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


    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e)
    {
        //Debug Code
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
        //Handles moving of Charges Artifact around inventory
        try
        {
            InventoryAction action = e.getAction();
            // If it is a pickup event
            if (action.equals(InventoryAction.PICKUP_ALL)  ||
                     action.equals(InventoryAction.PICKUP_HALF) ||
                     action.equals(InventoryAction.PICKUP_SOME) ||
                     action.equals(InventoryAction.PICKUP_ONE)  ||
                     action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) ||
                     action.equals(InventoryAction.SWAP_WITH_CURSOR))
            {
                //Reduce the number of charge to 1 to avoid splitting the stacks
                if (e.getCurrentItem().getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
                    e.getCurrentItem().setAmount(1);
                if(action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
                    new Delayed_BindChargesArtifact((Player)e.getWhoClicked()).runTaskLater(MonsterMadness.PLUGIN, 1);
            }
            else if(action.equals(InventoryAction.PLACE_ALL)    ||
                    action.equals(InventoryAction.PLACE_SOME)   ||
                    action.equals(InventoryAction.PLACE_ONE))
            {
                new Delayed_BindChargesArtifact((Player)e.getWhoClicked()).runTaskLater(MonsterMadness.PLUGIN, 1);
            }
            else if(action.equals(InventoryAction.HOTBAR_MOVE_AND_READD) || action.equals(InventoryAction.HOTBAR_SWAP))
            {
                e.setCancelled(true);
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
                new Delayed_BindChargesArtifact((Player)e.getWhoClicked()).runTaskLater(MonsterMadness.PLUGIN, 1);
        }catch (Exception err){}
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e)
    {
        try
        {
            if (e.getItemInHand().getItemMeta().getLocalizedName().contains("CHARGES_ARTIFACT"))
                e.setCancelled(true);
        }catch (Exception err){}
    }

    public void applyPoison(LivingEntityData led)
    {
        long currentTime = System.currentTimeMillis();
        for(int i = 5; i > -1; i--)
        {
            if(i == 0)
                led.setPoisoned(false);
            else if(led.getPoisonTime(i) > currentTime)
            {
                LivingEntity entity =(LivingEntity) MonsterMadness.PLUGIN.getServer().getEntity(led.getUUID());
                double newHealth = entity.getHealth() - i;
                if(newHealth < 0)
                {
                    newHealth = 0;
                    led.setPoisoned(false);
                    //onDeath handles removing led
                }
                entity.setHealth(newHealth);
                System.out.println(entity + "Poisoned-Level: " + i + ", HP: " + entity.getHealth());
                break;
            }
        }

        if(led.isPoisoned())
            new PoisonTimer(led).runTaskLater(MonsterMadness.PLUGIN, 10);
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e)
    {
        Player player = e.getPlayer();
        LivingEntityData playerData = LivingEntityBank.getLivingEntityData(player.getUniqueId());
        if(player.isSneaking())
            playerData.swapMainInventory();
        else
            playerData.swapPowerInventory();
    }

    //restricts drops
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e)
    {

        //TODO: IF THE WOP INV IS THE CURRENT HOW TO HANDLE THIS

        //Scan inv for charges artifact and set he respawn var for it.
        LivingEntityData playerLED = LivingEntityBank.getLivingEntityData(e.getEntity().getUniqueId());
        playerLED.setChargesArtifactSlotOnRespawn(-1); //clears charges artifact respawn
        int i = 0;
        for (ItemStack itemStack : e.getEntity().getInventory().getContents())
        {
            ItemMeta itemMeta = null;
            if(itemStack != null)
                itemMeta = itemStack.getItemMeta();
            if(itemMeta != null)
            {
                String localizedName = itemStack.getItemMeta().getLocalizedName();
                if(localizedName.contains("CHARGES_ARTIFACT"))
                    playerLED.setChargesArtifactSlotOnRespawn(i); //sets charges artifact respawn to inv number
            }
            i++;
        }

        //Prevent Inventory artifact and charges artifact from dropping
        List<ItemStack> drops = e.getDrops();
        for (ItemStack itemStack : drops)
        {
            ItemMeta itemMeta = null;
            if(itemStack != null)
                itemMeta = itemStack.getItemMeta();
            if(itemMeta != null)
            {
                String localizedName = itemStack.getItemMeta().getLocalizedName();
                if(localizedName.contains("INV_ARTIFACT") || localizedName.contains("CHARGES_ARTIFACT"))
                {
                    drops.remove(itemStack);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent e)
    {
        //if player had charges artifact respawn it in their inv
        Player player = e.getPlayer();
        LivingEntityData playerLED = LivingEntityBank.getLivingEntityData(player.getUniqueId());
        int chargesArtifactSlotOnRespawn = playerLED.getChargesArtifactSlotOnRespawn();
        if (chargesArtifactSlotOnRespawn > -1)
        {
            ItemStack itemStack = new ItemStack(Material.YELLOW_BANNER, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLocalizedName("CHARGES_ARTIFACT");
            itemMeta.setDisplayName("CHARGES ARTIFACT");
            ((BannerMeta)itemMeta).addPattern(new Pattern(DyeColor.GRAY, PatternType.FLOWER));
            itemStack.setItemMeta(itemMeta);
            LivingEntityBank.getLivingEntityData(player.getUniqueId()).setChargesArtifact(itemStack);
            player.getInventory().setItem(chargesArtifactSlotOnRespawn, itemStack);
            new Delayed_BindChargesArtifact(player).runTaskLater(MonsterMadness.PLUGIN, 1);
        }
    }

    public void spawnPowerUP(Location location, int powerupID)
    {
        location.getWorld().dropItemNaturally(location, Powers.generatePowerUp(powerupID));
    }

    public void spawnPowerUp(Inventory inventory, int powerupID)
    {
        inventory.addItem(Powers.generatePowerUp(powerupID));
    }

}
