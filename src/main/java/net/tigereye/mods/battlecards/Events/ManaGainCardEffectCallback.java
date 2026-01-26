package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

public interface ManaGainCardEffectCallback {
    Event<ManaGainCardEffectCallback> EVENT = EventFactory.createArrayBacked(ManaGainCardEffectCallback.class,
            (listeners) -> (pContext,target,context,amount) -> {
                for (ManaGainCardEffectCallback listener : listeners) {
                    amount = listener.modifyManaGain(pContext,target,context,amount);
                }
                return amount;
            });

    int modifyManaGain(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, int amount);
}
