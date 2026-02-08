package net.tigereye.mods.battlecards;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Config.BCConfig;
import net.tigereye.mods.battlecards.PrebuiltDecks.PrebuiltDeckManager;
import net.tigereye.mods.battlecards.registration.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Battlecards implements ModInitializer {

    public static final String MODID = "battlecards";
    public static final Logger LOGGER = LogManager.getLogger();
    public static BCConfig CONFIG;

    @Override
    public void onInitialize() {
        AutoConfig.register(BCConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BCConfig.class).getConfig();

        BCEntities.register();
        BCEffectSerializers.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CardManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(BoosterPackManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(PrebuiltDeckManager.INSTANCE);
        BCItems.register();
        BCListeners.register();
        BCStatusEffects.register();
    }
}


