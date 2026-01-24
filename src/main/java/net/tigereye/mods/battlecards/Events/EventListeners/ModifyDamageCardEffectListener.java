package net.tigereye.mods.battlecards.Events.EventListeners;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.Events.ModifyDamageCardEffectCallback;
import net.tigereye.mods.battlecards.Items.interfaces.CardSleeve;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;

public class ModifyDamageCardEffectListener {

    public static float applyPersistentContext(PersistantCardEffectContext pContext, Entity entity, CardEffectContext cardEffectContext, float amount) {
        for (ModifyDamageCardEffectCallback damageCallback : pContext.modifyDamageListeners){
            amount = damageCallback.modifyDamage(pContext, entity, cardEffectContext, amount);
        }
        return amount;
    }
}
