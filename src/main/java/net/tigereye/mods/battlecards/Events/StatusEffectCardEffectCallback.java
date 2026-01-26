package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

public interface StatusEffectCardEffectCallback {
    Event<StatusEffectCardEffectCallback> EVENT = EventFactory.createArrayBacked(StatusEffectCardEffectCallback.class,
            (listeners) -> (pContext,target,context,instance) -> {
                for (StatusEffectCardEffectCallback listener : listeners) {
                    instance = listener.modifyStatusEffect(pContext,target,context,instance);
                }
                return instance;
            });

    StatusEffectInstance modifyStatusEffect(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, StatusEffectInstance instance);
}
