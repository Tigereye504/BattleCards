package net.tigereye.mods.battlecards.Mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.tigereye.mods.battlecards.Projectiles.BCProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin implements BCProjectileEntity {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(DDD)V"), method = "tick", index = 1)
    public double TickModifyGravityMixin(double constant) {
        return constant + 0.05f - getGravity(); //reverse normal gravity, then apply my own.
    }
}
