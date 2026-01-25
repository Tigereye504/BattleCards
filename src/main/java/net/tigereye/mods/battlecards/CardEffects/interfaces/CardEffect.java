package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

public interface CardEffect {

    void apply(PersistentCardEffectContext pContext, CardEffectContext context);

    default void apply(PersistentCardEffectContext pContext){
        apply(pContext,new CardEffectContext());
    }

}
