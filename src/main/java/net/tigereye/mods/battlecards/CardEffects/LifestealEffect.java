package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardAfterDamageEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;

import java.util.List;

public class LifestealEffect implements CardEffect, CardAfterDamageEffect, CardTooltipNester {

    float ratio;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,0,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, float damageDealt, ItemStack item, BattleCard battleCard) {
        if(user instanceof LivingEntity livingEntity) {
            livingEntity.heal(damageDealt*ratio);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.lifesteal",ratio)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                LifestealEffect output = new LifestealEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (!obj.has("amount")) {
                    Battlecards.LOGGER.error("Healing effect missing amount!");
                }
                else{
                    output.ratio = obj.get("amount").getAsFloat();
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing lifesteal effect!");
                return new LifestealEffect();
            }
        }
    }
}
