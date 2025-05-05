package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class BoundingScalerEffect implements CardEffect, CardScalar, CardTooltipNester {

    private static final CardScalar DEFAULT_MINIMUM = new ConstantScalerEffect(Float.MIN_VALUE);
    private static final CardScalar DEFAULT_MAXIMUM = new ConstantScalerEffect(Float.MAX_VALUE);

    List<CardEffect> effects = new ArrayList<>();
    CardScalar low;
    CardScalar value;
    CardScalar high;


    public BoundingScalerEffect(){}

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = getValue(pContext,context);
        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistantCardEffectContext pContext, CardEffectContext context) {
        return Math.min(Math.max(value.getValue(pContext, context), low.getValue(pContext,context)), high.getValue(pContext, context));
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.bounding_scalar",
                            value.appendInlineTooltip(world, tooltip, tooltipContext),
                            low.appendInlineTooltip(world, tooltip, tooltipContext),
                            high.appendInlineTooltip(world, tooltip, tooltipContext))));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.bounding_scaler.inline"
                ,value.appendInlineTooltip(world, tooltip, tooltipContext)
                ,low.appendInlineTooltip(world, tooltip, tooltipContext)
                ,high.appendInlineTooltip(world, tooltip, tooltipContext));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public BoundingScalerEffect readFromJson(Identifier id, JsonElement entry) {
            BoundingScalerEffect output = new BoundingScalerEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.low = CardSerializer.readOrDefaultScalar(id,"low",entry,DEFAULT_MINIMUM);
            output.high = CardSerializer.readOrDefaultScalar(id,"high",entry,DEFAULT_MAXIMUM);
            if(output.low == DEFAULT_MINIMUM && output.high == DEFAULT_MAXIMUM){
                Battlecards.LOGGER.warn("Bounded Scalar in {} applies no bounds!", id.toString());
            }
            output.value = CardSerializer.readOrDefaultScalar(id,"value",entry,new XScalerEffect());
            return output;
        }
    }
}
