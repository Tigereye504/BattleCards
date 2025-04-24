package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
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

import java.util.ArrayList;
import java.util.List;

public class ClearStatusEffect implements CardEffect, CardTooltipNester {

    StatusEffect type = null;
    CardScalar count = new AbsoluteScalerEffect(0);
    boolean targetPositive = true;
    boolean targetNegative = true;

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
        int count = (int) Math.floor(this.count.getValue(pContext,context));
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
                    Text.translatable("card.battlecards.tooltip.clear_status_count", count.appendInlineTooltip(world, tooltip, tooltipContext))));
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ClearStatusEffect readFromJson(Identifier id, JsonElement entry) {
            ClearStatusEffect output = new ClearStatusEffect();

            String statusEffect = CardSerializer.readOrDefaultString(id,"type",entry,"");
            if(!statusEffect.isEmpty()) {
                Identifier statusEffectID = new Identifier(statusEffect);
                output.type = Registries.STATUS_EFFECT.get(statusEffectID);
                if (output.type == null) {
                    Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
                }
            }

            output.count = CardSerializer.readOrDefaultScalar(id,"count",entry,0);
            output.targetPositive = CardSerializer.readOrDefaultBoolean(id,"targetPositive",entry,true);
            output.targetNegative = CardSerializer.readOrDefaultBoolean(id,"targetNegative",entry,true);

            return output;
        }
    }
}
