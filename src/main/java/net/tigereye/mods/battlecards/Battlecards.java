package net.tigereye.mods.battlecards;

import net.fabricmc.api.ModInitializer;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

public class Battlecards implements ModInitializer {

    public static final String MODID = "battlecards";
    @Override
    public void onInitialize() {
        BCEntities.register();
        BCItems.register();
    }
}
