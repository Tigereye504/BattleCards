package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BCStatusEffect extends StatusEffect {

    public BCStatusEffect(StatusEffectCategory type, int color) {
        super(type, color);
    }

    /*****Hooks*****/

    public boolean allowDeath(LivingEntity entity, DamageSource source, float damage, StatusEffectInstance instance, List<StatusEffect> effectsToRemove) {
        return true;
    }

    public boolean forceUndeath(LivingEntity entity, StatusEffectInstance instance, List<StatusEffect> effectsToRemove) {
        return false;
    }

    /*****Utilities*****/

    public static void forEachBCStatusEffect(LivingEntity entity, BCStatusEffect.Consumer consumer){
        List<StatusEffect> effectsToRemove = new ArrayList<>();
        AtomicBoolean stopEarly = new AtomicBoolean(false);
        for(StatusEffectInstance instance : entity.getStatusEffects()){
            if(instance.getEffectType() instanceof BCStatusEffect bcStatusEffect){
                consumer.accept(bcStatusEffect,instance,effectsToRemove,stopEarly);
            }
            if(stopEarly.get()){
                break;
            }
        }
        for(StatusEffect effect : effectsToRemove){
            entity.removeStatusEffect(effect);
        }
    }

    @FunctionalInterface
    public interface Consumer {
        void accept(BCStatusEffect effect, StatusEffectInstance instance, List<StatusEffect> effectsToRemove, AtomicBoolean stopEarly);
    }
}