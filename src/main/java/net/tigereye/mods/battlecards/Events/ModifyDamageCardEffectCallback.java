package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;

public interface ModifyDamageCardEffectCallback {
    Event<ModifyDamageCardEffectCallback> EVENT = EventFactory.createArrayBacked(ModifyDamageCardEffectCallback.class,
            (listeners) -> (Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context, float amount) -> {
                for (ModifyDamageCardEffectCallback listener : listeners) {
                    amount = listener.modifyDamage(user,target,item,battleCard,context,amount);
                }
                return amount;
            });

    float modifyDamage(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context, float amount);
}
