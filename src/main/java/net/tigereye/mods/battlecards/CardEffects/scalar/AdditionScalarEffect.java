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

public class AdditionScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    CardScalar a;
    CardScalar b;


    public AdditionScalarEffect(){}

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
        return a.getValue(pContext, context)+b.getValue(pContext, context);
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.addition_scalar",
                            a.appendInlineTooltip(world, tooltip, tooltipContext),
                            b.appendInlineTooltip(world, tooltip, tooltipContext))));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.addition_scalar.inline"
                ,a.appendInlineTooltip(world, tooltip, tooltipContext)
                ,b.appendInlineTooltip(world, tooltip, tooltipContext));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public AdditionScalarEffect readFromJson(Identifier id, JsonElement entry) {
            AdditionScalarEffect output = new AdditionScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.a = CardSerializer.readOrDefaultScalar(id,"a",entry,null);
            if(output.a == null){
                Battlecards.LOGGER.warn("Addition Scalar missing left hand scalar!");
                output.a = new ConstantScalarEffect(0);
            }
            output.b = CardSerializer.readOrDefaultScalar(id,"b",entry,null);
            if(output.b == null){
                Battlecards.LOGGER.warn("Addition Scalar missing right hand scalar!");
                output.a = new ConstantScalarEffect(0);
            }
            return output;
        }
    }
}
