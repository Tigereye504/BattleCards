package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;

public class NetheriteCardSleeve extends CardSleeveItem{
    protected static final float DAMAGE_MULTIPLIER = 2.25f;
    public NetheriteCardSleeve(Settings settings) {
        super(settings);
    }

    public float modifyDamage(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context, float amount, ItemStack sleeve){
        return amount*DAMAGE_MULTIPLIER;
    }
}
