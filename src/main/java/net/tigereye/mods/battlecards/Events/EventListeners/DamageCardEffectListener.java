package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Events.DamageCardEffectCallback;

public class DamageCardEffectListener {

    public static float applyPersistentContext(PersistentCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, float amount) {
        for (DamageCardEffectCallback damageCallback : pContext.modifyDamageCallbacks){
            amount = damageCallback.modifyDamage(pContext, entity, cardEffectContext, amount);
        }
        return amount;
    }
}
