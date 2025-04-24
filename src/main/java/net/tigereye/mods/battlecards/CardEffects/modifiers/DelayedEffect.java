package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.AbsoluteScalerEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Util.DelayedAction;
import net.tigereye.mods.battlecards.Util.DelayedActionTaker;

import java.util.ArrayList;
import java.util.List;

public class DelayedEffect implements CardEffect, CardTooltipNester {

    CardScalar delay = new AbsoluteScalerEffect(1);
    List<CardEffect> effects = new ArrayList<>();

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(pContext.user instanceof DelayedActionTaker dat){
            dat.battleCards$addDelayedAction(new DelayedCardAction(pContext,context,effects,(int)delay.getValue(pContext,context)));
        }
    }

    private void addEffect(CardEffect cteEffect) {
        effects.add(cteEffect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.delay_entity",delay.appendInlineTooltip(world, tooltip, tooltipContext))));
        if(!effects.isEmpty()){
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    private static class DelayedCardAction extends DelayedAction {

        PersistantCardEffectContext pContext;
        Entity target;
        CardEffectContext context;
        List<CardEffect> effects;


        DelayedCardAction(PersistantCardEffectContext pContext, CardEffectContext context, List<CardEffect> effects, int delay){
            this.pContext = pContext;
            this.context = context;
            this.effects = effects;
            setTicks(delay);
        }

        @Override
        public void act() {
            for(CardEffect effect : effects){
                effect.apply(pContext,context);
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            DelayedEffect output = new DelayedEffect();

            output.effects = CardSerializer.readCardEffects(id, "after_delay",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Delay Entity Effect missing effect!");
            }

            output.delay = CardSerializer.readOrDefaultScalar(id, "delay", entry, 1);

            return output;
        }
    }
}
