package net.tigereye.mods.battlecards.Items.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

public interface BattleCardItem {

    public static final int MAX_MANA = 10;
    static final String SLEEVE_NBT = "battlecard_sleeve";

    boolean performQuickEffect(ItemStack stack, LivingEntity user, World world);
    boolean performChargeEffect(ItemStack stack, LivingEntity user, World world);

    static ItemStack getSleeve(ItemStack item){
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

    default void clearSleeve(ItemStack item){
        item.removeSubNbt(SLEEVE_NBT);
    }

    default void setSleeve(ItemStack item, ItemStack sleeve){
        item.setSubNbt(SLEEVE_NBT, sleeve.writeNbt(new NbtCompound()));
    }

    default void setSleeve(ItemStack item, String sleeve){
        Item sleeveItem = Registries.ITEM.get(Identifier.tryParse(sleeve));
        item.setSubNbt(SLEEVE_NBT, sleeveItem.getDefaultStack().writeNbt(new NbtCompound()));
        //TODO: this will need refactored to allow for data-driven sleeves.
    }

    default void gainMana(Entity user, ItemStack item, int amount){
        if(user instanceof LivingEntity livingEntity){
            int curMana = getCurrentMana(user,item);
            livingEntity.removeStatusEffect(BCStatusEffects.MANA);
            livingEntity.addStatusEffect(BCStatusEffect.buildGradualFalloffStatusEffectInstance(BCStatusEffects.MANA, 600, 200, Math.min(MAX_MANA-1,curMana + amount - 1), false, false, false));
            //livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.MANA,600,curMana+amount-1,false,false,false));
        }
    }

    default int getCurrentMana(Entity user, ItemStack item){
        if(user instanceof LivingEntity livingEntity) {
            return livingEntity.hasStatusEffect(BCStatusEffects.MANA) ?
                    livingEntity.getStatusEffect(BCStatusEffects.MANA).getAmplifier() + 1 : 0;
        }
        return 0;
    }

    int getChargeEffectCost(Entity user, ItemStack item, boolean forDisplay);

    default boolean payManaCost(Entity user, ItemStack item, int cost){
        if(user instanceof LivingEntity livingEntity){
            int curMana = getCurrentMana(user,item);
            if(curMana == cost){
                livingEntity.removeStatusEffect(BCStatusEffects.MANA);
                return true;
            }
            else if (curMana > cost){
                livingEntity.removeStatusEffect(BCStatusEffects.MANA);
                livingEntity.addStatusEffect(BCStatusEffect.buildGradualFalloffStatusEffectInstance(BCStatusEffects.MANA, 600, 200, curMana - cost - 1, false, false, false));
                //livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.MANA,600,curMana-cost-1,false,false,false));
                return true;
            }
            return false;
        }
        return true;
    }
}
