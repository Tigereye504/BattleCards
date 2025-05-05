package net.tigereye.mods.battlecards.CardEffects.delivery;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

import java.util.ArrayList;
import java.util.List;

public class ThrowCardsEffect implements CardEffect, CardTooltipNester {

    List<ThrowCardEffect> throwCardEffects = new ArrayList<>();
    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    List<CardEffect> onCollisionEffects = new ArrayList<>();
    List<CardEffect> onTickEffects = new ArrayList<>();

    public ThrowCardsEffect(){
    }

    public void addThrownCard(ThrowCardEffect effect){
        throwCardEffects.add(effect);
    }
    public void addThrownCards(List<ThrowCardEffect> effects){
        throwCardEffects.addAll(effects);
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
            apply(pContext,context,context.target);
        } else {
            apply(pContext,context,pContext.user);
        }
    }

    private void apply(PersistantCardEffectContext pContext, CardEffectContext context, Entity target) {
        World world = pContext.user.getWorld();
        if(!world.isClient()) {
            for (ThrowCardEffect effect : throwCardEffects) {
                float copyCount = effect.copies.getValue(pContext,context);
                for (int i = 1; i <= copyCount; i++) {
                    CardProjectileEntity cardProjectileEntity = effect.createProjectile(pContext, context);
                    if (cardProjectileEntity != null) {
                        cardProjectileEntity.addEffectsOnEntityHit(onEntityHitEffects);
                        cardProjectileEntity.addEffectsOnCollision(onCollisionEffects);
                        cardProjectileEntity.addEffectsOnTick(onTickEffects);
                        world.spawnEntity(cardProjectileEntity);
                    }
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
            for(CardEffect effect : onTickEffects){
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
                List<CardEffect> projectileEffectsRaw = CardSerializer.readCardEffects(id, "projectiles", entry);
                for(CardEffect effect : projectileEffectsRaw){
                    if(effect instanceof ThrowCardEffect throwEffect){
                        output.addThrownCard(throwEffect);
                    }
                    else{
                        Battlecards.LOGGER.error("An projectile CardEffect in {} is not a thrown card!",id);
                    }
                }

                output.addEffectsOnEntityHit(CardSerializer.readCardEffects(id, "onHit",entry));
                output.addEffectsOnCollision(CardSerializer.readCardEffects(id, "onCollision",entry));
                output.addEffectsOnTick(CardSerializer.readCardEffects(id, "onTick",entry));
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing throw card effect!");
                return new ThrowCardsEffect();
            }
        }
    }

}
