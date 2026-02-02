package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
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

public class IfStatusCondition implements CardEffect, CardTooltipNester {

    List<StatusEffect> type = new ArrayList<>();;
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
            boolean match = false;
            for(StatusEffect status : type){
                if(livingEntity.hasStatusEffect(status)){
                    match = true;
                    break;
                }
            }
            if (match){
                for(CardEffect effect : effects) {
                    effect.apply(pContext,context);
                }
            }
            else {
                for(CardEffect effect : falseEffects) {
                    effect.apply(pContext,context);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type != null){
            StringBuilder statusListText = new StringBuilder();
            if(!effects.isEmpty() || !falseEffects.isEmpty()) {
                boolean notFirst = false;
                for (StatusEffect status : type) {
                    if (notFirst) {
                        statusListText.append(", ");
                    } else {
                        notFirst = true;
                    }
                    statusListText.append(status.getName().getString());
                }
            }
            if(!effects.isEmpty()) {
                tooltip.add(Text.literal(" ".repeat(depth)).append(
                        Text.translatable("card.battlecards.tooltip.if_status",
                                targetElseUser ? "target" : "user",
                                statusListText.toString())));
                for (CardEffect effect : effects) {
                    if (effect instanceof CardTooltipNester nester) {
                        nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                    }
                }
            }
            if(!falseEffects.isEmpty()) {
                tooltip.add(Text.literal(" ".repeat(depth)).append(
                        Text.translatable("card.battlecards.tooltip.if_status.false",
                                targetElseUser ? "target" : "user",
                                statusListText.toString())));
                for (CardEffect effect : falseEffects) {
                    if (effect instanceof CardTooltipNester nester) {
                        nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                    }
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfStatusCondition readFromJson(Identifier id, JsonElement entry) {
            IfStatusCondition output = new IfStatusCondition();

            for(String string : CardSerializer.readOrDefaultStringList(id,"type",entry,new ArrayList<>())){
                try {
                    StatusEffect effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(string));
                    if (effect != null) {
                        output.type.add(effect);
                    }
                    else{
                        Battlecards.LOGGER.error("cannot find status effect {} from list in {}!",string,id.toString());
                    }
                }
                catch(Exception e){
                    Battlecards.LOGGER.error("error reading status effect {} from list in {}!",string,id.toString());
                }
            }

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.falseEffects = CardSerializer.readCardEffects(id, "falseEffects",entry);
            if (output.effects.isEmpty() && output.falseEffects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on status in {}!",id);
            }

            output.targetElseUser = CardSerializer.readOrDefaultBoolean(id,"targetPositive",entry,true);

            return output;
        }
    }
}
