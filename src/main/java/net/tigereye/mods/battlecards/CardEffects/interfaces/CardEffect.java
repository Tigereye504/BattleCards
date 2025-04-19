package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;

public interface CardEffect {

    void apply(PersistantCardEffectContext pContext, CardEffectContext context);

    default void apply(PersistantCardEffectContext pContext){
        apply(pContext,new CardEffectContext());
    }

}
