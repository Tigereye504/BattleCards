package net.tigereye.mods.battlecards.BoosterPacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.tigereye.mods.battlecards.Battlecards;

import java.util.HashMap;
import java.util.Map;

public class DropRateData {
    public String id;
    public float rate;
    public float lootingRate;
    public int rolls;

    public static DropRateData readDataFromJson(JsonElement entry, String id, String fileId){
        JsonObject jObject = entry.getAsJsonObject();
        DropRateData dropRate = new DropRateData();
        dropRate.id = id;
        dropRate.rate = jObject.has("rate") ? jObject.get("rate").getAsFloat() : 0;
        dropRate.lootingRate = jObject.has("lootingRate") ? jObject.get("lootingRate").getAsFloat() : 0;
        dropRate.rolls = jObject.has("rolls") ? jObject.get("rolls").getAsInt() : 1;
        if (dropRate.rate == 0 && dropRate.lootingRate == 0) {
            Battlecards.LOGGER.warn("Booster Pack {} has no chance to drop from {}", fileId, jObject.get("id").getAsString());
        }
        return dropRate;
    }

    public static Map<String, DropRateData> readSourceLootTablesFromJson(JsonArray jsonLootTable, String id, String fileId){
        Map<String, DropRateData> sourceLootTables = new HashMap<>();
        if(jsonLootTable != null) {
            int i = 0;
            for (JsonElement entry :
                    jsonLootTable) {
                try {
                    JsonObject jObject = entry.getAsJsonObject();
                    sourceLootTables.put(jObject.get("id").getAsString(), readDataFromJson(entry, id, fileId));
                    ++i;
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing mob identifier {} in {}'s entity list", i, id);
                }
            }
        }
        return sourceLootTables;
    }
}
