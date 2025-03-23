package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

public class BasicTestCard extends BasicCardItem {
    public BasicTestCard(Settings settings) {
        super(settings);
    }

    public int advancedEffectCost(ItemStack stack, LivingEntity user, World world){
        return 0;
    }

    /*
    Create Thrown Card Projectile:
        On Hit:
            Deal 8 Damage
    */
    @Override
    public boolean performBasicEffect(ItemStack stack, LivingEntity user, World world) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F); // plays a globalSoundEvent
        if (!world.isClient) {
            CardProjectileEntity cardProjectileEntity = new CardProjectileEntity(world, user);
            cardProjectileEntity.setItem(stack);
            cardProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            world.spawnEntity(cardProjectileEntity); // spawns entity
        }
        return true;
    }

    /*
    Create 3 Thrown Card Projectiles:
        On Hit:
            Deal 24 Damage
    */
    @Override
    public boolean performAdvancedEffect(ItemStack stack, LivingEntity user, World world) {

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1F); // plays a globalSoundEvent
        if (!world.isClient) {
            CardProjectileEntity cardProjectileEntity = new CardProjectileEntity(world, user);
            cardProjectileEntity.setItem(stack);
            cardProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 0F);
            world.spawnEntity(cardProjectileEntity); // spawns entity
            CardProjectileEntity cardProjectileEntity2 = new CardProjectileEntity(world, user);
            cardProjectileEntity2.setItem(stack);
            cardProjectileEntity2.setVelocity(user, user.getPitch(), user.getYaw() + 0.835f, 0.0F, 1.5F, 0F);
            world.spawnEntity(cardProjectileEntity2); // spawns entity
            CardProjectileEntity cardProjectileEntity3 = new CardProjectileEntity(world, user);
            cardProjectileEntity3.setItem(stack);
            cardProjectileEntity3.setVelocity(user, user.getPitch(), user.getYaw() - 0.835f, 0.0F, 1.5F, 0F);
            world.spawnEntity(cardProjectileEntity3); // spawns entity

        }
        return true;
    }
}
