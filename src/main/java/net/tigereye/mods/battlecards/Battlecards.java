package net.tigereye.mods.battlecards;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.registration.BCEffectSerializers;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Battlecards implements ModInitializer {

    public static final String MODID = "battlecards";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        BCEntities.register();
        BCEffectSerializers.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CardManager());
        BCItems.register();
    }
}


