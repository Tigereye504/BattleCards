package net.tigereye.mods.battlecards.Projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

public class CardProjectileEntity extends ThrownItemEntity {

    public CardProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public CardProjectileEntity(World world, LivingEntity owner) {
        super(BCEntities.CardProjectileEntityType, owner, world);
    }

    public CardProjectileEntity(World world, double x, double y, double z) {
        super(BCEntities.CardProjectileEntityType, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return BCItems.BASIC_CARD;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
        entity.damage(entity.getDamageSources().thrown(this, this.getOwner()), 6); // deals damage
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }
}
