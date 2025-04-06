package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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

public class HealEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    float healing;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(target != null && target instanceof LivingEntity livingEntity) {
            livingEntity.heal(healing);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.heal",healing)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                HealEntityEffect output = new HealEntityEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (!obj.has("amount")) {
                    Battlecards.LOGGER.error("Healing effect missing amount!");
                }
                else{
                    output.healing = obj.get("amount").getAsInt();
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing healing effect!");
                return new HealEntityEffect();
            }
        }
    }
}
