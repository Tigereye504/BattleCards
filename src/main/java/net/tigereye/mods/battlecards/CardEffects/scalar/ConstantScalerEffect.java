package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class ConstantScalerEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    float amount = 0;

    public ConstantScalerEffect(){}

    public ConstantScalerEffect(float amount){this.amount = amount;}

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = amount;
        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistantCardEffectContext pContext, CardEffectContext context) {
        return amount;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.absolute_scaler",
                            amount)));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.absolute_scaler.inline",amount);
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ConstantScalerEffect readFromJson(Identifier id, JsonElement entry) {
            ConstantScalerEffect output = new ConstantScalerEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.amount = CardSerializer.readOrDefaultFloat(id,"amount",entry,0);
            return output;
        }
    }
}
