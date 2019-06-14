package labs.madskwerl.monstermadness;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.UUID;

public class WOP_EXPLOSION
{
    //called from EntityDamageByEntityEvent if Entity.getType() is EntityType.FIREBALL
    public static void onHit(EntityDamageByEntityEvent e)
    {
        //locate attacker from source
        Entity source = e.getDamager();
        String sourceCustomName = source.getCustomName();
        if(sourceCustomName==null)
            sourceCustomName = "";

        Entity attacker = null;
        String attackerCustomName = "";
        if(sourceCustomName.contains("WOP"))
        {
            String uuid = sourceCustomName.split(":")[Powers.NumberOfPowers + 2];
            attacker = MonsterMadness.PLUGIN.getServer().getEntity(UUID.fromString(uuid));
        }
        if(attacker != null)
        {
            attackerCustomName = attacker.getCustomName();
            if (attackerCustomName == null)
                attackerCustomName = "";
        }

        //locate defender
        Entity defender = e.getEntity();
        String defenderCustomName = defender.getCustomName();


        if(defenderCustomName == null)
            defenderCustomName = "";

        //================================= Cancellation Block ======================================\
        //Note: additional cancel condition below that is not in cancellation block
        boolean bothPlayers = (attacker instanceof Player && defender instanceof  Player);
        boolean attackSelf = attacker == defender;
        boolean bothNotPlayers = !(attacker instanceof Player) && !(defender instanceof Player);
        boolean oneIsWOP = attackerCustomName.contains("WOP") || defenderCustomName.contains("WOP");
        if ((bothPlayers && !attackSelf) || (bothNotPlayers && oneIsWOP))
        {
            e.setCancelled(true);
            return;
        }

        //================================= Damage Application Block ======================================
        int attackerLevel;
        int defenderLevel;
        if (attacker instanceof Player)
        {
            LivingEntityData attackerLED = LivingEntityBank.getLivingEntityData(attacker.getUniqueId());
            attackerLevel = attackerLED.getLevel();
            if (defenderCustomName.contains("WOP"))
                defenderLevel = (int)MonsterMadness.MONSTER_LEVEL_AVERAGE;
            else
                defenderLevel = LivingEntityBank.getLivingEntityData(attacker.getUniqueId()).getLevel();
        }
        else
        {
            defenderLevel = LivingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
            if (attackerCustomName.contains("WOP"))
                attackerLevel = (int)MonsterMadness.MONSTER_LEVEL_AVERAGE;
            else
                attackerLevel = LivingEntityBank.getLivingEntityData(defender.getUniqueId()).getLevel();
        }

        double levelRatioModifier = 1 + (attackerLevel - defenderLevel) * 0.01;

        int protectionLevel = !defenderCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(defenderCustomName, 5); //PowerID:5 = PROTECTION/WEAKNESS
        double protectionModifier = 1 - protectionLevel * .1;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
        {
            int explosiveProtectionLevel = !defenderCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(defenderCustomName, 9); //PowerID:9 = BLAST PRUF/CRUMBLE
            double explosiveProtection = 1 - explosiveProtectionLevel * 0.15;
            if ((explosiveProtectionLevel > 0 && protectionLevel < 0) || (explosiveProtectionLevel < 0 && protectionLevel > 0))
                protectionModifier += explosiveProtection;
            else
                protectionModifier = (protectionLevel > explosiveProtectionLevel) ? protectionLevel:explosiveProtection;
        }

        int damageLevel = !attackerCustomName.contains("WOP") ? 0 : WOP.getPowerLevel(attackerCustomName, 4); //PowerID:9 = BLAST PRUF/CRUMBLE
        double damageIncreaseModifier = 1 + damageLevel * 0.1;

        int wopBaseDamage = 0;
        if (attackerCustomName.contains("WOP") && defenderCustomName.contains("WOP"))
        {
            int attackerBase = LivingEntityBank.getLivingEntityData(attacker.getUniqueId()).getBaseATK();
            int defenderBase = LivingEntityBank.getLivingEntityData(defender.getUniqueId()).getBaseDEF();
            wopBaseDamage = attackerBase - defenderBase;
        }

        double damage = wopBaseDamage + 10 * levelRatioModifier * protectionModifier * damageIncreaseModifier; //Note: overriding event damage ("+ 10") to ensuring a more consistent damage
        e.setDamage(damage);
        System.out.println( "The Thing dealt " + damage + " damage to " + defender.getName());
    }
}
