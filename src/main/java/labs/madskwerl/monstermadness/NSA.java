package labs.madskwerl.monstermadness;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NSA implements Listener
{
    private MonsterMadness plugin;
    private Random random = new Random();
    private LivingEntityBank livingEntityBank;

    public NSA(MonsterMadness plugin, LivingEntityBank livingEntityBank)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.livingEntityBank = livingEntityBank;

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        String customName = player.getCustomName();
        Block blockClicked = e.getClickedBlock();
        Action action = e.getAction();
        // If the player left clicks
        if ((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)))
        {
            ItemStack itemStackInMainHand = e.getItem();
            if (!WOP.isWOP(itemStackInMainHand))
                return;

            ItemMeta itemMeta = itemStackInMainHand.getItemMeta();

            try //to handle null pointer exceptions
            {
                //============================== Interact: Cool-Down/Durability =======================================
                long currentTime = System.currentTimeMillis();
                LivingEntityData livingEntityData = this.livingEntityBank.getLivingEntityData(player.getUniqueId());
                if ((currentTime - livingEntityData.getLastAttackTime()) > livingEntityData.getAttackDelay() &&//if player's cool-down is finished
                        (currentTime - livingEntityData.getLastWOPRegenTime()) > 100)//and 100ms since last WOP regen (to prevent making too many)
                {
                    livingEntityData.setLastAttackTime(currentTime);//then reset player cool-down period

                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = WOP.getPowerLevel(customName, 28); //PowerID:28 = FRAGILE
                    int damageMultiplier = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                    int maxDamage = WOP.getMaxDurability(customName);
                    int currentDamage = damageable.getDamage();
                    int newDamage = currentDamage + (5 + damageMultiplier * 5);

                    if (newDamage >= maxDamage)//if durability is too low
                    {
                        e.setCancelled(true);//cancel the event
                        livingEntityData.setOnInteractCanceled(true);
                        return;
                    } else //otherwise, remove durability (ammo)
                    {
                        System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                        damageable.setDamage(newDamage);

                        itemStackInMainHand.setItemMeta(itemMeta);

                        //===================================== Interact: Regen ===========================================
                        //only start a new regen ammo recursion if current damage is 0 and item has appropriate lore
                        if (currentDamage == 0 && WOP.getPowerLevel(customName, 1) != 0) //PowerID:3 = REGEN
                        {
                            System.out.println("Regen Timer Started From Player Interact.");
                            new Regen_Ammo(this, itemStackInMainHand, livingEntityData).runTaskLater(this.plugin, 20);
                        }
                    }
                    //======= Regen ====

                    //==================================== Interact: Jamming ==========================================
                    //roll for power, will either be jamming or volatile, not both
                    int roll = this.random.nextInt(5);
                    //Handle Jamming power
                    if (WOP.getPowerLevel(customName, 0) * -1 > roll)//PowerID:0 = JAMMING
                    {
                        System.out.println("InteractCanceled");
                        e.setCancelled(true);
                        return;
                    }
                    //======== End Jamming ====
                    if (blockClicked == null)
                        return;
                    //================================= Interact: Volatile/Boom =======================================
                    Location location = null;
                    int boomLevel = WOP.getPowerLevel(customName, 8);
                    if (boomLevel * -1 > roll) //PowerID:8 = volatile/boom. * -1 inverts the neg to pos
                        location = player.getLocation(); //explode on player
                    else if (boomLevel > roll)
                        location = blockClicked.getLocation(); //explode where the player is looking
                    //note this only handles melee atm
                    if (location != null)
                    {
                        Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                        fireball.setCustomName(customName + ":" + player.getUniqueId()); //provides way to track entity
                        fireball.setYield(2);
                        fireball.setIsIncendiary(false);
                        fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
                    }
                    //======= End Volatile/Boom ====
                }
                //========= End Cool-Down =======
            } catch (Exception err)
            {
                System.out.println("InteractErrorCaught");
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e)
    {
        //debug stuff atm
        System.out.println(e.getEntity() + " was hit by " + e.getCause() + " for " + e.getDamage() + ". Orig: " + e.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE));
        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
        {
            //e.setDamage(1.0);
            //e.setCancelled(true);
            //EntityDamageEvent event = new EntityDamageEvent(e.getEntity(), EntityDamageEvent.DamageCause.CUSTOM, e.getDamage());
            //this.plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
    {
        try
        {
            //locate attacker from source
            Entity source = e.getDamager();
            EntityDamageEvent.DamageCause cause = e.getCause();


            Entity attacker;
            String attackerCustomName;
            String sourceCustomName = source.getCustomName()==null?"":source.getCustomName();
            if (source.getType().equals(EntityType.FIREBALL))
            {
                if (sourceCustomName.contains("WOP"))
                    attacker = this.plugin.getServer().getEntity(UUID.fromString(sourceCustomName.split(":")[Powers.NumberOfPowers + 2]));
                else
                    attacker = source;
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE))
            {
                if (sourceCustomName.contains("WOP"))
                {
                    ProjectileSource shooter = ((Projectile) source).getShooter();
                    if (shooter instanceof Entity)
                        attacker = (Entity) shooter;
                    else
                        attacker = source;
                } else
                    attacker = source;
            } else
                attacker = source;
            attackerCustomName = attacker.getCustomName();

            //locate defender
            Entity defender = e.getEntity();
            String defenderCustomName = defender.getCustomName();

            if (attackerCustomName == null)
                attackerCustomName = "";
            if (defenderCustomName == null)
                defenderCustomName = "";

            //================================= Cancellation Block ======================================\
            //Note: additional cancel condition below that is not in cancellation block
            boolean bothPlayers = (attacker instanceof Player && defender instanceof Player);
            boolean bothNotPlayers = !(attacker instanceof Player) && !(defender instanceof Player);
            boolean oneIsWOP = attackerCustomName.contains("WOP") || defenderCustomName.contains("WOP");
            boolean isBowAttack = WOP.isWOP(attackerCustomName) && WOP.getType(attackerCustomName).equals("BOW") && cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
            if (bothPlayers || (bothNotPlayers && oneIsWOP) || isBowAttack)
            {
                e.setCancelled(true);
                return;
            }

            //================================= Damage Application Block ======================================
            int attackerLevel;
            int defenderLevel;
            if (attacker instanceof Player)
            {
                LivingEntityData attackerLED = this.livingEntityBank.getLivingEntityData(attacker.getUniqueId());

                //Is set in on interact when there is not enough ammo
                if (attackerLED.isOnInteractCanceled())
                {
                    e.setCancelled(true);
                    attackerLED.setOnInteractCanceled(false);
                    return;
                }

                attackerLevel = attackerLED.getLevel();
                if (defenderCustomName.contains("WOP"))
                    defenderLevel = (int) this.plugin.wopMonsterLevel;
                else
                    defenderLevel = this.livingEntityBank.getLivingEntityData(attacker.getUniqueId()).getLevel();
            } else
            {
                defenderLevel = this.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
                if (attackerCustomName.contains("WOP"))
                    attackerLevel = (int) this.plugin.wopMonsterLevel;
                else
                    attackerLevel = this.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
            }

            double levelRatioModifier = 1 + (attackerLevel - defenderLevel) * 0.01;

            int protectionLevel = !defenderCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(defenderCustomName, 5); //PowerID:5 = PROTECTION/WEAKNESS
            double protectionModifier = 1 - protectionLevel * .1;
            if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            {
                int explosiveProtectionLevel = !defenderCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(defenderCustomName, 9); //PowerID:9 = BLAST PRUF/CRUMBLE
                double explosiveProtection = 1 - explosiveProtectionLevel * 0.15;
                if ((explosiveProtectionLevel > 0 && protectionLevel < 0) || (explosiveProtectionLevel < 0 && protectionLevel > 0))
                    protectionModifier += explosiveProtection;
                else
                    protectionModifier = (protectionLevel > explosiveProtectionLevel) ? protectionLevel : explosiveProtection;
            }

            int damageLevel = !attackerCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(attackerCustomName, 4); //PowerID:9 = BLAST PRUF/CRUMBLE
            double damageIncreaseModifier = 1 + damageLevel * 0.1;

            int wopBaseDamage = 0;
            if (attackerCustomName.contains("WOP") && defenderCustomName.contains("WOP"))
            {
                int attackerBase = this.livingEntityBank.getLivingEntityData(attacker.getUniqueId()).getBaseATK();
                int defenderBase = this.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getBaseDEF();
                wopBaseDamage = attackerBase - defenderBase;
            }

            double damage = wopBaseDamage + e.getDamage() * levelRatioModifier * protectionModifier * damageIncreaseModifier;
            System.out.println(attacker.getName() + " dealt " + damage + " damage to " + defender.getName());


            //================================= Entity vs Entity: Boom ======================================

            int roll = this.random.nextInt(5);
            try
            {
                Location location = null;
                if (WOP.getPowerLevel(attackerCustomName, 8) > roll)//PowerID:8 = BOOM
                    location = defender.getLocation(); //explode where the player is looking
                //note this only handles melee atm
                Fireball fireball = (Fireball) attacker.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
                fireball.setCustomName(attackerCustomName + attacker.getUniqueId()); //provides way to track entity
                fireball.setYield(2);
                fireball.setIsIncendiary(false);
                fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
            } catch (Exception err)
            {
                System.out.println("Explosion Catch");
            }
            //======= End Volatile/Boom ====
        }catch(Exception err)
        {
            err.printStackTrace();
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

        System.out.println("old: " + e.getPreviousSlot() + " new: " + e.getNewSlot());
        //e.getPlayer().getInventory().setHeldItemSlot(2);

        try
        {
            Player player = e.getPlayer();
            if(e.getPlayer().isSneaking())
            {
                LivingEntityData playerData = livingEntityBank.getLivingEntityData(player.getUniqueId());
                if(e.getPreviousSlot() == 8 && e.getNewSlot() == 0)
                {
                    playerData.powerIndexPlus();
                    System.out.println("power index: " + playerData.getPowerIndex());
                    player.getInventory().setContents(playerData.getPowerUps(playerData.getPowerIndex()));
                    player.updateInventory();
                }else if (e.getPreviousSlot() == 0 && e.getNewSlot() == 8)
                {
                    playerData.powerIndexMinus();
                    System.out.println("power index: " + playerData.getPowerIndex());
                    player.getInventory().setContents(playerData.getPowerUps(playerData.getPowerIndex()));
                    player.updateInventory();
                }
            }
        }catch(Exception err)
        {
            System.err.println("ItemHeldEvent: Sneak Swap");
            err.printStackTrace();
        }
        try
        {
            Player player = e.getPlayer();
            //set player customName to wop localizedName
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
            System.out.println("ItemHeldEvent: WOP");
        }
    }


    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e)
    {
        try
        {
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

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e)
    {
        Player player = e.getPlayer();
        boolean isSneaking = player.isSneaking();
        LivingEntityData playerData = livingEntityBank.getLivingEntityData(player.getUniqueId());
        System.out.println(player.getName() + " is sneaking: " + player.isSneaking());
        if(isSneaking)
        {
            player.getInventory().setContents(playerData.getBackupInventory());
            player.updateInventory();
        } else {

            playerData.backupInventory(player.getInventory().getStorageContents());
            ItemStack[] powerUps = playerData.getPowerUps(playerData.getPowerIndex());
            player.getInventory().setContents(powerUps);
            player.updateInventory();

            BukkitRunnable myTask = new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    int cur, prev;
                    prev = player.getInventory().getHeldItemSlot();
                    while (player.isSneaking())
                    {
                        cur = player.getInventory().getHeldItemSlot();
                        if (cur != prev)
                            System.out.println("Current slot: " + cur + " -> " + prev);
                        prev = cur;
                    }
                    this.cancel();
                }
            };
            myTask.runTaskAsynchronously(plugin);
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
                    int newDamage = currentDamage - (powerLevel * 2);
                    if (newDamage < 0)
                        newDamage = 0;//catches underflow of durability (going over 100%)
                    if (newDamage > maxDamage - 1)
                        newDamage = maxDamage - 1;//and durability from going to 0%

                    livingEntityData.setLastWOPRegenTime(System.currentTimeMillis());//timestamp to prevent creating too many regen_ammo tasks
                    damageable.setDamage(newDamage);
                    itemStack.setItemMeta(itemMeta);
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
            livingEntityBank.addLivingEntityData(player.getUniqueId(), new PlayerData());
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



}
