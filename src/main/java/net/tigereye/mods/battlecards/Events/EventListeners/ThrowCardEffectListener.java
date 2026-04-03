package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardEffect;
import net.tigereye.mods.battlecards.Events.DamageCardEffectCallback;
import net.tigereye.mods.battlecards.Events.ThrowCardEffectCallback;

public class ThrowCardEffectListener {

    public static void applyPersistentContext(PersistentCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, ThrowCardEffect.Parameters params) {
        for (ThrowCardEffectCallback callback : pContext.throwCardCallbacks){
            callback.modifyThrowCardEffect(pContext, entity, cardEffectContext, params);
        }
    }
}
