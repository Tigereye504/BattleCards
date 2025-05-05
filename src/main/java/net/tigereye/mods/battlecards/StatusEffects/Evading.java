package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

import java.util.List;

public class Evading extends BCStatusEffect{

    public Evading(){
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setInvulnerable(true);
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setInvulnerable(false);
    }
}
