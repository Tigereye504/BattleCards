package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

public interface DamageCardEffectCallback {
    Event<DamageCardEffectCallback> EVENT = EventFactory.createArrayBacked(DamageCardEffectCallback.class,
            (listeners) -> (pContext,target,context,amount) -> {
                for (DamageCardEffectCallback listener : listeners) {
                    amount = listener.modifyDamage(pContext,target,context,amount);
                }
                return amount;
            });

    float modifyDamage(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, float amount);
}
