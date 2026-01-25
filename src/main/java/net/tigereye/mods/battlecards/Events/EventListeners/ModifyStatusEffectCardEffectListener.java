package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Events.ModifyStatusEffectCardEffectCallback;

public class ModifyStatusEffectCardEffectListener {

    public static StatusEffectInstance applyPersistentContext(PersistentCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, StatusEffectInstance instance) {
        for (ModifyStatusEffectCardEffectCallback statusEffectCallback : pContext.modifyStatusEffectListeners){
            instance = statusEffectCallback.modifyStatusEffect(pContext, entity, cardEffectContext, instance);
        }
        return instance;
    }
}
