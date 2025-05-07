package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Overdraw extends BCStatusEffect{

    public Overdraw(){
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    //TODO: apply damage boost event to next 'attack' card played
}
