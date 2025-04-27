package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;

import java.util.ArrayList;
import java.util.List;

public interface BCProjectileEntity {

    default List<CardEffect>  getEffectsOnEntityHit(){return new ArrayList<>();}
    default void addEffectOnEntityHit(CardEffect effect){}
    default void addEffectsOnEntityHit(List<CardEffect> effects){}

    default List<CardEffect>  getEffectsOnCollision(){return new ArrayList<>();}
    default void addEffectOnCollision(CardEffect effect){}
    default void addEffectsOnCollision(List<CardEffect> effects){}

    default List<CardEffect> getEffectsOnTick(){return new ArrayList<>();}
    default void addEffectOnTick(CardEffect effect){}
    default void addEffectsOnTick(List<CardEffect> effects){}

    default void applyOnEntityHitEffects(EntityHitResult entityHitResult){}
    default void applyOnCollisionEffects(HitResult hitResult){}
    default void applyOnTickEffects(EntityHitResult entityHitResult){}

    default float getGravity(){return 0.05f;}
}
