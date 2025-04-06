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
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardOnCollisionEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Items.BattleCardItem;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.ArrayList;
import java.util.List;

public class CardProjectileEntity extends ThrownItemEntity {

    ItemStack item;
    BattleCard battleCard;
    List<CardTargetEntityEffect> onEntityHitEffects = new ArrayList<>();
    List<CardOnCollisionEffect> onCollisionEffects = new ArrayList<>();
    List<CardTargetEntityEffect> onTickEffects = new ArrayList<>();

    //TODO: custom nbt data to save effects

    public CardProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.item = null;
        this.battleCard = null;
    }

    public CardProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world, ItemStack item, BattleCard battleCard) {
        super(entityType, world);
        this.item = item;
        this.battleCard = battleCard;
    }

    public CardProjectileEntity(World world, LivingEntity owner, ItemStack item, BattleCard battleCard) {
        super(BCEntities.CardProjectileEntityType, owner, world);
        this.item = item;
        this.battleCard = battleCard;
    }

    public CardProjectileEntity(World world, Entity owner, double x, double y, double z, ItemStack item, BattleCard battleCard) {
        super(BCEntities.CardProjectileEntityType, x, y, z, world);
        setOwner(owner);
        this.item = item;
        this.battleCard = battleCard;
    }

    @Override
    protected Item getDefaultItem() {
        return BCItems.BATTLECARD;
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
    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        for(CardTargetEntityEffect effect : onEntityHitEffects){
            effect.apply(getOwner(),entity, item, battleCard);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        for(CardOnCollisionEffect effect : onCollisionEffects){
            //TODO: this is clearly incomplete!
            effect.apply(getOwner(),null, item, battleCard);
        }
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    public void tick(){
        super.tick();
        for(CardTargetEntityEffect effect : onTickEffects){
            effect.apply(getOwner(),this, item, battleCard);
        }
    }
}
