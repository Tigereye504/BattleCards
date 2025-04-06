package net.tigereye.mods.battlecards.Cards.Json;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;

import java.util.List;

public interface BattleCard {


    public Identifier getArt();
    public void setArt(Identifier texture);
    public Identifier getIcon();
    public void setIcon(Identifier texture);
    public Identifier getBackground();
    public void setBackground(Identifier texture);
    public int getChargeEffectCost();
    public Text getBasicDescription();
    public Text getChargeDescription();
    public List<CardEffect> getBasicEffects();
    public List<CardEffect> getChargeEffects();
    public boolean performBasicEffect(LivingEntity user, ItemStack itemStack);
    public boolean performChargeEffect(LivingEntity user, ItemStack itemStack);
    public default void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if(tooltipContext.isAdvanced()) {
            tooltip.add(Text.translatable("card.battlecards.tooltip.basic_header"));
            for (CardEffect effect : this.getBasicEffects()) {
                if (effect instanceof CardTooltipNester tooltipNester) {
                    tooltipNester.appendNestedTooltip(world, tooltip, tooltipContext, 1);
                }
            }
            tooltip.add(Text.translatable("card.battlecards.tooltip.charge_header", this.getChargeEffectCost()));
            for (CardEffect effect : this.getChargeEffects()) {
                if (effect instanceof CardTooltipNester tooltipNester) {
                    tooltipNester.appendNestedTooltip(world, tooltip, tooltipContext, 1);
                }
            }
        }
        else{
            tooltip.add(Text.translatable("card.battlecards.tooltip.basic_header"));
            tooltip.add(getBasicDescription());
            tooltip.add(Text.translatable("card.battlecards.tooltip.charge_header", this.getChargeEffectCost()));
            tooltip.add(getChargeDescription());
        }
    }
}
