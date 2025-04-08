package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;

public interface OnCollisionCardEffect {
    void apply(Entity user, HitResult hitResult, ItemStack item, BattleCard battleCard);
}
