package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface BattleCardItem {

    public static final String MANA_NBT = "battlecards_mana";
    boolean performBasicEffect(ItemStack stack, LivingEntity user, World world);
    boolean performAdvancedEffect(ItemStack stack, LivingEntity user, World world);

    default void gainMana(Entity user, ItemStack item, int amount){
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putInt(MANA_NBT,amount+nbt.getInt(MANA_NBT));
    }

    default boolean payManaCost(Entity user, ItemStack item, int cost){
        if(user instanceof PlayerEntity playerEntity){
            playerEntity.getItemCooldownManager().set(item.getItem(),cost*20);
        }
        return true;
    }
}
