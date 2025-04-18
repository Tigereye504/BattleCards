package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.ArrayList;
import java.util.List;

public class CardProjectileEntity extends ThrownItemEntity {

    PersistantCardEffectContext pContext;
    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    List<CardEffect> onCollisionEffects = new ArrayList<>();
    List<CardEffect> onTickEffects = new ArrayList<>();

    //TODO: custom nbt data to save effects

    public CardProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.pContext = null;
    }

    public CardProjectileEntity(PersistantCardEffectContext pContext, World world, double x, double y, double z) {
        super(BCEntities.CardProjectileEntityType, x, y, z, world);
        setOwner(pContext.user);
        this.pContext = pContext;
    }

    @Override
    protected Item getDefaultItem() {
        return BCItems.BATTLECARD;
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
    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        CardEffectContext context = new CardEffectContext();
        context.target = entityHitResult.getEntity();
        for(CardEffect effect : onEntityHitEffects){
            effect.apply(pContext,context);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        for(CardEffect effect : onCollisionEffects){
            CardEffectContext context = new CardEffectContext();
            context.hitResult = hitResult;
            effect.apply(pContext,context);
        }

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    public void tick(){
        super.tick();
        CardEffectContext context = new CardEffectContext();
        context.target = this;
        for(CardEffect effect : onTickEffects){
            effect.apply(pContext,context);
        }
    }
}
