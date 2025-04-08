package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;

public interface ScalingTargetEntityCardEffect {
    void apply(Entity user, Entity target, float scalar, ItemStack item, BattleCard battleCard);
}
