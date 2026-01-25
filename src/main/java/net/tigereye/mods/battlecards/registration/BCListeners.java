package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.tigereye.mods.battlecards.Events.EventListeners.*;
import net.tigereye.mods.battlecards.Events.ModifyDamageCardEffectCallback;
import net.tigereye.mods.battlecards.Events.ModifyStatusEffectCardEffectCallback;
import net.tigereye.mods.battlecards.Events.PreparePersistentContextCallback;

public class BCListeners {
    public static void register(){
        ServerLivingEntityEvents.ALLOW_DEATH.register(OnDeathListener::checkStatusEffectsAllowDeath);
        LootTableEvents.MODIFY.register(LootTableEventsListener::injectBoosterPacks);
        ModifyDamageCardEffectCallback.EVENT.register(ModifyDamageCardEffectListener::applyPersistentContext);
        ModifyStatusEffectCardEffectCallback.EVENT.register(ModifyStatusEffectCardEffectListener::applyPersistentContext);
        PreparePersistentContextCallback.EVENT.register(PreparePersistentContextListener::applySleeve);
        PreparePersistentContextCallback.EVENT.register(PreparePersistentContextListener::applyStatusEffects);
    }
}
