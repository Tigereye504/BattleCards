package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Events.ManaGainCardEffectCallback;

public class ManaGainCardEffectListener {

    public static int applyPersistentContext(PersistentCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, int amount) {
        for (ManaGainCardEffectCallback manaGainCallback : pContext.manaGainCallbacks){
            amount = manaGainCallback.modifyManaGain(pContext, entity, cardEffectContext, amount);
        }
        return amount;
    }
}
