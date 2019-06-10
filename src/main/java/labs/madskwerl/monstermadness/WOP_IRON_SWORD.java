package labs.madskwerl.monstermadness;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;


public class WOP_IRON_SWORD
{
    public static void onUse(PlayerInteractEvent e, MonsterMadness monsterMadness, NSA nsa)
    {
        Player player = e.getPlayer();
        String customName = player.getCustomName();
        Block blockClicked = e.getClickedBlock();
        Action action = e.getAction();
        // If the player left clicks
        ItemStack itemStackInMainHand = e.getItem();
        ItemMeta itemMeta = itemStackInMainHand.getItemMeta();

        try //to handle null pointer exceptions
        {
            //============================== Interact: Cool-Down/Durability =======================================
            long currentTime = System.currentTimeMillis();
            LivingEntityData livingEntityData = nsa.livingEntityBank.getLivingEntityData(player.getUniqueId());
            if ((currentTime - livingEntityData.getLastAttackTime()) > livingEntityData.getAttackDelay() && //if player's cool-down is finished
                    (currentTime - livingEntityData.getLastWOPRegenTime()) > 100)                           //and 100ms since last WOP regen (to prevent making too many)
            {
                livingEntityData.setLastAttackTime(currentTime);                                           //then reset player cool-down period

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
                        new Regen_Ammo(nsa, itemStackInMainHand, livingEntityData).runTaskLater(monsterMadness, 20);
                    }
                }
                //======= Regen ====

                //==================================== Interact: Jamming ==========================================
                //roll for power, will either be jamming or volatile, not both
                int roll = nsa.random.nextInt(5);
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
                    fireball.setYield(10);
                    fireball.setIsIncendiary(false);
                    //fireball.setVelocity(new Vector(0, -10, 0)); //sends straight down fast enough to explode immediately
                }
                //======= End Volatile/Boom ====
            }
            //========= End Cool-Down =======
        } catch (Exception err)
        {
            System.out.println("InteractErrorCaught");
        }
    }

    public static void onHit(EntityDamageByEntityEvent e, MonsterMadness monsterMadness, NSA nsa)
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

        //================================= Ammo Application Block ======================================
        if(attacker instanceof Player)
        {
            long currentTime = System.currentTimeMillis();
            LivingEntityData attackerLED = nsa.livingEntityBank.getLivingEntityData(attacker.getUniqueId());
            ItemStack mainHandItemStack = ((Player) attacker).getInventory().getItemInMainHand();
            ItemMeta itemMeta = mainHandItemStack.getItemMeta();

            if ((currentTime - attackerLED.getLastAttackTime()) > attackerLED.getAttackDelay() &&               //if player's cool-down is finished
                    (currentTime - attackerLED.getLastWOPRegenTime()) > 100)                                    //and 100ms since last WOP regen (to prevent making too many)
            {
                attackerLED.setLastAttackTime(currentTime);//then reset player cool-down period

                Damageable damageable = (Damageable) itemMeta;
                int fragilityLevel = WOP.getPowerLevel(attackerCustomName, 28); //PowerID:28 = FRAGILE
                int damageMultiplier = fragilityLevel < 0 ? fragilityLevel * -1 : 0;
                int maxDamage = WOP.getMaxDurability(attackerCustomName);
                int currentDamage = damageable.getDamage();
                int newDamage = currentDamage + (5 + damageMultiplier * 5);

                if (newDamage >= maxDamage)//if durability is too low
                {
                    e.setCancelled(true);//cancel the event
                    return;
                } else //otherwise, remove durability (ammo)
                {
                    System.out.println("Damage: " + currentDamage + " -> " + newDamage);
                    damageable.setDamage(newDamage);
                }
            }
        }
        //================================= Damage Application Block ======================================
        int attackerLevel;
        int defenderLevel;
        if (attacker instanceof Player)
        {
            LivingEntityData attackerLED = nsa.livingEntityBank.getLivingEntityData(attacker.getUniqueId());
            attackerLevel = attackerLED.getLevel();
            if (defenderCustomName.contains("WOP"))
                defenderLevel = (int) monsterMadness.wopMonsterLevel;
            else
                defenderLevel = nsa.livingEntityBank.getLivingEntityData(attacker.getUniqueId()).getLevel();
        } else
        {
            defenderLevel = nsa.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
            if (attackerCustomName.contains("WOP"))
                attackerLevel = (int) monsterMadness.wopMonsterLevel;
            else
                attackerLevel = nsa.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
        }

        double levelRatioModifier = 1 + (attackerLevel - defenderLevel) * 0.01;

        int protectionLevel = !defenderCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(defenderCustomName, 5); //PowerID:5 = PROTECTION/WEAKNESS
        double protectionModifier = 1 - protectionLevel * .1;

        int damageLevel = !attackerCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(attackerCustomName, 4); //PowerID:9 = BLAST PRUF/CRUMBLE
        double damageIncreaseModifier = 1 + damageLevel * 0.1;

        int wopBaseDamage = 0;
        if (attackerCustomName.contains("WOP") && defenderCustomName.contains("WOP"))
        {
            int attackerBase = nsa.livingEntityBank.getLivingEntityData(attacker.getUniqueId()).getBaseATK();
            int defenderBase = nsa.livingEntityBank.getLivingEntityData(defender.getUniqueId()).getBaseDEF();
            wopBaseDamage = attackerBase - defenderBase;
        }

        double damage = wopBaseDamage + e.getDamage() * levelRatioModifier * protectionModifier * damageIncreaseModifier;
        System.out.println(attacker.getName() + " dealt " + damage + " damage to " + defender.getName());


        //================================= Entity vs Entity: Boom ======================================
        int roll = nsa.random.nextInt(5);
        try
        {
            Location location = null;
            if (WOP.getPowerLevel(attackerCustomName, 8) > roll)//PowerID:8 = BOOM
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