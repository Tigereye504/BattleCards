package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

public class OnFire extends BCStatusEffect{

    public OnFire(){
        super(StatusEffectCategory.HARMFUL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!entity.isOnFire()) {
            igniteEntity(entity, amplifier);
        }
    }

    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        igniteEntity(entity, amplifier);
        if(amplifier == 0) {
            entity.removeStatusEffect(BCStatusEffects.ON_FIRE);
        }
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    }

    private void igniteEntity(LivingEntity entity, int amplifier) {
        StatusEffectInstance instance = entity.getStatusEffect(BCStatusEffects.ON_FIRE);
        if (instance != null && entity.getFireTicks() <= instance.getDuration()) {
            entity.setFireTicks(instance.getDuration());
        }
    }
}

