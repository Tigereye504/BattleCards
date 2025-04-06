package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;

public interface CardAfterDamageEffect {
    void apply(Entity user, Entity target, float damageDealt, ItemStack item, BattleCard battleCard);
}
