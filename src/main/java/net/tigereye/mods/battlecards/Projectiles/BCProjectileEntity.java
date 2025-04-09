package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;

import java.util.List;

public interface BCProjectileEntity {

    void getEffectsOnEntityHit();
    void addEffectOnEntityHit(CardEffect effect);
    void addEffectsOnEntityHit(List<CardEffect> effects);

    void getEffectsOnCollision();
    void addEffectOnCollision(CardEffect effect);
    void addEffectsOnCollision(List<CardEffect> effects);

    void getEffectsOnTick();
    void addEffectOnTick(CardEffect effect);
    void addEffectsOnTick(List<CardEffect> effects);

    void applyOnEntityHitEffects(EntityHitResult entityHitResult);
    void applyOnCollisionEffects(HitResult hitResult);
    void applyOnTickEffects(EntityHitResult entityHitResult);
}
