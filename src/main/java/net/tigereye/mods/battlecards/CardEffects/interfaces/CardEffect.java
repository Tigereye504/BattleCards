package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.CardEffectContext;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;

public interface CardEffect {

    void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context);

    default void apply(Entity user, ItemStack item, BattleCard battleCard){
        apply(user,item,battleCard,new CardEffectContext());
    }

}
