package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class AbsorptionScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    boolean userElseTarget = true;
    boolean replaceElseAdd = true;
    boolean absoluteElseRatio = true;
    //'absolute' returns the entity's absorption value
    //'ratio' returns the entity's absorption as a portion of the entity's max health

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
        Entity scalarEntity = userElseTarget ? pContext.user : context.target;
        if(scalarEntity instanceof LivingEntity livingEntity){
            return (replaceElseAdd ? 0 : context.scalar) + (livingEntity.getAbsorptionAmount()
                            / (absoluteElseRatio ? 1 : livingEntity.getMaxHealth()));
        }
        else {
            return replaceElseAdd ? 0 : context.scalar;
        }
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.absorption_scalar",
                            replaceElseAdd ? "" : "X + ",
                            userElseTarget ? "User's" : "Target's",
                            absoluteElseRatio ? "" : "% ")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public AbsorptionScalarEffect readFromJson(Identifier id, JsonElement entry) {
            AbsorptionScalarEffect output = new AbsorptionScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.absoluteElseRatio = CardSerializer.readOrDefaultBoolean(id,"absoluteElseRatio",entry,true);
            output.userElseTarget = CardSerializer.readOrDefaultBoolean(id,"userElseTarget",entry,true);
            output.replaceElseAdd = CardSerializer.readOrDefaultBoolean(id,"replaceElseAdd",entry,true);
            return output;
        }
    }
}
