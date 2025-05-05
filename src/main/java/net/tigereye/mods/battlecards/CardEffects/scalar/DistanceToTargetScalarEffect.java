package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
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

public class DistanceToTargetScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    boolean userElseTrackedEntity = true;

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
        Entity scalarEntity = userElseTrackedEntity ? pContext.user : context.trackedEntity;
        if(scalarEntity == null || context.target == null){
            return 0;
        }
        return scalarEntity.distanceTo(context.target);
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.distance_to_target_scalar")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public DistanceToTargetScalarEffect readFromJson(Identifier id, JsonElement entry) {
            DistanceToTargetScalarEffect output = new DistanceToTargetScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.userElseTrackedEntity = CardSerializer.readOrDefaultBoolean(id,"userElseTrackedEntity",entry,true);
            return output;
        }
    }
}
