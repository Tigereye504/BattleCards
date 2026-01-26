package net.tigereye.mods.battlecards.Cards;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;

import java.util.Collection;
import java.util.List;

public interface BattleCard {

    public Identifier getID();
    public int getChargeEffectCost();
    public Text getName();
    public Text getBasicDescription();
    public Text getChargeDescription();
    public List<Identifier> getVariants();
    public void addVariant(Identifier id);
    public Collection<String> getQuickKeywords();
    public Collection<String> getChargeKeywords();
    public List<CardEffect> getQuickEffects();
    public List<CardEffect> getChargeEffects();
    public boolean performQuickEffect(LivingEntity user, ItemStack itemStack);
    public boolean performChargeEffect(LivingEntity user, ItemStack itemStack);
    public ItemStack getScrapValue();
    public default void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if(tooltipContext.isAdvanced()) {
            tooltip.add(Text.translatable("card.battlecards.tooltip.quick_header"));
            for (CardEffect effect : this.getQuickEffects()) {
                if (effect instanceof CardTooltipNester tooltipNester) {
                    tooltipNester.appendNestedTooltip(world, tooltip, tooltipContext, 1);
                }
            }
            int cost = this.getChargeEffectCost();
            int modifiedCost = cost;
            if(itemStack.getItem() instanceof BattleCardItem bci){
                modifiedCost = bci.getChargeEffectCost(null,itemStack,true);
            }
            tooltip.add(Text.translatable("card.battlecards.tooltip.charge_header", this.getChargeEffectCost(),
                    cost != modifiedCost ? " ("+modifiedCost+")" : ""));
            for (CardEffect effect : this.getChargeEffects()) {
                if (effect instanceof CardTooltipNester tooltipNester) {
                    tooltipNester.appendNestedTooltip(world, tooltip, tooltipContext, 1);
                }
            }
        }
        else{
            tooltip.add(Text.translatable("card.battlecards.tooltip.quick_header"));
            tooltip.add(getBasicDescription());
            int cost = this.getChargeEffectCost();
            int modifiedCost = cost;
            if(itemStack.getItem() instanceof BattleCardItem bci){
                modifiedCost = bci.getChargeEffectCost(null,itemStack,true);
            }
            tooltip.add(Text.translatable("card.battlecards.tooltip.charge_header", this.getChargeEffectCost(),
                    cost != modifiedCost ? " ("+modifiedCost+")" : ""));
            tooltip.add(getChargeDescription());
        }
    }
}
