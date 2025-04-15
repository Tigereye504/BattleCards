package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class HealthScalerEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    boolean userElseTarget = true;
    boolean missingElseCurrent = true;
    boolean replaceElseAdd = true;
    boolean absoluteElseRatio = true;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        Entity scalarEntity = userElseTarget ? user : context.target;
        if(scalarEntity instanceof LivingEntity livingEntity){
            newContext.scalar = (replaceElseAdd ? 0 : newContext.scalar) + (
                (missingElseCurrent ? livingEntity.getMaxHealth() - livingEntity.getHealth() : livingEntity.getHealth())
                / (absoluteElseRatio ? 1 : livingEntity.getMaxHealth()));
        }
        else {
            newContext.scalar = replaceElseAdd ? 0 : newContext.scalar;
        }
        for(CardEffect effect : effects){
            effect.apply(user, item, battleCard, newContext);
        }
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.health_scaler",
                            replaceElseAdd ? "" : "X + ",
                            userElseTarget ? "User's" : "Target's",
                            absoluteElseRatio ? "" : "% ",
                            missingElseCurrent ? "missing" : "current")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public HealthScalerEffect readFromJson(Identifier id, JsonElement entry) {
            HealthScalerEffect output = new HealthScalerEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Missing effects for HealthScaler modifier in {}.",id);
            }

            output.absoluteElseRatio = CardSerializer.readOrDefaultBoolean(id,"absoluteElseRatio",entry,true);
            output.userElseTarget = CardSerializer.readOrDefaultBoolean(id,"userElseTarget",entry,true);
            output.missingElseCurrent = CardSerializer.readOrDefaultBoolean(id,"missingElseCurrent",entry,true);
            output.replaceElseAdd = CardSerializer.readOrDefaultBoolean(id,"replaceElseAdd",entry,true);
            return output;
        }
    }
}
