package net.tigereye.mods.battlecards.CardEffects.entityEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyBreathEffect implements CardEffect, CardTooltipNester {

    CardScalar amount;
    boolean modifyElseSet;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        Entity entity;
        if(context.target != null){
            entity = context.target;
        }
        else {
            entity = pContext.user;
        }
        entity.setAir((modifyElseSet ? pContext.user.getAir() : 0)+((int)amount.getValue(pContext, context)));
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable(modifyElseSet ? "card.battlecards.tooltip.modify_breath" : "card.battlecards.tooltip.modify_breath.set",
                        amount.appendInlineTooltip(world, tooltip, tooltipContext).getString())));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyBreathEffect output = new ModifyBreathEffect();
            output.amount = CardSerializer.readOrDefaultScalar(id,"amount",entry,0);
            output.modifyElseSet = CardSerializer.readOrDefaultBoolean(id,"modifyElseSet",entry,true);
            return output;
        }
    }
}
