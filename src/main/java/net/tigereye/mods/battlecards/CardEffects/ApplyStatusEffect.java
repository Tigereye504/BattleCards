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
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ApplyStatusEffect implements CardEffect, CardTooltipNester {

    StatusEffect type = null;
    int duration = 0;
    int magnitude = 0;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        if(context.target != null){
            apply(user,context.target,item,battleCard);
        }
        else {
            apply(user, user, item, battleCard);
        }
    }

    private void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(type != null && target instanceof LivingEntity livingEntity){
            livingEntity.addStatusEffect(new StatusEffectInstance(type,duration,magnitude));
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type == null){
            tooltip.add(Text.literal(" ".repeat(depth)+"Malformed Apply Status Card Effect"));
            return;
        }
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.status",type.getName(), magnitude+1, ((float)duration)/20)));
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

            output.duration = CardSerializer.readOrDefaultInt(id,"duration",entry,0);
            output.magnitude = CardSerializer.readOrDefaultInt(id,"magnitude",entry,0);

            return output;
        }
    }
}
