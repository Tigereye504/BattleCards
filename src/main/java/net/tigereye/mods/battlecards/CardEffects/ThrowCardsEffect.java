package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardOnCollisionEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Items.BattleCardItem;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

import java.util.ArrayList;
import java.util.List;

public class ThrowCardsEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    BattleCardItem card;
    List<ThrowCardEffect> throwCardEffects = new ArrayList<>();
    List<CardTargetEntityEffect> onEntityHitEffects = new ArrayList<>();
    List<CardOnCollisionEffect> onCollisionEffects = new ArrayList<>();
    List<CardTargetEntityEffect> onTickEffects = new ArrayList<>();

    public ThrowCardsEffect(){
    }

    public void addThrownCard(ThrowCardEffect effect){
        throwCardEffects.add(effect);
    }
    public void addThrownCards(List<ThrowCardEffect> effects){
        throwCardEffects.addAll(effects);
    }

    public void addEffectOnEntityHit(CardTargetEntityEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardTargetEntityEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    public void addEffectOnCollision(CardOnCollisionEffect effect){
        onCollisionEffects.add(effect);
    }
    public void addEffectsOnCollision(List<CardOnCollisionEffect> effects){
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
        apply(user,user, item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        World world = user.getWorld();
        if(!world.isClient()) {
            for (ThrowCardEffect effect : throwCardEffects) {
                CardProjectileEntity cardProjectileEntity = effect.createProjectile(user, item, battleCard);
                if(cardProjectileEntity != null) {
                    cardProjectileEntity.addEffectsOnEntityHit(onEntityHitEffects);
                    cardProjectileEntity.addEffectsOnCollision(onCollisionEffects);
                    cardProjectileEntity.addEffectsOnTick(onTickEffects);
                    world.spawnEntity(cardProjectileEntity);
                }
            }
        }
    }

    @Override
    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.throw_cards",throwCardEffects.size())));
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
            for(CardOnCollisionEffect effect : onCollisionEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
        if(!onTickEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_tick")));
            for(CardTargetEntityEffect effect : onTickEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public ThrowCardsEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                ThrowCardsEffect output = new ThrowCardsEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (obj.has("projectiles")) {
                    JsonArray onHitJson = obj.get("projectiles").getAsJsonArray();
                    List<CardEffect> projectileEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : projectileEffectsRaw){
                        if(effect instanceof ThrowCardEffect throwEffect){
                            output.addThrownCard(throwEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An projectile CardEffect in {} is not a thrown card!",id);
                        }
                    }
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
                        if(effect instanceof CardOnCollisionEffect cocEffect){
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
                return new ThrowCardsEffect();
            }
        }
    }

}
