package net.tigereye.mods.battlecards.CardEffects.context;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public class CardEffectContext {
    public Entity target;
    public float scalar;
    public HitResult hitResult;

    @Override
    public CardEffectContext clone(){
        CardEffectContext cardEffectContext = new CardEffectContext();
        cardEffectContext.target = target;
        cardEffectContext.scalar = scalar;
        cardEffectContext.hitResult = hitResult;
        return cardEffectContext;
    }
}
