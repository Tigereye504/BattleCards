package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
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

public class HungerScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    int nonPlayerHungerPerEffectLevel = 3;
    boolean userElseTarget = true;
    boolean missingElseCurrent = true;
    boolean replaceElseAdd = true;

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
        Entity scalarEntity = userElseTarget ? pContext.user : context.target;
        int hungerlevel = 20;
        if(scalarEntity instanceof PlayerEntity playerEntity){
            hungerlevel = playerEntity.getHungerManager().getFoodLevel();
        }
        else if(scalarEntity instanceof LivingEntity livingEntity){
            hungerlevel = Math.max(0,
                    20 - (nonPlayerHungerPerEffectLevel*(livingEntity.getStatusEffect(StatusEffects.HUNGER).getAmplifier()+1)));
        }
        return (replaceElseAdd ? 0 : context.scalar) +
                (missingElseCurrent ? 20 - hungerlevel : hungerlevel);
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.hunger_scaler",
                            replaceElseAdd ? "" : "X + ",
                            userElseTarget ? "User's" : "Target's",
                            missingElseCurrent ? "Missing" : "Current")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public HungerScalarEffect readFromJson(Identifier id, JsonElement entry) {
            HungerScalarEffect output = new HungerScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Missing effects for HungerScaler modifier in {}.",id);
            }

            output.nonPlayerHungerPerEffectLevel = CardSerializer.readOrDefaultInt(id,"nonPlayerHungerPerEffectLevel",entry,3);
            output.userElseTarget = CardSerializer.readOrDefaultBoolean(id,"userElseTarget",entry,true);
            output.missingElseCurrent = CardSerializer.readOrDefaultBoolean(id,"missingElseCurrent",entry,true);
            output.replaceElseAdd = CardSerializer.readOrDefaultBoolean(id,"replaceElseAdd",entry,true);
            return output;
        }
    }
}
