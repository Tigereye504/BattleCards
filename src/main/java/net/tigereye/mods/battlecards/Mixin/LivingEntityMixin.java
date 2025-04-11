package net.tigereye.mods.battlecards.Mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin{
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Inject(at = @At("HEAD"), method = "isUndead", cancellable = true)
    public void LivingEntityBaseTickMixin(CallbackInfoReturnable<Boolean> cir) {
        if(this.hasStatusEffect(BCStatusEffects.UNDEATH)){
            cir.setReturnValue(true);
        }
    }
}
