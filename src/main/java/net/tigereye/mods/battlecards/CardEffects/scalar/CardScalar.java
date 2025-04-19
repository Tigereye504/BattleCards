package net.tigereye.mods.battlecards.CardEffects.scalar;

import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;

public interface CardScalar {
    double getValue(PersistantCardEffectContext pContext, CardEffectContext context);
}
