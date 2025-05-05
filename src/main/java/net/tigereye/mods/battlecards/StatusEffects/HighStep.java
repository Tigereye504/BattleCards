package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;

public class HighStep extends BCStatusEffect{

    public HighStep(){
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    //step assistance implemented via client mixin.
    //TODO: once updated to newer MC, instead use Step Height attribute
}
