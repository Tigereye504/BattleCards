package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;

import java.util.ArrayList;
import java.util.List;

public class ClearStatusEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    StatusEffect type = null;
    int count = 0;
    boolean targetPositive = true;
    boolean targetNegative = true;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(target instanceof LivingEntity livingEntity) {
            if (type != null){
                livingEntity.removeStatusEffect(type);
            }
            else {
                int amountToClear = 0;
                List<StatusEffect> toClear = new ArrayList<>();
                for(StatusEffectInstance instance : livingEntity.getStatusEffects()){
                    if(amountToClear >= count){
                        break;
                    }
                    if((instance.getEffectType().isBeneficial() && targetPositive)
                            || (!instance.getEffectType().isBeneficial() && targetNegative)){
                        toClear.add(instance.getEffectType());
                        amountToClear++;
                    }
                }
                for(StatusEffect effect : toClear){
                    livingEntity.removeStatusEffect(effect);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type != null){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.clear_status", type.getName())));
        }
        else {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.clear_status_count", count)));
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ClearStatusEntityEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                JsonObject obj = entry.getAsJsonObject();
                ClearStatusEntityEffect output = new ClearStatusEntityEffect();
                if (obj.has("type")) {
                    Identifier statusEffectID = new Identifier(obj.get("type").getAsString());
                    output.type = Registries.STATUS_EFFECT.get(statusEffectID);
                    if(output.type == null) {
                        Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
                    }
                }

                if (obj.has("count")) {
                    output.count = obj.get("count").getAsInt();
                }
                if (obj.has("targetPositive")) {
                    output.targetPositive = obj.get("targetPositive").getAsBoolean();
                }
                if (obj.has("targetNegative")) {
                    output.targetNegative = obj.get("targetNegative").getAsBoolean();
                }

                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing status clear effect!");
                return new ClearStatusEntityEffect();
            }
        }
    }
}
