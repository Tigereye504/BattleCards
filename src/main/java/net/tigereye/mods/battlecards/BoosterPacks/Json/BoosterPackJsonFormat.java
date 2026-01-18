package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.JsonArray;

public class BoosterPackJsonFormat {
    JsonArray sourceLootTables;
    String id;
    float dropRate;
    float dropRateLootingFactor;
    int commonCount;
    JsonArray commonCards;
    int rareCount;
    JsonArray rareCards;
}
