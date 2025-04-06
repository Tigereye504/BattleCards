package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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

public class ApplyStatusEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    StatusEffect type = null;
    int duration = 0;
    int magnitude = 0;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
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
        public ApplyStatusEntityEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                JsonObject obj = entry.getAsJsonObject();
                ApplyStatusEntityEffect output = new ApplyStatusEntityEffect();
                if (!obj.has("type")) {
                    Battlecards.LOGGER.error("Status effect missing type in {}!",id);
                }
                else {
                    Identifier statusEffectID = new Identifier(obj.get("type").getAsString());
                    output.type = Registries.STATUS_EFFECT.get(statusEffectID);
                    if(output.type == null) {
                        Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
                    }
                }

                if (obj.has("duration")) {
                    output.duration = obj.get("duration").getAsInt();
                }
                if (obj.has("magnitude")) {
                    output.magnitude = obj.get("magnitude").getAsInt();
                }

                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing damage effect!");
                return new ApplyStatusEntityEffect();
            }
        }
    }
}
