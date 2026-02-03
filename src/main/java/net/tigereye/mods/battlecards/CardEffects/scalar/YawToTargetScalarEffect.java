package net.tigereye.mods.battlecards.CardEffects.scalar;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class YawToTargetScalarEffect implements CardEffect, CardScalar, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    boolean userElseTrackedEntity = true;
    boolean degElseRad = true;

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        newContext.scalar = getValue(pContext,context);

        for(CardEffect effect : effects){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public float getValue(PersistentCardEffectContext pContext, CardEffectContext context) {
        Entity scalarEntity = userElseTrackedEntity ? pContext.user : context.trackedEntity;
        if(context.target == null || scalarEntity == null){
            return 0;
        }
        Vec3d relativizedPos = scalarEntity.getPos().relativize(context.target.getPos());
        Vec3d removeYAxis = relativizedPos.multiply(1,0,1);
        Vec3d step1 = removeYAxis.rotateY((float) (scalarEntity.getYaw()*Math.PI/180));
        //Vec3d step2 = step1.rotateX((float) (context.target.getPitch()*Math.PI/180));
        Vec3d orientedNormalizedRPos = step1.normalize();
        double angleInRads = Math.acos(orientedNormalizedRPos.z);
        if(orientedNormalizedRPos.x < 0){
            angleInRads = -angleInRads;
        }
        if(degElseRad){
            return (float) (angleInRads*180/Math.PI);
        }
        return (float) angleInRads;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.yaw_to_target_scalar")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public YawToTargetScalarEffect readFromJson(Identifier id, JsonElement entry) {
            YawToTargetScalarEffect output = new YawToTargetScalarEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.userElseTrackedEntity = CardSerializer.readOrDefaultBoolean(id,"userElseTrackedEntity",entry,true);
            output.degElseRad = CardSerializer.readOrDefaultBoolean(id,"degElseRad",entry,true);
            return output;
        }
    }
}
