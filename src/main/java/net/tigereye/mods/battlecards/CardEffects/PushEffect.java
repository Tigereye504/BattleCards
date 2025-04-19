package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class PushEffect implements CardEffect, CardTooltipNester {

    public float pitch = 0;
    public float yaw = 0;
    public float magnitude = 0.3f;
    public boolean pushRelativeToUserElseTarget = true;
    public boolean applyKnockbackRes = true;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(pContext,context.target);
        }
        else {
            apply(pContext, pContext.user);
        }
    }

    private void apply(PersistantCardEffectContext pContext, Entity target) {
        Entity relativeEntity = pushRelativeToUserElseTarget ? pContext.user : target;
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
        public PushEffect readFromJson(Identifier id, JsonElement entry) {
            PushEffect output = new PushEffect();

            output.pitch = CardSerializer.readOrDefaultInt(id, "pitch",entry,0);
            output.yaw = CardSerializer.readOrDefaultInt(id, "yaw",entry,0);
            output.magnitude = CardSerializer.readOrDefaultFloat(id, "magnitude",entry,0.3f);
            output.pushRelativeToUserElseTarget = CardSerializer.readOrDefaultBoolean(id, "angleRelativeToUser",entry,true);
            output.applyKnockbackRes = CardSerializer.readOrDefaultBoolean(id, "applyKnockback",entry,true);

            return output;
        }
    }
}
