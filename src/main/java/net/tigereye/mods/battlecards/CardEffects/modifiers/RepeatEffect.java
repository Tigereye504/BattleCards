package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.CardEffects.scalar.XScalarEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Util.DelayedActionTaker;

import java.util.ArrayList;
import java.util.List;

public class RepeatEffect implements CardEffect, CardTooltipNester {

    CardScalar startingScalar = new XScalarEffect();
    CardScalar incrementScalar = new XScalarEffect();
    CardScalar count = new ConstantScalarEffect(1);
    CardScalar delay = new ConstantScalarEffect(0);
    List<CardEffect> effects = new ArrayList<>();

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {

        int countValue = (int)(Math.floor(count.getValue(pContext,context)));
        int delayValue = (int)(Math.floor(delay.getValue(pContext,context)));
        context.scalar = startingScalar.getValue(pContext, context);
        for (int i = 0; i < countValue; i++) {
            if(delayValue > 0 && i > 0 && pContext.user instanceof DelayedActionTaker dat) {
                int finalI = i;
                effects.forEach((effect) ->
                        dat.battleCards$addDelayedAction(new DelayedEffect.DelayedCardAction(
                                pContext, context.clone(), effect, delayValue * finalI)));
            }
            else{
                effects.forEach((effect) ->
                        effect.apply(pContext, context.clone()));
            }
            context.scalar = incrementScalar.getValue(pContext, context);
        }
    }

    private void addEffect(CardEffect cteEffect) {
        effects.add(cteEffect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.repeat",count.appendInlineTooltip(world, tooltip, tooltipContext))));
        if(!effects.isEmpty()){
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            RepeatEffect output = new RepeatEffect();

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Repeat Effect missing effect!");
            }

            output.startingScalar = CardSerializer.readOrDefaultScalar(id, "startingScalar", entry, new XScalarEffect());
            output.incrementScalar = CardSerializer.readOrDefaultScalar(id, "incrementScalar", entry, new XScalarEffect());
            output.count = CardSerializer.readOrDefaultScalar(id, "count", entry, 1);
            output.delay = CardSerializer.readOrDefaultScalar(id, "delay", entry, 0);

            return output;
        }
    }
}
