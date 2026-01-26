package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Events.ManaGainCardEffectCallback;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardSleeve;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;

import java.util.ArrayList;
import java.util.List;

public class CardManaCostListener {

    //TODO: I'm sure mana modifying status effects will exist eventually. Then implement this
    /*
    public static void applyStatusEffects(PersistentCardEffectContext pContext, Entity user) {
        if(user instanceof LivingEntity lEntity){
            List<StatusEffect> effectsToRemove = new ArrayList<>();
            for(StatusEffectInstance instance : lEntity.getStatusEffects()){
                if(instance.getEffectType() instanceof BCStatusEffect bcEffect){
                    bcEffect.preparePersistentContext(pContext,user,instance,effectsToRemove);
                }
            }
            for(StatusEffect effect : effectsToRemove){
                lEntity.removeStatusEffect(effect);
            }
        }
    }
    */

    public static int applySleeve(Entity user, ItemStack stack, BattleCard card, int cost, boolean forDisplay) {
        ItemStack sleeveStack = BattleCardItem.getSleeve(stack);
        if(sleeveStack.getItem() instanceof CardSleeve sleeve){
            return sleeve.modifyManaCost(user,stack,cost,forDisplay);
        }
        return cost;
    }

    public static int applyCardOwnership(Entity user, ItemStack stack, BattleCard card, int cost, boolean forDisplay) {
        if(stack.hasNbt()) {
            return stack.getNbt().containsUuid(CardOwningItem.CARD_OWNER_UUID_NBTKEY) ? cost : cost * 2;
        }
        return cost;
    }
}
