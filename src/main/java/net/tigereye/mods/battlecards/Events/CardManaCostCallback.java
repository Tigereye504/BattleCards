package net.tigereye.mods.battlecards.Events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;

public interface CardManaCostCallback {
    Event<CardManaCostCallback> EVENT = EventFactory.createArrayBacked(CardManaCostCallback.class,
            (listeners) -> (user,stack,card,cost,forDisplay) -> {
                for (CardManaCostCallback listener : listeners) {
                    cost = listener.modifyManaCost(user,stack,card,cost,forDisplay);
                }
                return cost;
            });

    int modifyManaCost(Entity user, ItemStack stack, BattleCard card, int cost, boolean forDisplay);
}
