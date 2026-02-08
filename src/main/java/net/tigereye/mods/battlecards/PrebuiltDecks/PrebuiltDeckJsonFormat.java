package net.tigereye.mods.battlecards.PrebuiltDecks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PrebuiltDeckJsonFormat {
    String id;
    String name;
    String color;
    JsonObject scrapValue;
    JsonArray cards;
    JsonArray sourceLootTables;
}
