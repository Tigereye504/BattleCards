package net.tigereye.mods.battlecards.Items.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Items.sleeves.CardSleeve;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

public interface BattleCardItem {

    static final String SLEEVE_NBT = "battlecard_sleeve";

    boolean performQuickEffect(ItemStack stack, LivingEntity user, World world);
    boolean performChargeEffect(ItemStack stack, LivingEntity user, World world);

    default ItemStack getSleeve(ItemStack item){
        NbtCompound sleeveNbt = item.getSubNbt(SLEEVE_NBT);
        if(sleeveNbt == null) {
            return ItemStack.EMPTY;
        }
        ItemStack sleeveStack = ItemStack.fromNbt(item.getSubNbt(SLEEVE_NBT));
        if(sleeveStack.getItem() instanceof CardSleeve csi){
            return sleeveStack;
        }
        return ItemStack.EMPTY;
    }

    default void setSleeve(ItemStack item, ItemStack sleeve){
        item.setSubNbt(SLEEVE_NBT, sleeve.writeNbt(new NbtCompound()));
    }

    default void gainMana(Entity user, ItemStack item, int amount){
        if(user instanceof LivingEntity livingEntity){
            int curMana = getCurrentMana(user,item);
            livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
            livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.UNBOUND_MANA,600,curMana+amount-1,false,false,false));
        }
    }

    default int getCurrentMana(Entity user, ItemStack item){
        if(user instanceof LivingEntity livingEntity) {
            return livingEntity.hasStatusEffect(BCStatusEffects.UNBOUND_MANA) ?
                    livingEntity.getStatusEffect(BCStatusEffects.UNBOUND_MANA).getAmplifier() + 1 : 0;
        }
        return 0;
    }

    int getChargeEffectCost(Entity user, ItemStack item);

    default boolean payManaCost(Entity user, ItemStack item, int cost){
        if(user instanceof LivingEntity livingEntity){
            int curMana = getCurrentMana(user,item);
            if(curMana == cost){
                livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
                return true;
            }
            else if (curMana > cost){
                livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
                livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.UNBOUND_MANA,600,curMana-cost-1,false,false,false));
                return true;
            }
            return false;
        }
        return true;
    }
}
