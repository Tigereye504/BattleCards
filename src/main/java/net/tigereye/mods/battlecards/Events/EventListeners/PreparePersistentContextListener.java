package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardSleeve;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;

import java.util.ArrayList;
import java.util.List;

public class PreparePersistentContextListener {
    public static void applyStatusEffects(PersistentCardEffectContext pContext, Entity user, boolean quickElseCharge) {
        if(user instanceof LivingEntity lEntity){
            List<StatusEffect> effectsToRemove = new ArrayList<>();
            for(StatusEffectInstance instance : lEntity.getStatusEffects()){
                if(instance.getEffectType() instanceof BCStatusEffect bcEffect){
                    bcEffect.preparePersistentContext(pContext,user,instance,quickElseCharge,effectsToRemove);
                }
            }
            for(StatusEffect effect : effectsToRemove){
                lEntity.removeStatusEffect(effect);
            }
        }
    }

    public static void applySleeve(PersistentCardEffectContext pContext, Entity user, boolean quickElseCharge) {
        if(pContext.cardItem.getItem() instanceof BattleCardItem bci){
            ItemStack sleeveStack = BattleCardItem.getSleeve(pContext.cardItem);
            if(sleeveStack.getItem() instanceof CardSleeve sleeve){
                sleeve.preparePersistentContext(pContext,user,sleeveStack,quickElseCharge);
            }
        }
    }
}
