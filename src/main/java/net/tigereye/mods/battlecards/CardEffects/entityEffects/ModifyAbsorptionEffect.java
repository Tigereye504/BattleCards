package net.tigereye.mods.battlecards.CardEffects.entityEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyAbsorptionEffect implements CardEffect, CardTooltipNester {

    CardScalar amount;
    CardScalar scalingAmount;
    boolean modifyElseSet = true;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        Entity target;
        if(context.target != null){
            target = context.target;
        }
        else {
            target = pContext.user;
        }
        if(target instanceof LivingEntity livingEntity) {
            float totalAmount = amount.getValue(pContext, context) + (scalingAmount.getValue(pContext, context)*context.scalar);
            if(modifyElseSet) {
                livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + totalAmount);
            }
            else{
                livingEntity.setAbsorptionAmount(totalAmount);
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(modifyElseSet) {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.modify_absorption",
                            amount.appendInlineTooltip(world, tooltip, tooltipContext).getString(),
                            scalingAmount.appendInlineTooltip(world, tooltip, tooltipContext).getString() + "X")));
        }
        else{
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.modify_absorption.set",
                            amount.appendInlineTooltip(world, tooltip, tooltipContext).getString(),
                            scalingAmount.appendInlineTooltip(world, tooltip, tooltipContext).getString() + "X")));
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyAbsorptionEffect output = new ModifyAbsorptionEffect();
            output.amount = CardSerializer.readOrDefaultScalar(id, "amount",entry,0);
            output.scalingAmount = CardSerializer.readOrDefaultScalar(id, "scalingAmount",entry,0);
            return output;
        }
    }
}
