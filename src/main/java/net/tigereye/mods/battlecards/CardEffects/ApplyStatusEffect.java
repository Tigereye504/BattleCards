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
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.AbsoluteScalerEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ApplyStatusEffect implements CardEffect, CardTooltipNester {

    StatusEffect type = null;
    CardScalar duration = new AbsoluteScalerEffect(1);
    CardScalar magnitude = new AbsoluteScalerEffect(0);

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(pContext,context.target, context);
        }
        else {
            apply(pContext, pContext.user, context);
        }
    }

    private void apply(PersistantCardEffectContext pContext, Entity target, CardEffectContext context) {
        if(type != null && target instanceof LivingEntity livingEntity){
            livingEntity.addStatusEffect(new StatusEffectInstance(type,(int)duration.getValue(pContext,context),(int)magnitude.getValue(pContext, context)));
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type == null){
            tooltip.add(Text.literal(" ".repeat(depth)+"Malformed Apply Status Card Effect"));
            return;
        }
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.status",type.getName(),
                        "magnitude "+magnitude.appendInlineTooltip(world, tooltip, tooltipContext),
                        duration.appendInlineTooltip(world, tooltip, tooltipContext)+" ticks")));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ApplyStatusEffect readFromJson(Identifier id, JsonElement entry) {
            JsonObject obj = entry.getAsJsonObject();
            ApplyStatusEffect output = new ApplyStatusEffect();

            Identifier statusEffectID = new Identifier(CardSerializer.readOrDefaultString(id,"type",entry,""));
            output.type = Registries.STATUS_EFFECT.get(statusEffectID);
            if(output.type == null) {
                Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
            }

            output.duration = CardSerializer.readOrDefaultScalar(id,"duration",entry,1);
            output.magnitude = CardSerializer.readOrDefaultScalar(id,"magnitude",entry,0);

            return output;
        }
    }
}
