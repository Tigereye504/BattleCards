package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
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

public class HungerEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    int hunger = 0;
    int saturation = 0;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(target instanceof PlayerEntity pEntity){
            HungerManager manager = pEntity.getHungerManager();
            manager.setFoodLevel(manager.getFoodLevel()+hunger);
            manager.setSaturationLevel(manager.getSaturationLevel()+saturation);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.hunger",hunger, saturation)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                HungerEntityEffect output = new HungerEntityEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (obj.has("hunger")) {
                    output.hunger = obj.get("hunger").getAsInt();
                }
                if (obj.has("saturation")) {
                    output.saturation = obj.get("saturation").getAsInt();
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing hunger effect!");
                return new HungerEntityEffect();
            }
        }
    }
}
