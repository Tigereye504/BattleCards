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
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class TargetTrackedEntityEffect implements CardEffect, CardScalar, CardTooltipNester {
    List<CardEffect> effects = new ArrayList<>();
    boolean trackOldTarget = true;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = makeNewContext(pContext,context);
        if(newContext.target != null) {
            for (CardEffect effect : effects) {
                effect.apply(pContext, newContext);
            }
        }
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    @Override
    public float getValue(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(!effects.isEmpty()) {
            if(effects.get(0) instanceof CardScalar scalar){
                CardEffectContext newContext = makeNewContext(pContext,context);
                if(newContext.target != null) {
                    return scalar.getValue(pContext, newContext);
                }
            }
        }
        return 0;
    }

    private CardEffectContext makeNewContext(PersistantCardEffectContext pContext, CardEffectContext context){
        CardEffectContext newContext = context.clone();
        newContext.target = context.trackedEntity;
        if(trackOldTarget){
            newContext.trackedEntity = context.target;
        }
        newContext.hitResult = null;
        return newContext;
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.target_projectile")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    @Override
    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return CardScalar.super.appendInlineTooltip(world, tooltip, tooltipContext);
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public TargetTrackedEntityEffect readFromJson(Identifier id, JsonElement entry) {
            TargetTrackedEntityEffect output = new TargetTrackedEntityEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Missing effects for TargetTrackedEntity modifier in {}.",id);
            }
            output.trackOldTarget = CardSerializer.readOrDefaultBoolean(id, "trackOldTarget",entry,true);
            return output;
        }
    }
}
