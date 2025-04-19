package net.tigereye.mods.battlecards.CardEffects.delivery;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.RetainCardEffect;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class MeleeEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    double reach = 3.5;
    double maxAngle = 30;
    boolean isSweep = true;
    boolean retainOnMiss = true;

    public void addEffectOnEntityHit(CardEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {

        if (pContext.user instanceof PlayerEntity pEntity) {
            pEntity.swingHand(pEntity.getActiveHand());
        }

        //TODO: raycast out to reach (configured, by default use the user's reach)
        Vec3d boxMin = pContext.user.getEyePos().add(-reach,-reach,-reach);
        Vec3d boxMax = pContext.user.getEyePos().add(reach,reach,reach);
        /*EntityHitResult ehr = ProjectileUtil.raycast(user,
                user.getEyePos(),
                user.getEyePos().add(user.getRotationVec(1).multiply(reach)),
                user.getBoundingBox().stretch(user.getRotationVec(1.0f).multiply(reach)).expand(reach), entity -> entity != user,reach*reach);
         */
        //instead of raycasting, perhaps iterating the box for targets in a cone in front of the user would be better.
        Box sweepBox = new Box(boxMin,boxMax);
        List<Entity> possibleTargets = pContext.user.getEntityWorld().getOtherEntities(pContext.user,sweepBox);
        List<Entity> targetsInCone = new ArrayList<>();
        for(Entity possibleTarget : possibleTargets){
            if(isEntityInCone(pContext.user.getEyePos(),possibleTarget,pContext.user.getHeadYaw(),pContext.user.getPitch(),maxAngle)){
                targetsInCone.add(possibleTarget);
            }
        }
        //TODO: Then take the nearest foe in cone that passes a 'line of sight' check. For now, keep that relatively simple;
        // a raycast towards each of the target's corners and center. On any hit, the target was targetable.

        //apply card effects to all hit entities
        if(!targetsInCone.isEmpty()){
            if(!isSweep){
                //determine the closest target. remove the rest
                targetsInCone.sort((entity1, entity2) -> {
                    double dis1 = entity1.squaredDistanceTo(pContext.user);
                    double dis2 = entity2.squaredDistanceTo(pContext.user);
                    if(dis1 > dis2){
                        return 1;
                    }
                    if(dis1 < dis2){
                        return -1;
                    }
                    return 0;
                });
                Entity closest = targetsInCone.get(0);
                targetsInCone.clear();
                targetsInCone.add(closest);
            }
            for(Entity target : targetsInCone) {
                CardEffectContext onHitContext = new CardEffectContext();
                onHitContext.target = target;
                for (CardEffect effect : onEntityHitEffects) {
                    effect.apply(pContext, onHitContext);
                }
            }
        }
        /*
        if(ehr != null){
            if(user instanceof PlayerEntity pEntity){
                pEntity.swingHand(pEntity.getActiveHand());
            }
            CardEffectContext onHitContext = new CardEffectContext();
            onHitContext.target = ehr.getEntity();
            for (CardEffect effect : onEntityHitEffects){
                effect.apply(user,item,battleCard,onHitContext);
            }
        }
        */
        else if(retainOnMiss){
            new RetainCardEffect().apply(pContext,context);
        }
    }

    private boolean isEntityInCone(Vec3d origin, Entity entity, float yaw, float pitch, double coneWidth){
        Box boundingBox = entity.getBoundingBox();
        if(isPointInCone(origin,boundingBox.getCenter(),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.maxX,boundingBox.maxY,boundingBox.maxZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.maxX,boundingBox.maxY,boundingBox.minZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.maxX,boundingBox.minY,boundingBox.maxZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.maxX,boundingBox.minY,boundingBox.minZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.minX,boundingBox.maxY,boundingBox.maxZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.minX,boundingBox.maxY,boundingBox.minZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.minX,boundingBox.minY,boundingBox.maxZ),yaw,pitch,coneWidth)){return true;}
        if(isPointInCone(origin,new Vec3d(boundingBox.minX,boundingBox.minY,boundingBox.minZ),yaw,pitch,coneWidth)){return true;}
        return false;
    }

    private boolean isPointInCone(Vec3d origin, Vec3d target, float yaw, float pitch, double coneWidth){
        Vec3d relativizedPos = origin.relativize(target);
        Vec3d step1 = relativizedPos.rotateY((float) (yaw*Math.PI/180));
        Vec3d step2 = step1.rotateX((float) (pitch*Math.PI/180));
        Vec3d orientedNormalizedRPos = step2.normalize();
        //Vec3d orientedNormalizedRPos = relativizedPos.rotateX(-user.getHeadYaw()).rotateY(-user.getPitch()).normalize();
        double angle = Math.acos(orientedNormalizedRPos.z)*180/Math.PI; //minecraft does angles in degrees, so convert from radians for consistency
        return angle <= coneWidth;
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.melee")));
        if(!onEntityHitEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_hit")));
            for(CardEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public MeleeEffect readFromJson(Identifier id, JsonElement entry) {
            MeleeEffect output = new MeleeEffect();
            output.reach = CardSerializer.readOrDefaultFloat(id,"reach",entry,3.5f);
            output.isSweep = CardSerializer.readOrDefaultBoolean(id,"isSweep",entry,true);
            output.retainOnMiss = CardSerializer.readOrDefaultBoolean(id,"retainOnMiss",entry,true);
            output.addEffectsOnEntityHit(CardSerializer.readCardEffects(id, "onHit",entry));
            if (output.onEntityHitEffects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on melee hit in {}!",id);
            }
            return output;
        }
    }
}
