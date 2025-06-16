package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;

import java.util.Collection;
import java.util.List;

public class Overdraw extends BCStatusEffect{

    private static final float DAMAGE_BOOST_PER_LEVEL = 0.2f;

    public Overdraw(){
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    //apply damage boost event to next 'attack' played
    public void preparePersistentContext(PersistantCardEffectContext pContext, Entity entity, StatusEffectInstance instance, boolean quickElseCharge, List<StatusEffect> effectsToRemove) {
        Collection<String> keywords = quickElseCharge ? pContext.card.getQuickKeywords() : pContext.card.getChargeKeywords();
        if(keywords.contains("Attack")){
            pContext.modifyDamageListeners.add((pContext2,target2,context,amount)
                    -> amount * (1 + ((instance.getAmplifier()+1)*DAMAGE_BOOST_PER_LEVEL)));
            effectsToRemove.add(this);
        }
    }
}
