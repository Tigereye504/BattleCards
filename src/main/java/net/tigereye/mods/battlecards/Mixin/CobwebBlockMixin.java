package net.tigereye.mods.battlecards.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CobwebBlock.class)
public class CobwebBlockMixin {

    @Inject(at = @At("HEAD"), method = "onEntityCollision", cancellable = true)
    public void onEntityCollisionMixin(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if(entity instanceof LivingEntity lEntity && lEntity.hasStatusEffect(BCStatusEffects.WEBWALKING)){
            ci.cancel();
        }
    }

}
