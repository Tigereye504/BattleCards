package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardEffect;

public interface ThrowCardEffectCallback {
    Event<ThrowCardEffectCallback> EVENT = EventFactory.createArrayBacked(ThrowCardEffectCallback.class,
            (listeners) -> (pContext,target,context,params) -> {
                for (ThrowCardEffectCallback listener : listeners) {
                    listener.modifyThrowCardEffect(pContext,target,context,params);
                }
            });

    void modifyThrowCardEffect(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, ThrowCardEffect.Parameters params);
}
