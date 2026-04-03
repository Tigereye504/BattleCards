package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

import java.util.Collection;
import java.util.List;

public class Multishot extends BCStatusEffect{

    private static final float EXTRA_SHOTS_PER_LEVEL = 1;

    public Multishot(){
        super(StatusEffectCategory.BENEFICIAL, 0xBBBBBB);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    public void preparePersistentContext(PersistentCardEffectContext pContext, Entity entity, StatusEffectInstance instance, boolean quickElseCharge, List<StatusEffect> effectsToRemove) {
        Collection<String> keywords = quickElseCharge ? pContext.card.getQuickKeywords() : pContext.card.getChargeKeywords();
        if(keywords.contains("Projectile")){
            pContext.throwCardCallbacks.add((pContext2, target2, context, params) -> {
                params.copies *= (int) (EXTRA_SHOTS_PER_LEVEL*(instance.getAmplifier()+1));
                if(params.copyDelay == 0){
                    params.copyDelay = 5;
                }
            });
            effectsToRemove.add(this);
        }
    }
}
