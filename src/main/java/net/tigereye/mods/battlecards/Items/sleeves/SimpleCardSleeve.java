package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleCardSleeve extends CardSleeveItem{
    private float damageMultiplier = 0;
    public SimpleCardSleeve(Settings settings, float damageMultiplier) {
        super(settings);
        this.damageMultiplier = damageMultiplier;
    }

    public float modifyDamage(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context, float amount, ItemStack sleeve){
        return amount*damageMultiplier;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlecards.sleeve.simple.desc",(int)((damageMultiplier-1)*100)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
