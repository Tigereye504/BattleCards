package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;

import java.util.List;

public class BreathEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    int amount;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        target.setAir(target.getAir()+amount);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.breath",amount)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                BreathEntityEffect output = new BreathEntityEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (!obj.has("amount")) {
                    Battlecards.LOGGER.error("Breath effect missing amount!");
                }
                else{
                    output.amount = obj.get("amount").getAsInt();
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing breath effect!");
                return new BreathEntityEffect();
            }
        }
    }
}
