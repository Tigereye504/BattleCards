package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class RandomScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    CardScalar minimum = null;
    CardScalar maximum = null;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = getValue(pContext, context);
        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistentCardEffectContext pContext, CardEffectContext context) {
        float min = minimum.getValue(pContext,context);
        float max = maximum.getValue(pContext,context);
        Random random = pContext.user.getWorld().getRandom();
        return min+(random.nextFloat()*(max-min));
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.random_scalar",
                            minimum,maximum)));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext){
        return Text.translatable("card.battlecards.tooltip.random_scalar.inline",
            minimum.appendInlineTooltip(world, tooltip, tooltipContext),
            maximum.appendInlineTooltip(world, tooltip, tooltipContext));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public RandomScalarEffect readFromJson(Identifier id, JsonElement entry) {
            RandomScalarEffect output = new RandomScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.minimum = CardSerializer.readOrDefaultScalar(id,"minimum",entry,0);
            output.maximum = CardSerializer.readOrDefaultScalar(id,"maximum",entry,1);
            return output;
        }
    }
}
