package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class ConstantScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    float amount = 0;

    public ConstantScalarEffect(){}

    public ConstantScalarEffect(float amount){this.amount = amount;}

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = amount;
        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistentCardEffectContext pContext, CardEffectContext context) {
        return amount;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.constant_scalar",
                            amount)));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.constant_scalar.inline",
                amount == Float.MAX_VALUE ? "∞" : amount == Float.MIN_VALUE ? "-∞" : amount);
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ConstantScalarEffect readFromJson(Identifier id, JsonElement entry) {
            ConstantScalarEffect output = new ConstantScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.amount = CardSerializer.readOrDefaultFloat(id,"amount",entry,0);
            return output;
        }
    }
}
