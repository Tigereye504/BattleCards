package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

import java.util.List;

public class Undying extends BCStatusEffect{

    public Undying(){
        super(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    public boolean allowDeath(LivingEntity entity, DamageSource source, float damage, StatusEffectInstance effect, List<StatusEffect> effectsToRemove) {
        int level = effect.getAmplifier()+1;
        entity.setHealth(entity.getMaxHealth()*0.1f*(level));
        entity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.UNDEATH, 600*level));
        effectsToRemove.add(effect.getEffectType());
        return false;
    }
}
