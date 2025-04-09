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
import net.tigereye.mods.battlecards.CardEffects.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
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
    public float pitch;
    public float yaw;
    public boolean angleRelativeToEntityElseAbsolute;
    public float speed;

    public ThrowCardEffect (){
        originOffset = Vec3d.ZERO;
        originRelativeToUserElseTarget = true;
        pitch = 0;
        yaw = 0;
        angleRelativeToEntityElseAbsolute = true;
        speed = 1.5f;
    }

    public ThrowCardEffect (Vec3d originOffset, boolean originRelativeToUserElseTarget, float pitch, float yaw, boolean angleRelativeToEntityElseAbsolute, float speed){
        this.originOffset = originOffset;
        this.originRelativeToUserElseTarget = originRelativeToUserElseTarget;
        this.pitch = pitch;
        this.yaw = yaw;
        this.angleRelativeToEntityElseAbsolute = angleRelativeToEntityElseAbsolute;
        this.speed = speed;
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
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        if (context.target != null) {
            apply(user, context.target, item, battleCard);
        } else {
            apply(user, user, item, battleCard);
        }
    }

    private void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        World world = user.getWorld();
        if (!world.isClient()) {
            CardProjectileEntity cardProjectileEntity = createProjectile(user,target,item,battleCard);
            if(cardProjectileEntity != null) {
                world.spawnEntity(cardProjectileEntity);
            }
        }
    }

    public CardProjectileEntity createProjectile(Entity user, ItemStack item, BattleCard battleCard) {
        return createProjectile(user,user,item,battleCard);
    }
    public CardProjectileEntity createProjectile(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(user == null ||((!originRelativeToUserElseTarget)&&(target == null))){
            return null;
        }
        Entity originEntity = originRelativeToUserElseTarget ? user : target;
        World world = user.getWorld();
        Vec3d rotatedOrigin = originOffset.rotateX(originEntity.getPitch()).rotateY(originEntity.getYaw());

        CardProjectileEntity cardProjectileEntity = new CardProjectileEntity(world, user,
                originEntity.getX() + rotatedOrigin.getX(),
                originEntity.getEyeY() - 0.1F + rotatedOrigin.getY(),
                originEntity.getZ() + rotatedOrigin.getZ(),
                item, battleCard);
        cardProjectileEntity.setVelocity(user,
                angleRelativeToEntityElseAbsolute ? originEntity.getPitch() + this.pitch : this.pitch,
                angleRelativeToEntityElseAbsolute ? originEntity.getYaw() + this.yaw : this.yaw, 0.0F, speed, 0F);
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

            output.pitch = CardSerializer.readOrDefaultFloat(id, "pitch",entry,0);
            output.yaw = CardSerializer.readOrDefaultFloat(id, "yaw",entry,0);
            output.speed = CardSerializer.readOrDefaultFloat(id, "speed",entry,0);

            output.originRelativeToUserElseTarget = CardSerializer.readOrDefaultBoolean(id, "originRelativeToUser",entry,true);
            output.angleRelativeToEntityElseAbsolute = CardSerializer.readOrDefaultBoolean(id, "angleRelativeToEntity",entry,true);

            output.addEffectsOnEntityHit(CardSerializer.readCardEffects(id, "onHit",entry));
            output.addEffectsOnCollision(CardSerializer.readCardEffects(id, "onCollision",entry));
            output.addEffectsOnTick(CardSerializer.readCardEffects(id, "onTick",entry));

            return output;
        }
    }
}
