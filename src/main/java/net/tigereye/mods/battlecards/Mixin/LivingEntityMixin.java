package net.tigereye.mods.battlecards.Mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin{
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Inject(at = @At("HEAD"), method = "isUndead", cancellable = true)
    public void LivingEntityBaseTickMixin(CallbackInfoReturnable<Boolean> cir) {
        if(this.hasStatusEffect(BCStatusEffects.UNDEATH)){
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "getStepHeight", cancellable = true)
    public void LivingEntityStepHeightMixin(CallbackInfoReturnable<Float> cir) {
        if(this.hasStatusEffect(BCStatusEffects.HIGHSTEP)){
            cir.setReturnValue(cir.getReturnValueF()+((this.getStatusEffect(BCStatusEffects.HIGHSTEP).getAmplifier()+1)*0.5F));
        }
    }
}
