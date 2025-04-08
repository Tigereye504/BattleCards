package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;

import java.util.List;

public class PushEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    public float pitch = 0;
    public float yaw = 0;
    public float magnitude = 0.3f;
    public boolean pushRelativeToUserElseTarget = true;
    public boolean applyKnockbackRes = true;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        Entity relativeEntity = pushRelativeToUserElseTarget ? user : target;
        Vec3d pushVector = relativeEntity.getCameraPosVec(1).normalize().multiply(magnitude)
                .rotateX(pitch)
                .rotateY(yaw);
        double KBRes = 0;
        if(target instanceof LivingEntity lEntity){
            KBRes = lEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        }
        target.addVelocity(pushVector.multiply(Math.max(0,1-KBRes)));
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.push",magnitude)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public PushEntityEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                JsonObject obj = entry.getAsJsonObject();
                PushEntityEffect output = new PushEntityEffect();
                if (obj.has("pitch")) {
                    output.pitch = obj.get("pitch").getAsFloat();
                }
                if (obj.has("yaw")) {
                    output.yaw = obj.get("yaw").getAsFloat();
                }
                if (obj.has("magnitude")) {
                    output.magnitude = obj.get("magnitude").getAsFloat();
                }
                if (obj.has("angleRelativeToUser")) {
                    output.pushRelativeToUserElseTarget = obj.get("angleRelativeToUser").getAsBoolean();
                }
                if (obj.has("applyKnockback")) {
                    output.applyKnockbackRes = obj.get("applyKnockback").getAsBoolean();
                }

                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing knockback effect!");
                return new PushEntityEffect();
            }
        }
    }
}
