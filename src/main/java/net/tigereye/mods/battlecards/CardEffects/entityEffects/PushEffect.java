package net.tigereye.mods.battlecards.CardEffects.entityEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class PushEffect implements CardEffect, CardTooltipNester {

    public CardScalar pitch = new ConstantScalarEffect(0);
    public CardScalar yaw = new ConstantScalarEffect(0);
    public CardScalar magnitude = new ConstantScalarEffect(0.3f);
    public boolean pushRelativeToUserElseTarget = true;
    public boolean absolutePitch = false;
    public boolean applyKnockbackRes = true;
    public boolean overrideVelocity = false;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(pContext,context.target, context);
        }
        else {
            apply(pContext, pContext.user, context);
        }
    }

    private void apply(PersistentCardEffectContext pContext, Entity target, CardEffectContext context) {
        Entity relativeEntity = pushRelativeToUserElseTarget ? pContext.user : target;
        float pushPitch = pitch.getValue(pContext,context) - (absolutePitch ? 0 : relativeEntity.getPitch());
        float pushYaw =  yaw.getValue(pContext,context) - relativeEntity.getYaw();
        Vec3d pushVector = new Vec3d(0,0,magnitude.getValue(pContext,context))
                .rotateX((float) (pushPitch*Math.PI/180))
                .rotateY((float) (pushYaw*Math.PI/180));
        double KBRes = 0;
        if(applyKnockbackRes && target instanceof LivingEntity lEntity){
            KBRes = lEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        }
        if(overrideVelocity){
            target.setVelocity(pushVector.multiply(Math.max(0, 1 - KBRes)));
        }
        else {
            target.addVelocity(pushVector.multiply(Math.max(0, 1 - KBRes)));
        }
        target.limitFallDistance();
        target.velocityModified = true;
        target.velocityDirty = true;
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.push",magnitude.appendInlineTooltip(world, tooltip, tooltipContext))));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public PushEffect readFromJson(Identifier id, JsonElement entry) {
            PushEffect output = new PushEffect();

            output.pitch = CardSerializer.readOrDefaultScalar(id, "pitch",entry,0);
            output.yaw = CardSerializer.readOrDefaultScalar(id, "yaw",entry,0);
            output.magnitude = CardSerializer.readOrDefaultScalar(id, "magnitude",entry,0.3f);
            output.pushRelativeToUserElseTarget = CardSerializer.readOrDefaultBoolean(id, "angleRelativeToUser",entry,true);
            output.applyKnockbackRes = CardSerializer.readOrDefaultBoolean(id, "applyKnockbackRes",entry,true);
            output.overrideVelocity = CardSerializer.readOrDefaultBoolean(id, "overrideVelocity",entry,false);
            output.absolutePitch = CardSerializer.readOrDefaultBoolean(id, "absolutePitch",entry,false);

            return output;
        }
    }
}
