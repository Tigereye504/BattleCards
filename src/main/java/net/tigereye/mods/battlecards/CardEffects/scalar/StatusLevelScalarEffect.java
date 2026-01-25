package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
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

public class StatusLevelScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    StatusEffect type = null;
    boolean userElseTarget = true;
    boolean replaceElseAdd = true;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = getValue(pContext,context);

        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistentCardEffectContext pContext, CardEffectContext context) {
        if(type != null) {
            Entity scalarEntity = userElseTarget ? pContext.user : context.target;
            if (scalarEntity instanceof LivingEntity livingEntity) {
                int scalar = livingEntity.hasStatusEffect(type) ? livingEntity.getStatusEffect(type).getAmplifier()+1 : 0;
                return replaceElseAdd ? scalar : scalar + context.scalar;
            }
        }
        return replaceElseAdd ? 0 : context.scalar;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.status_magnitude_scalar",
                            replaceElseAdd ? "" : "X + ",
                            userElseTarget ? "User's" : "Target's",
                            type != null ? type.getName() : "MISSING STATUS EFFECT")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.status_magnitude_scalar.inline",
                userElseTarget ? "User's" : "Target's",
                type != null ? type.getName() : "MISSING STATUS EFFECT");
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public StatusLevelScalarEffect readFromJson(Identifier id, JsonElement entry) {
            StatusLevelScalarEffect output = new StatusLevelScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);

            Identifier statusEffectID = new Identifier(CardSerializer.readOrDefaultString(id,"type",entry,""));
            output.type = Registries.STATUS_EFFECT.get(statusEffectID);
            if(output.type == null) {
                Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
            }

            output.userElseTarget = CardSerializer.readOrDefaultBoolean(id,"userElseTarget",entry,true);
            output.replaceElseAdd = CardSerializer.readOrDefaultBoolean(id,"replaceElseAdd",entry,true);
            return output;
        }
    }
}
