package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.tigereye.mods.battlecards.Events.*;
import net.tigereye.mods.battlecards.Events.EventListeners.*;

public class BCListeners {
    public static void register(){
        ServerLivingEntityEvents.ALLOW_DEATH.register(OnDeathListener::checkStatusEffectsAllowDeath);
        LootTableEvents.MODIFY.register(LootTableEventsListener::injectBoosterPacks);

        PreparePersistentContextCallback.EVENT.register(PreparePersistentContextListener::applySleeve);
        PreparePersistentContextCallback.EVENT.register(PreparePersistentContextListener::applyStatusEffects);

        DamageCardEffectCallback.EVENT.register(DamageCardEffectListener::applyPersistentContext);
        StatusEffectCardEffectCallback.EVENT.register(StatusEffectCardEffectListener::applyPersistentContext);
        ManaGainCardEffectCallback.EVENT.register(ManaGainCardEffectListener::applyPersistentContext);

        CardManaCostCallback.EVENT.register(CardManaCostListener::applyCardOwnership);
        CardManaCostCallback.EVENT.register(CardManaCostListener::applySleeve);
    }
}
