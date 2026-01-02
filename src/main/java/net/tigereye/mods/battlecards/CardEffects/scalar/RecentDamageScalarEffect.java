package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class RecentDamageScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    int maxTicks = 20;
    boolean userElseTarget = true;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = getValue(pContext,context);
        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistantCardEffectContext pContext, CardEffectContext context) {
        Entity scalarEntity = userElseTarget ? pContext.user : context.target;
        if(scalarEntity instanceof LivingEntity livingEntity && maxTicks >= livingEntity.getDamageTracker().getTimeSinceLastAttack()){
            return livingEntity.lastDamageTaken;
        }
        return 0;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.recent_damage_scalar",
                        userElseTarget ? "User's" : "Target's",
                        maxTicks)));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    @Override
    public Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext) {
        return Text.translatable("card.battlecards.tooltip.recent_damage_scalar.inline",
                userElseTarget ? "User's" : "Target's",
                maxTicks);
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public RecentDamageScalarEffect readFromJson(Identifier id, JsonElement entry) {
            RecentDamageScalarEffect output = new RecentDamageScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.maxTicks = CardSerializer.readOrDefaultInt(id,"maxTicks",entry,20);
            output.userElseTarget = CardSerializer.readOrDefaultBoolean(id,"userElseTarget",entry,true);
            return output;
        }
    }
}
