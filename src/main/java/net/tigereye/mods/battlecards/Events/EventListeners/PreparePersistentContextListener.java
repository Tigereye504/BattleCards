package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;

import java.util.ArrayList;
import java.util.List;

public class PreparePersistentContextListener {
    public static void applyStatusEffects(PersistantCardEffectContext pContext, Entity entity, boolean quickElseCharge) {
        if(entity instanceof LivingEntity lEntity){
            List<StatusEffect> effectsToRemove = new ArrayList<>();
            for(StatusEffectInstance instance : lEntity.getStatusEffects()){
                if(instance.getEffectType() instanceof BCStatusEffect bcEffect){
                    bcEffect.preparePersistentContext(pContext,entity,instance,quickElseCharge,effectsToRemove);
                }
            }
            for(StatusEffect effect : effectsToRemove){
                lEntity.removeStatusEffect(effect);
            }
        }
    }
}
