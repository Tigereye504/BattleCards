package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Items.BattleCardItem;

public interface CardTargetEntityEffect {
    void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard);
}
