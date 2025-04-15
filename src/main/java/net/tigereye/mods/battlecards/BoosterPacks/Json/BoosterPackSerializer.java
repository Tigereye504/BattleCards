package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;

import java.util.ArrayList;
import java.util.List;

public class BoosterPackSerializer {
    //remember: the first identifier is the entity, the second is the chest cavity type
    public Pair<BoosterPackData, List<Identifier>> read(Identifier id, BoosterPackJsonFormat bpdJson) {

        if (bpdJson.id == null) {
            throw new JsonSyntaxException("Booster Pack " + id + " lacks an id");
        }
        if (bpdJson.sourceLootTables == null) {
            throw new JsonSyntaxException("Booster Pack " + id + " has no sourceLootTable entry");
        }
        if (bpdJson.dropRate == 0 && bpdJson.dropRateLootingFactor == 0 && bpdJson.dropRateLuckFactor == 0) {
            Battlecards.LOGGER.warn("Booster Pack " + id + " have no way to drop");
        }

        List<Identifier> mobs = new ArrayList<>();
        Identifier boosterPackID = new Identifier(bpdJson.id);
        BoosterPackData boosterPackData = new BoosterPackData();
        boosterPackData.id = boosterPackID;
        boosterPackData.dropRate = bpdJson.dropRate;
        boosterPackData.dropRateLootingFactor = bpdJson.dropRateLootingFactor;
        boosterPackData.dropRateLuckFactor = bpdJson.dropRateLuckFactor;
        int i = 0;
        for (JsonElement entry :
                bpdJson.sourceLootTables) {
            ++i;
            try {
                mobs.add(new Identifier(entry.getAsString()));
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing entry no. " + i + " in " + id.toString() + "'s entity list");
            }
        }
        return new Pair<>(boosterPackData,mobs);
    }
}