package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.OnCollisionCardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class TargetUserEffect implements CardEffect, CardTargetEntityEffect, OnCollisionCardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        for(CardEffect effect : effects){
            effect.apply(user, item, battleCard);
        }
    }

    @Override
    public void apply(Entity user, HitResult hitResult, ItemStack item, BattleCard battleCard) {
        for(CardEffect effect : effects){
            effect.apply(user, item, battleCard);
        }
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.target_user")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public TargetUserEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                TargetUserEffect output = new TargetUserEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (obj.has("effects")) {
                    JsonArray onHitJson = obj.get("effects").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        output.addCardEffect(effect);
                    }
                }
                else{
                    Battlecards.LOGGER.error("Missing effects for TargetUser modifier in {}.",id);
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing throw card effect!");
                return new TargetUserEffect();
            }
        }
    }
}
