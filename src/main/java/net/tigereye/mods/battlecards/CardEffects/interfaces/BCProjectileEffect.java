package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Projectiles.BCProjectileEntity;

import java.util.List;

public interface BCProjectileEffect {

    public void addEffectOnEntityHit(CardTargetEntityEffect effect);
    public void addEffectsOnEntityHit(List<CardTargetEntityEffect> effects);

    public void addEffectOnCollision(OnCollisionCardEffect effect);
    public void addEffectsOnCollision(List<OnCollisionCardEffect> effects);

    public void addEffectOnTick(CardTargetEntityEffect effect);
    public void addEffectsOnTick(List<CardTargetEntityEffect> effects);

    default public BCProjectileEntity createProjectile(Entity user, ItemStack item, BattleCard battleCard) {
        return createProjectile(user,user,item,battleCard);
    }
    public BCProjectileEntity createProjectile(Entity user, Entity target, ItemStack item, BattleCard battleCard);
}
