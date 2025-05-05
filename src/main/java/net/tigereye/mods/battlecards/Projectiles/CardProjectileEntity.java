package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.registration.BCEntities;

import java.util.ArrayList;
import java.util.List;

public class CardProjectileEntity extends PersistentProjectileEntity implements BCProjectileEntity {

    PersistantCardEffectContext pContext;
    CardEffectContext context;
    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    List<CardEffect> onCollisionEffects = new ArrayList<>();
    List<CardEffect> onTickEffects = new ArrayList<>();
    public float gravity = 0.05f;

    //TODO: custom nbt data to save effects
    //TODO: change sound effect away from arrows
    //TODO: mixin persistent projectiles to modulate gravity

    public CardProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pContext = new PersistantCardEffectContext(null,CardManager.readNBTBattleCard(ItemStack.EMPTY),ItemStack.EMPTY);
        this.context = new CardEffectContext();
        this.init();
    }

    public CardProjectileEntity(PersistantCardEffectContext pContext, CardEffectContext context, World world, double x, double y, double z) {
        super(BCEntities.CardProjectileEntityType, x, y, z, world);
        this.pContext = pContext;
        this.context = context;
        setOwner(pContext.user);
        this.init();
    }

    private void init(){
        this.setDamage(0);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public List<CardEffect> getEffectsOnEntityHit(){return onEntityHitEffects;}
    public void addEffectOnEntityHit(CardEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    public List<CardEffect> getEffectsOnCollision(){return onCollisionEffects;}
    public void addEffectOnCollision(CardEffect effect){
        onCollisionEffects.add(effect);
    }
    public void addEffectsOnCollision(List<CardEffect> effects){
        onCollisionEffects.addAll(effects);
    }

    public List<CardEffect> getEffectsOnTick(){return onTickEffects;}
    public void addEffectOnTick(CardEffect effect){
        onTickEffects.add(effect);
    }
    public void addEffectsOnTick(List<CardEffect> effects){
        onTickEffects.addAll(effects);
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        CardEffectContext newContext = context.clone();
        newContext.target = entityHitResult.getEntity();
        newContext.trackedEntity = this;
        for(CardEffect effect : getEffectsOnEntityHit()){
            effect.apply(pContext, newContext);
        }
    }

    @Override
    public ItemStack asItemStack() {
        return CardManager.generateCardItemstack(pContext != null ? pContext.card.getID() : new Identifier(""));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if(hitResult.getType() != HitResult.Type.MISS) {
            for (CardEffect effect : getEffectsOnCollision()) {
                CardEffectContext newContext = context.clone();
                newContext.hitResult = hitResult;
                effect.apply(pContext, newContext);
            }
        }

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            //this.discard();
        }
    }

    public float getGravity(){
        return gravity;
    }

    @Override
    public void tick(){
        if(!getEffectsOnTick().isEmpty()){
            CardEffectContext context = this.context.clone();
            context.target = this;
            for(CardEffect effect : getEffectsOnTick()){
                effect.apply(pContext,context);
            }
        }
        super.tick();
    }
}
