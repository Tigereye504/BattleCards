package net.tigereye.mods.battlecards.StatusEffects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;

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

    public void preparePersistentContext(PersistantCardEffectContext pContext, Entity entity, StatusEffectInstance instance, boolean quickElseCharge, List<StatusEffect> effectsToRemove) {
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

    public static StatusEffectInstance buildGradualFalloffStatusEffectInstance(StatusEffect effect, int fullStrengthDuration, int falloffInterval,int magnitude, boolean ambient, boolean showParticles, boolean showIcon){
        StatusEffectInstance instance = new StatusEffectInstance(effect,fullStrengthDuration,magnitude,ambient,showParticles,showIcon);
        for (int i = 1; i <= magnitude; i++) {
            instance.upgrade(new StatusEffectInstance(effect,fullStrengthDuration+(falloffInterval*i),magnitude-i,ambient,showParticles,showIcon));
        }
        return instance;
    }
}