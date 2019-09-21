package labs.madskwerl.monstermadness;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;


public class WOP_IRON_SWORD
{
    public static void onUse(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();
        String customName = player.getCustomName();
        Block blockClicked = e.getClickedBlock();
        // If the player left clicks
        ItemStack itemStackInMainHand = e.getItem();
        ItemMeta itemMeta = itemStackInMainHand.getItemMeta();

        try //to handle null pointer exceptions
        {
            //============================== Interact: Cool-Down/Durability =======================================
            long currentTime = System.currentTimeMillis();
            LivingEntityData livingEntityData = LivingEntityBank.getLivingEntityData(player.getUniqueId());
            if ((currentTime - livingEntityData.getLastAttackTime()) > livingEntityData.getAttackDelay() && //if player's cool-down is finished
                    (currentTime - livingEntityData.getLastWOPRegenTime()) > 100)                           //and 100ms since last WOP regen (to prevent making too many)
            {
                livingEntityData.setLastAttackTime(currentTime);                                           //then reset player cool-down period
                //handle infinity powerUp
                if(WOP.getPowerLevel(customName, 0) < 1)
                {
                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = WOP.getPowerLevel(customName, 28); //PowerID:28 = FRAGILE
                    int damageMultiplier = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                    int maxDamage = WOP.getMaxDurability(customName);
                    int currentDamage = damageable.getDamage();
                    int newDamage = currentDamage + (1 + damageMultiplier);

                    if (newDamage > maxDamage)//if durability is too low
                    {
                        e.setCancelled(true);//cancel the event
                        return;
                    } else //otherwise, remove durability (ammo)
                    {
                        System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                        damageable.setDamage(newDamage);
                        itemStackInMainHand.setItemMeta(itemMeta);

                        MonsterMadness.NSA.refreshChargesArtifact(player);
                        //===================================== Interact: Regen ===========================================
                        //only start a new regen ammo recursion if current damage is 0 and item has appropriate power
                        if (currentDamage == 0 && WOP.getPowerLevel(customName, 1) != 0) //PowerID:3 = REGEN
                        {
                            System.out.println("Regen Timer Started From Player Interact.");
                            new Regen_Ammo(itemStackInMainHand, livingEntityData).runTaskLater(MonsterMadness.PLUGIN, 20);
                        }
                    }
                }
                //======= Regen ====

                //==================================== Interact: Jamming ==========================================
                //roll for power, will either be jamming or volatile, not both
                int roll = MonsterMadness.RANDOM.nextInt(5);
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

    public static void onHit(EntityDamageByEntityEvent e)
    {
        Entity attacker = e.getDamager();
        String attackerCustomName = attacker.getCustomName();

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
        if (bothPlayers || (bothNotPlayers && oneIsWOP))
        {
            e.setCancelled(true);
            return;
        }

        int[] defendersPowersArray = WOP.getPowersArray(defenderCustomName);
        int[] attackersPowersArray = WOP.getPowersArray(attackerCustomName);


        //================================= Ammo Application Block ======================================
        long currentTime = System.currentTimeMillis();
        if(attacker instanceof Player)
        {
            LivingEntityData attackerLED = LivingEntityBank.getLivingEntityData(attacker.getUniqueId());
            Player player = (Player)attacker;
            ItemStack mainHandItemStack = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = mainHandItemStack.getItemMeta();
            if ((currentTime - attackerLED.getLastAttackTime()) > attackerLED.getAttackDelay() &&            //if player's cool-down is finished
                (currentTime - attackerLED.getLastWOPRegenTime()) > 100)                                    //and 100ms since last WOP regen (to prevent making too many)                                    //and 100ms since last WOP regen (to prevent making too many)
            {
                attackerLED.setLastAttackTime(currentTime);//then reset player cool-down period

                if(attackersPowersArray[0] < 1)
                {
                    Damageable damageable = (Damageable) itemMeta;
                    int fragilityLevel = attackersPowersArray[28]; //PowerID:28 = FRAGILE
                    int damageMultiplier = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                    int maxDamage = WOP.getMaxDurability(attackerCustomName);
                    int currentDamage = damageable.getDamage();
                    int newDamage = currentDamage + (1 + damageMultiplier);

                    if (newDamage > maxDamage)//if durability is too low
                    {
                        e.setCancelled(true);//cancel the event
                        return;
                    } else //otherwise, remove durability (ammo)
                    {
                        System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                        damageable.setDamage(newDamage);
                        mainHandItemStack.setItemMeta(itemMeta);
                        MonsterMadness.NSA.refreshChargesArtifact(player);
                        //===================================== Interact: Regen ===========================================
                        //only start a new regen ammo recursion if current damage is 0 and item has appropriate lore
                        if (currentDamage == 0 && attackersPowersArray[1] != 0) //PowerID:3 = REGEN
                        {
                            System.out.println("Regen Timer Started From Player Interact.");
                            new Regen_Ammo(mainHandItemStack, attackerLED).runTaskLater(MonsterMadness.PLUGIN, 20);
                        }
                    }
                }
            }
        }
        //================================= Damage Application Block ======================================
        int attackerLevel;
        int defenderLevel;
        LivingEntityData attackerLED = null;
        LivingEntityData defenderLED = null;
        if (attacker instanceof Player) //attacker is player.
        {   //attacker level is in led
            attackerLED = LivingEntityBank.getLivingEntityData(attacker.getUniqueId());
            attackerLevel = attackerLED.getLevel();
            if (defenderCustomName.contains("WOP")) //defender is wop mob
            {   //monster level is that of the plugin level, and the mob has LED
                defenderLevel = (int) MonsterMadness.MONSTER_LEVEL_AVERAGE;
                defenderLED = LivingEntityBank.getLivingEntityData(defender.getUniqueId());
            }
            else  //defender is not a wop generated mob
            {   //defender level is same a player (makes the lvl dam mod 1)
                defenderLevel = attackerLED.getLevel();
                //may have data (in the case of prev being hit with timer effect). may not and return null if so
                defenderLED = LivingEntityBank.getLivingEntityData(defender.getUniqueId());
            }
        } else //attacker is a mob with an wop iron sword, defender is a player. either or both attacker and defender have a wop
        {
            //defender is a player
            defenderLED = LivingEntityBank.getLivingEntityData(defender.getUniqueId());
            defenderLevel = defenderLED.getLevel();
            attackerLevel = (int) MonsterMadness.MONSTER_LEVEL_AVERAGE;

        }

        double levelRatioModifier = 1 + (attackerLevel - defenderLevel) * 0.01;

        int protectionLevel = !defenderCustomName.contains("WOP") ? 0 : defendersPowersArray[5]; //PowerID:5 = PROTECTION/WEAKNESS
        double protectionModifier = 1 - protectionLevel * .1;

        int damageLevel = !attackerCustomName.contains("WOP") ? 0 : attackersPowersArray[4]; //PowerID:4 = Damage/Feeble
        double damageIncreaseModifier = 1 + damageLevel * 0.1;

        int wopBaseDamage = 0;
        if (attackerCustomName.contains("WOP") && defenderCustomName.contains("WOP"))
        {
            int attackerBase = LivingEntityBank.getLivingEntityData(attacker.getUniqueId()).getBaseATK();
            int defenderBase = LivingEntityBank.getLivingEntityData(defender.getUniqueId()).getBaseDEF();
            wopBaseDamage = attackerBase - defenderBase;
        }

        double damage = wopBaseDamage + e.getDamage() * levelRatioModifier * protectionModifier * damageIncreaseModifier;
        System.out.println(attacker.getName() + " dealt " + damage + " damage to " + defender.getName());

        //handle vamp/charity, allows overheal for now...
        int vampLevel = attackersPowersArray[2];
        if (vampLevel > 0)  //vamp
        {
            LivingEntity livingEntity = (LivingEntity) attacker;
            livingEntity.setHealth(livingEntity.getHealth() + vampLevel * .1 * damage);
        }
        else if(vampLevel < 0) //charity
        {
            LivingEntity livingEntityAttacker = (LivingEntity) attacker;
            double newHealth = livingEntityAttacker.getHealth() + vampLevel * .1 * damage;
            if(newHealth < 0)
                newHealth = 0;
            livingEntityAttacker.setHealth(newHealth);
            LivingEntity livingEntityDefender = (LivingEntity) defender;
            livingEntityDefender.setHealth(livingEntityDefender.getHealth() + vampLevel * .1 * damage);
        }

        //================================= Regen Timer Starter ======================================
        LivingEntity livingEntity = (LivingEntity)defender;
        if(WOP.isWOP(defenderCustomName) && defendersPowersArray[3] > 0 && livingEntity.getHealth() - e.getFinalDamage() < livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
            new Regen_Health(livingEntity).runTaskLater(MonsterMadness.PLUGIN, 20);

        int poisonLevel = attackersPowersArray[6]; //PowerID:6 = POISON
        if( poisonLevel > 0 )
        {
            if(defenderLED == null)
            {
                defenderLED = new  LivingEntityData(defender.getUniqueId());
                LivingEntityBank.addLivingEntityData(defender.getUniqueId(), defenderLED);
            }
            defenderLED.setPoisonTime(poisonLevel, currentTime + 5000);
            if(!defenderLED.isPoisoned())
            {
                defenderLED.setPoisoned(true);
                new PoisonTimer(defenderLED).runTaskLater(MonsterMadness.PLUGIN, 10);
            }
        }

        //================================= Entity vs Entity: Boom ======================================
        int roll = MonsterMadness.RANDOM.nextInt(5);
        try
        {
            Location location = null;
            if (attackersPowersArray[8] > roll)//PowerID:8 = BOOM
                location = defender.getLocation(); //explode where the player is looking
            //note this only handles melee atm
            Fireball fireball = (Fireball) attacker.getWorld().spawnEntity(location, EntityType.FIREBALL); //fireball had the more control and aesthetics than creeper or tnt. Could not use world.createExplosion(), needed way to track entity
            fireball.setCustomName(attackerCustomName + ":" + attacker.getUniqueId()); //provides way to track entity
            fireball.setYield(2);
            fireball.setIsIncendiary(false);
            fireball.setVelocity(new Vector(0, -1000, 0)); //sends straight down fast enough to explode immediately
        } catch (Exception err)
        {
            System.out.println("Explosion Catch");
        }
    }
}