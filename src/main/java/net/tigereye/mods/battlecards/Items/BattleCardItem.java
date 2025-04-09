package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

public interface BattleCardItem {

    public static final String MANA_NBT = "battlecards_mana";
    boolean performBasicEffect(ItemStack stack, LivingEntity user, World world);
    boolean performChargeEffect(ItemStack stack, LivingEntity user, World world);

    default void gainMana(Entity user, ItemStack item, int amount){
        if(user instanceof LivingEntity livingEntity){
            int curMana = livingEntity.hasStatusEffect(BCStatusEffects.UNBOUND_MANA) ?
                    livingEntity.getStatusEffect(BCStatusEffects.UNBOUND_MANA).getAmplifier()+1 : 0;
            livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
            livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.UNBOUND_MANA,600,curMana+amount-1,false,false,true));
        }
    }

    default boolean payManaCost(Entity user, ItemStack item, int cost){
        if(user instanceof LivingEntity livingEntity){
            int curMana = livingEntity.hasStatusEffect(BCStatusEffects.UNBOUND_MANA) ?
                    livingEntity.getStatusEffect(BCStatusEffects.UNBOUND_MANA).getAmplifier()+1 : 0;
            if(curMana == cost){
                livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
                return true;
            }
            else if (curMana > cost){
                livingEntity.removeStatusEffect(BCStatusEffects.UNBOUND_MANA);
                livingEntity.addStatusEffect(new StatusEffectInstance(BCStatusEffects.UNBOUND_MANA,600,curMana-cost-1,false,false,true));
                return true;
            }
            return false;
        }
        return true;
    }
}
