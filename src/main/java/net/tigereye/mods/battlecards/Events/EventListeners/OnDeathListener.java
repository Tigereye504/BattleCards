package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;

import java.util.concurrent.atomic.AtomicBoolean;

public class OnDeathListener {
    public static boolean checkStatusEffectsAllowDeath(LivingEntity entity, DamageSource source, float damage) {
        AtomicBoolean allowDeath = new AtomicBoolean(true);
        BCStatusEffect.forEachBCStatusEffect(entity, (effect, instance, effectsToRemove, stopEarly) -> {
            if(!effect.allowDeath(entity,source,damage,instance,effectsToRemove)){
                allowDeath.set(false);
                stopEarly.set(true);
            }
        });
        return allowDeath.get();
    }
}
