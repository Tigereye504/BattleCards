package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyHealthEffect implements CardEffect, CardTooltipNester {

    float amount;
    float scalingAmount;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(context.target, context);
        }
        else {
            apply(pContext.user, context);
        }
    }

    private void apply(Entity target, CardEffectContext context) {
        if(target instanceof LivingEntity livingEntity) {
            float totalAmount = amount + (scalingAmount*context.scalar);
            if(totalAmount > 0) {
                livingEntity.heal(totalAmount);
            }
            else if(totalAmount < 0){
                livingEntity.setHealth((float)Math.max(0.01,livingEntity.getHealth() + totalAmount));
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.modify_health", amount,
                        scalingAmount > 0 ? " + "+scalingAmount+"X" : "")));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyHealthEffect output = new ModifyHealthEffect();
            output.amount = CardSerializer.readOrDefaultFloat(id, "amount",entry,0);
            output.scalingAmount = CardSerializer.readOrDefaultFloat(id, "scalingAmount",entry,0);
            return output;
        }
    }
}
