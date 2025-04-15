package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Projectiles.BCProjectileEntity;

import java.util.List;

public interface BCProjectileEffect {

    public void addEffectOnEntityHit(CardEffect effect);
    public void addEffectsOnEntityHit(List<CardEffect> effects);

    public void addEffectOnCollision(CardEffect effect);
    public void addEffectsOnCollision(List<CardEffect> effects);

    public void addEffectOnTick(CardEffect effect);
    public void addEffectsOnTick(List<CardEffect> effects);

    default public BCProjectileEntity createProjectile(Entity user, ItemStack item, BattleCard battleCard) {
        return createProjectile(user,user,item,battleCard);
    }
    public BCProjectileEntity createProjectile(Entity user, Entity target, ItemStack item, BattleCard battleCard);
}
