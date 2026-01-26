package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class IfGroundedCondition implements CardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    List<CardEffect> falseEffects = new ArrayList<>();
    boolean targetElseUser = true;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(pContext,context.target, context);
        }
        else {
            apply(pContext, pContext.user, context);
        }
    }

    private void apply(PersistentCardEffectContext pContext, Entity target, CardEffectContext context) {
        Entity subject = targetElseUser ? target : pContext.user;
        if(subject instanceof LivingEntity livingEntity) {
            if (subject.isOnGround()){
                for(CardEffect effect : effects) {
                    effect.apply(pContext,context);
                }
            }
            else{
                for(CardEffect falseEffect : falseEffects) {
                    falseEffect.apply(pContext,context);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(effects != null){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_grounded",
                            targetElseUser ? "target" : "user")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(falseEffects != null){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_grounded.false",
                            targetElseUser ? "target" : "user")));
            for(CardEffect effect : falseEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfGroundedCondition readFromJson(Identifier id, JsonElement entry) {
            IfGroundedCondition output = new IfGroundedCondition();

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.falseEffects = CardSerializer.readCardEffects(id, "falseEffects",entry);
            if (output.effects.isEmpty() && output.falseEffects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on grounded in {}!",id);
            }

            output.targetElseUser = CardSerializer.readOrDefaultBoolean(id,"targetElseUser",entry,true);

            return output;
        }
    }
}
