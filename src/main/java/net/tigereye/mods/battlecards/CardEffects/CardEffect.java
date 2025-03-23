package net.tigereye.mods.battlecards.CardEffects;

import net.minecraft.entity.LivingEntity;
import net.tigereye.mods.battlecards.Items.BattleCard;

public interface CardEffect {
    void apply(LivingEntity user, LivingEntity target, BattleCard battleCard);
}
