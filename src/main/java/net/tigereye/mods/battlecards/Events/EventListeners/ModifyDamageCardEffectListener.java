package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Events.ModifyDamageCardEffectCallback;

public class ModifyDamageCardEffectListener {

    public static float applyPersistentContext(PersistentCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, float amount) {
        for (ModifyDamageCardEffectCallback damageCallback : pContext.modifyDamageCallbacks){
            amount = damageCallback.modifyDamage(pContext, entity, cardEffectContext, amount);
        }
        return amount;
    }
}
