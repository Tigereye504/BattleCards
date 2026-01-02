package net.tigereye.mods.battlecards.CardEffects.modifiers;

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
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class IfScalarEffect implements CardEffect, CardTooltipNester {

    CardScalar amount = new ConstantScalarEffect(0);
    List<CardEffect> effects = new ArrayList<>();
    List<CardEffect> falseEffects = new ArrayList<>();
    boolean greaterElseLesser = true;
    boolean acceptEqual = false;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        float amount = this.amount.getValue(pContext,context);
        if (greaterElseLesser ? context.scalar > amount : context.scalar < amount){
            for(CardEffect effect : effects) {
                effect.apply(pContext,context);
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()) {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_scalar",
                            (greaterElseLesser ? "greater than" : "less than") +
                                    (acceptEqual ? " or equal to" : ""),
                            amount.appendInlineTooltip(world, tooltip, tooltipContext))));
            for (CardEffect effect : effects) {
                if (effect instanceof CardTooltipNester nester) {
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                }
            }
        }
        if(!falseEffects.isEmpty()) {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_scalar",
                            (!greaterElseLesser ? "greater than" : "less than") +
                                    (!acceptEqual ? " or equal to" : ""),
                            amount.appendInlineTooltip(world, tooltip, tooltipContext))));
            for (CardEffect effect : falseEffects) {
                if (effect instanceof CardTooltipNester nester) {
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfScalarEffect readFromJson(Identifier id, JsonElement entry) {
            IfScalarEffect output = new IfScalarEffect();

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.falseEffects = CardSerializer.readCardEffects(id, "falseEffects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on if scalar in {}!",id);
            }

            output.amount = CardSerializer.readOrDefaultScalar(id,"amount",entry,0);
            output.greaterElseLesser = CardSerializer.readOrDefaultBoolean(id,"greaterElseLesser",entry,true);
            output.acceptEqual = CardSerializer.readOrDefaultBoolean(id,"acceptEqual",entry,false);

            return output;
        }
    }
}
