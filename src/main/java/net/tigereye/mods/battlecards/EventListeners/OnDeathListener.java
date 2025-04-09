package net.tigereye.mods.battlecards.EventListeners;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class OnDeathListener {

    private static ServerLivingEntityEvents.@NotNull AllowDeath checkStatusEffectsAllowDeath() {
        return (entity, source, damage) -> {

            AtomicBoolean allowDeath = new AtomicBoolean(true);
            BCStatusEffect.forEachBCStatusEffect(entity, (effect, instance, effectsToRemove, stopEarly) -> {
                if(!effect.allowDeath(entity,source,damage,instance,effectsToRemove)){
                    allowDeath.set(false);
                    stopEarly.set(true);
                }
            });
            return allowDeath.get();
        };
    }

    public static void register(){
        ServerLivingEntityEvents.ALLOW_DEATH.register(checkStatusEffectsAllowDeath());
    }
}
