package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.OnCollisionCardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

import java.util.ArrayList;
import java.util.List;

public class ThrowCardEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    List<CardTargetEntityEffect> onEntityHitEffects = new ArrayList<>();
    List<OnCollisionCardEffect> onCollisionEffects = new ArrayList<>();
    List<CardTargetEntityEffect> onTickEffects = new ArrayList<>();
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

    public void addEffectOnEntityHit(CardTargetEntityEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardTargetEntityEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    public void addEffectOnCollision(OnCollisionCardEffect effect){
        onCollisionEffects.add(effect);
    }
    public void addEffectsOnCollision(List<OnCollisionCardEffect> effects){
        onCollisionEffects.addAll(effects);
    }

    public void addEffectOnTick(CardTargetEntityEffect effect){
        onTickEffects.add(effect);
    }
    public void addEffectsOnTick(List<CardTargetEntityEffect> effects){
        onTickEffects.addAll(effects);
    }

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user, item, battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
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
            for(CardTargetEntityEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(!onCollisionEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_collision")));
            for(OnCollisionCardEffect effect : onCollisionEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(!onTickEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_tick")));
            for(CardTargetEntityEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }

    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ThrowCardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                ThrowCardEffect output = new ThrowCardEffect();
                JsonObject obj = entry.getAsJsonObject();
                float x = 0;
                float y = 0;
                float z = 0;
                if (obj.has("originOffsetX")){
                    x = obj.get("originOffsetX").getAsFloat();
                }
                if (obj.has("originOffsetY")){
                    y = obj.get("originOffsetY").getAsFloat();
                }
                if (obj.has("originOffsetZ")){
                    z = obj.get("originOffsetZ").getAsFloat();
                }
                output.originOffset = new Vec3d(x,y,z);
                if (obj.has("originRelativeToUser")){
                    output.originRelativeToUserElseTarget = obj.get("originRelativeToUser").getAsBoolean();
                }
                if (obj.has("pitch")){
                    output.pitch = obj.get("pitch").getAsFloat();
                }
                if (obj.has("yaw")){
                    output.yaw = obj.get("yaw").getAsFloat();
                }
                if (obj.has("angleRelativeToEntity")){
                    output.angleRelativeToEntityElseAbsolute = obj.get("angleRelativeToEntity").getAsBoolean();
                }
                if (obj.has("speed")){
                    output.speed = obj.get("speed").getAsFloat();
                }
                if (obj.has("onHit")) {
                    JsonArray onHitJson = obj.get("onHit").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        if(effect instanceof CardTargetEntityEffect cteEffect){
                            output.addEffectOnEntityHit(cteEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An onHit CardEffect in {} cannot target entity!",id);
                        }
                    }
                }
                if (obj.has("onCollision")) {
                    JsonArray onHitJson = obj.get("onCollision").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        if(effect instanceof OnCollisionCardEffect cocEffect){
                            output.addEffectOnCollision(cocEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An onCollision CardEffect in {} cannot trigger on collision!",id);
                        }
                    }
                }
                if (obj.has("onTick")) {
                    JsonArray onHitJson = obj.get("onTick").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        if(effect instanceof CardTargetEntityEffect cteEffect){
                            output.addEffectOnTick(cteEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An onTick CardEffect in {} cannot target entity!",id);
                        }
                    }
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing throw card effect!");
                return new ThrowCardEffect();
            }
        }
    }
}
