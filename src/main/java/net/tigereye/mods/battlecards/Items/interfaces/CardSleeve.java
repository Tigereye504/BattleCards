package net.tigereye.mods.battlecards.Items.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;

public interface CardSleeve {

    default float modifyDamage(PersistantCardEffectContext pContext, Entity target, CardEffectContext context, float amount, ItemStack sleeve){
        return amount;
    }

    //TODO: make event and hook for afterDamage
    default void afterDamage(PersistantCardEffectContext pContext, Entity target, CardEffectContext context,  float amountApplied, float amountTaken, ItemStack sleeve){}

    /***********************
     * modifyManaCost changes how much a card's charged effect costs to use.
     * @param user
     * user is the entity using the card.
     * @param item
     * The itemStack used. Should be a stack of the card being used (a BattleCardItem), but shenanigans are possible.
     * @param cost
     * The card's mana cost.
     * @param forDisplay
     * If true, the method is being invoked 'cosmetically' to inform a tooltip or similar.
     * Implementing sleeves should not make any changes to the world.
     * Note that both the unmodified and modified costs of cards will be shown in the tooltip.
     * If false, the sleeve is being invoked 'for real' and should apply changes.
     * For example, Batet Sleeves could allow the user to substitute missing mana for blood debt.
     * When forDisplay is true, the mana cost is set to the user's current available mana. No blood debt is incurred.
     * When forDisplay is false, the mana cost is set to the user's current available mana and the rest taken in blood.
     * @return
     * Returns the mana cost after the sleeve's modification.
     */
    //TODO: make event and hook for modifyManaCost
    default int modifyManaCost(Entity user, ItemStack item, int cost, boolean forDisplay){return cost;}

    void preparePersistentContext(PersistantCardEffectContext pContext, Entity user, ItemStack sleeve, boolean quickElseCharge);

}
