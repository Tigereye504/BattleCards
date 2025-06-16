package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.tigereye.mods.battlecards.Events.EventListeners.LootTableEventsListener;
import net.tigereye.mods.battlecards.Events.EventListeners.ModifyDamageCardEffectListener;
import net.tigereye.mods.battlecards.Events.EventListeners.OnDeathListener;
import net.tigereye.mods.battlecards.Events.EventListeners.PreparePersistentContextListener;
import net.tigereye.mods.battlecards.Events.ModifyDamageCardEffectCallback;
import net.tigereye.mods.battlecards.Events.PreparePersistentContextCallback;

public class BCListeners {
    public static void register(){
        ServerLivingEntityEvents.ALLOW_DEATH.register(OnDeathListener::checkStatusEffectsAllowDeath);
        LootTableEvents.MODIFY.register(LootTableEventsListener::injectBoosterPacks);
        ModifyDamageCardEffectCallback.EVENT.register(ModifyDamageCardEffectListener::applySleeve);
        ModifyDamageCardEffectCallback.EVENT.register(ModifyDamageCardEffectListener::applyPersistentContext);
        PreparePersistentContextCallback.EVENT.register(PreparePersistentContextListener::applyStatusEffects);
    }
}
