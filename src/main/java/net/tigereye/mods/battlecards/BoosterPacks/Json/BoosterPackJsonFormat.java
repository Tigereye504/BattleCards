package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BoosterPackJsonFormat {
    JsonArray sourceLootTables;
    String id;
    JsonObject scrapValue;
    float dropRate;
    float dropRateLootingFactor;
    int commonCount;
    JsonArray commonCards;
    int rareCount;
    JsonArray rareCards;
}
