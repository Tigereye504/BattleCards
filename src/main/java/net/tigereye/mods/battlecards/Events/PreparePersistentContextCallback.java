package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

public interface PreparePersistentContextCallback {
    Event<PreparePersistentContextCallback> EVENT = EventFactory.createArrayBacked(PreparePersistentContextCallback.class,
            (listeners) -> (pContext,target,quickOrCharge) -> {
                for (PreparePersistentContextCallback listener : listeners) {
                    listener.preparePersistentContext(pContext,target,quickOrCharge);
                }
            });

    void preparePersistentContext(PersistentCardEffectContext pContext, Entity user, boolean quickElseCharge);
}
