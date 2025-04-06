package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.util.hit.EntityHitResult;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardOnCollisionEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;

import java.util.List;

public interface BCProjectileEntity {

    public void getEffectsOnEntityHit();
    public void addEffectOnEntityHit(CardTargetEntityEffect effect);
    public void addEffectsOnEntityHit(List<CardTargetEntityEffect> effects);

    public void getEffectsOnCollision();
    public void addEffectOnCollision(CardOnCollisionEffect effect);
    public void addEffectsOnCollision(List<CardOnCollisionEffect> effects);

    public void getEffectsOnTick();
    public void addEffectOnTick(CardTargetEntityEffect effect);
    public void addEffectsOnTick(List<CardTargetEntityEffect> effects);

    public void applyOnEntityHitEffects(EntityHitResult entityHitResult);
    public void applyOnTickEffects(EntityHitResult entityHitResult);
}
