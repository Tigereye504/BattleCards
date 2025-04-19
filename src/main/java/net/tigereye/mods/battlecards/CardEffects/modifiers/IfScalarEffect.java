package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class IfScalarEffect implements CardEffect, CardTooltipNester {

    float amount = 0;
    List<CardEffect> effects = new ArrayList<>();
    boolean greaterElseLesser = true;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if (greaterElseLesser ? context.scalar >= amount : context.scalar <= amount){
            for(CardEffect effect : effects) {
                effect.apply(pContext,context);
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.if_scaler",
                        greaterElseLesser ? "greater than" : "less than",
                        amount)));
        for(CardEffect effect : effects){
            if(effect instanceof CardTooltipNester nester){
                nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfScalarEffect readFromJson(Identifier id, JsonElement entry) {
            IfScalarEffect output = new IfScalarEffect();

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on status in {}!",id);
            }

            output.amount = CardSerializer.readOrDefaultFloat(id,"amount",entry,0);
            output.greaterElseLesser = CardSerializer.readOrDefaultBoolean(id,"greaterElseLesser",entry,true);

            return output;
        }
    }
}
