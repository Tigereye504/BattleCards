package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Items.BattleCardItem;

public interface CardEffect {
    void apply(Entity user, ItemStack item, BattleCard battleCard);
}
