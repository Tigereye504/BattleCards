package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Items.sleeves.CardSleeve;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;

public class ModifyDamageCardEffectListener {


    public static float applySleeve(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context, float amount) {
        if(item.getItem() instanceof BattleCardItem bci){
            ItemStack sleeveStack = bci.getSleeve(item);
            if(sleeveStack.getItem() instanceof CardSleeve sleeve){
                amount = sleeve.modifyDamage(user,target,item,battleCard,context,amount,sleeveStack);
            }
        }
        return amount;
    }
}
