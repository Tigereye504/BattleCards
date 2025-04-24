package net.tigereye.mods.battlecards.CardEffects.delivery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.AbsoluteScalerEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

import java.util.ArrayList;
import java.util.List;

public class ThrowCardEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    List<CardEffect> onCollisionEffects = new ArrayList<>();
    List<CardEffect> onTickEffects = new ArrayList<>();
    public Vec3d originOffset;
    public boolean originRelativeToUserElseTarget;
    public CardScalar pitch;
    public CardScalar yaw;
    public boolean angleRelativeToEntityElseAbsolute;
    public CardScalar speed;
    CardScalar copies;

    public ThrowCardEffect (){
        originOffset = Vec3d.ZERO;
        originRelativeToUserElseTarget = true;
        pitch = new AbsoluteScalerEffect(0);
        yaw = new AbsoluteScalerEffect(0);
        angleRelativeToEntityElseAbsolute = true;
        speed = new AbsoluteScalerEffect(1.5f);
        copies = new AbsoluteScalerEffect(1);
    }

    public void addEffectOnEntityHit(CardEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    public void addEffectOnCollision(CardEffect effect){
        onCollisionEffects.add(effect);
    }
    public void addEffectsOnCollision(List<CardEffect> effects){
        onCollisionEffects.addAll(effects);
    }

    public void addEffectOnTick(CardEffect effect){
        onTickEffects.add(effect);
    }
    public void addEffectsOnTick(List<CardEffect> effects){
        onTickEffects.addAll(effects);
    }

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if (context.target != null) {
            apply(pContext, context, context.target);
        } else {
            apply(pContext, context, pContext.user);
        }
    }

    private void apply(PersistantCardEffectContext pContext, CardEffectContext context, Entity target) {
        World world = pContext.user.getWorld();
        if (!world.isClient()) {
            float copiesValue = copies.getValue(pContext,context);
            for (int i = 1; i <= copiesValue; i++) {
                CardProjectileEntity cardProjectileEntity = createProjectile(pContext,context,target);
                if(cardProjectileEntity != null) {
                    world.spawnEntity(cardProjectileEntity);
                }
            }
        }
    }

    public CardProjectileEntity createProjectile(PersistantCardEffectContext pContext, CardEffectContext context) {
        return createProjectile(pContext,context,pContext.user);
    }
    public CardProjectileEntity createProjectile(PersistantCardEffectContext pContext, CardEffectContext context, Entity target) {
        if(pContext.user == null ||((!originRelativeToUserElseTarget)&&(target == null))){
            return null;
        }
        Entity originEntity = originRelativeToUserElseTarget ? pContext.user : target;
        World world = pContext.user.getWorld();
        Vec3d rotatedOrigin = originOffset.rotateX(originEntity.getPitch()).rotateY(originEntity.getYaw());

        CardProjectileEntity cardProjectileEntity = new CardProjectileEntity(pContext, world,
                originEntity.getX() + rotatedOrigin.getX(),
                originEntity.getEyeY() - 0.1F + rotatedOrigin.getY(),
                originEntity.getZ() + rotatedOrigin.getZ());
        cardProjectileEntity.setVelocity(originEntity,
                angleRelativeToEntityElseAbsolute ? originEntity.getPitch() + this.pitch.getValue(pContext,context) : this.pitch.getValue(pContext,context),
                angleRelativeToEntityElseAbsolute ? originEntity.getYaw() + this.yaw.getValue(pContext,context) : this.yaw.getValue(pContext,context), 0.0F, speed.getValue(pContext,context), 0F);
        cardProjectileEntity.addEffectsOnEntityHit(onEntityHitEffects);
        cardProjectileEntity.addEffectsOnCollision(onCollisionEffects);
        cardProjectileEntity.addEffectsOnTick(onTickEffects);
        return cardProjectileEntity;
    }

    @Override
    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.throw_card")));
        if(!onEntityHitEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_hit")));
            for(CardEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(!onCollisionEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_collision")));
            for(CardEffect effect : onCollisionEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(!onTickEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_tick")));
            for(CardEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }

    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ThrowCardEffect readFromJson(Identifier id, JsonElement entry) {
            ThrowCardEffect output = new ThrowCardEffect();
            JsonObject obj = entry.getAsJsonObject();
            float x = CardSerializer.readOrDefaultFloat(id, "originOffsetX",entry,0);
            float y = CardSerializer.readOrDefaultFloat(id, "originOffsetY",entry,0);
            float z = CardSerializer.readOrDefaultFloat(id, "originOffsetZ",entry,0);
            output.originOffset = new Vec3d(x,y,z);

            output.pitch = CardSerializer.readOrDefaultScalar(id, "pitch",entry,0);
            output.yaw = CardSerializer.readOrDefaultScalar(id, "yaw",entry,0);
            output.speed = CardSerializer.readOrDefaultScalar(id, "speed",entry,1.5f);
            output.copies = CardSerializer.readOrDefaultScalar(id, "copies",entry,1);

            output.originRelativeToUserElseTarget = CardSerializer.readOrDefaultBoolean(id, "originRelativeToUser",entry,true);
            output.angleRelativeToEntityElseAbsolute = CardSerializer.readOrDefaultBoolean(id, "angleRelativeToEntity",entry,true);

            output.addEffectsOnEntityHit(CardSerializer.readCardEffects(id, "onHit",entry));
            output.addEffectsOnCollision(CardSerializer.readCardEffects(id, "onCollision",entry));
            output.addEffectsOnTick(CardSerializer.readCardEffects(id, "onTick",entry));

            return output;
        }
    }
}
