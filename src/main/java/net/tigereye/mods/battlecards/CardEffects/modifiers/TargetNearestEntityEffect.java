package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalerEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class TargetNearestEntityEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    boolean ignoreUser = true;
    boolean ignorePierced = true;
    boolean ignoreTrackedEntity = true;
    boolean ignoreCurrentTarget = true;
    boolean mustBeLiving = true;
    CardScalar range = new ConstantScalerEffect(16);

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        CardEffectContext newContext = context.clone();
        Vec3d origin;
        if(context.target != null){
            origin = context.target.getPos();
        } else if (context.hitResult != null) {
            origin = context.hitResult.getPos();
        }
        else {
            origin = pContext.user.getPos();
        }
        newContext.target = getTarget(pContext,context,origin);
        if(newContext.target != null) {
            for (CardEffect effect : effects) {
                effect.apply(pContext, newContext);
            }
        }
    }

    private Entity getTarget(PersistantCardEffectContext pContext, CardEffectContext context, Vec3d origin) {
        double range = this.range.getValue(pContext,context);
        List<Entity> ignoreList = new ArrayList<>();
        if(ignoreUser && pContext.user != null) ignoreList.add(pContext.user);
        if(ignoreCurrentTarget && context.target != null) ignoreList.add(context.target);
        if(ignoreTrackedEntity && context.trackedEntity != null) ignoreList.add(context.trackedEntity);

        Vec3d boxMin = origin.add(-range,-range,-range);
        Vec3d boxMax = origin.add(range,range,range);
        Box box = new Box(boxMin,boxMax);
        List<Entity> possibleTargets = pContext.user.getEntityWorld().getOtherEntities(pContext.user,box);
        Entity closestTarget = null;
        double SqBestDistance = range*range;
        for (Entity possibleTarget : possibleTargets){
            //check various ignore conditions
            if(mustBeLiving && !(possibleTarget instanceof LivingEntity)){continue;}
            if(ignoreList.contains(possibleTarget)){continue;}
            if(ignorePierced && (context.target instanceof PersistentProjectileEntity ppe) && !(ppe.canHit(possibleTarget))){continue;}

            double SqDistance = possibleTarget.getPos().squaredDistanceTo(origin);
            if(SqDistance < SqBestDistance){
                closestTarget = possibleTarget;
                SqBestDistance = SqDistance;
            }
        }
        return closestTarget;
    }

    public void addCardEffect(CardEffect effect){
        effects.add(effect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.target_projectile")));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public TargetNearestEntityEffect readFromJson(Identifier id, JsonElement entry) {
            TargetNearestEntityEffect output = new TargetNearestEntityEffect();
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("Missing effects for TargetProjectile modifier in {}.",id);
            }
            output.ignoreUser = CardSerializer.readOrDefaultBoolean(id, "ignoreUser",entry,true);
            output.ignorePierced = CardSerializer.readOrDefaultBoolean(id, "ignorePierced",entry,true);
            output.ignoreTrackedEntity = CardSerializer.readOrDefaultBoolean(id, "ignoreProjectile",entry,true);
            output.ignoreCurrentTarget = CardSerializer.readOrDefaultBoolean(id, "ignoreCurrentTarget",entry,true);
            output.mustBeLiving = CardSerializer.readOrDefaultBoolean(id, "mustBeLiving",entry,true);
            return output;
        }
    }
}
