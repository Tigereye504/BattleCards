package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class IfStatusEffect implements CardEffect, CardTooltipNester {

    StatusEffect type = null;
    List<CardEffect> effects = new ArrayList<>();
    boolean targetElseUser = true;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        if(context.target != null){
            apply(user,context.target,item,battleCard, context);
        }
        else {
            apply(user, user, item, battleCard, context);
        }
    }

    private void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        Entity statusHaver = targetElseUser ? target : user;
        if(statusHaver instanceof LivingEntity livingEntity) {
            if (type != null && livingEntity.hasStatusEffect(type)){
                for(CardEffect effect : effects) {
                    effect.apply(user,item,battleCard,context);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type != null){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_status",
                            targetElseUser ? "target" : "user",
                            type.getName())));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfStatusEffect readFromJson(Identifier id, JsonElement entry) {
            IfStatusEffect output = new IfStatusEffect();

            Identifier statusEffectID = new Identifier(CardSerializer.readOrDefaultString(id,"type",entry,""));
            output.type = Registries.STATUS_EFFECT.get(statusEffectID);
            if(output.type == null) {
                Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
            }

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on status in {}!",id);
            }

            output.targetElseUser = CardSerializer.readOrDefaultBoolean(id,"targetPositive",entry,true);

            return output;
        }
    }
}
