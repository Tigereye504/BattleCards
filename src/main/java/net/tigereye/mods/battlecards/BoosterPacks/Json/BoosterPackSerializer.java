package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoosterPackSerializer {
    //remember: the first identifier is the entity, the second is the chest cavity type
    public BoosterPackData read(Identifier id, BoosterPackJsonFormat bpdJson) {

        if (bpdJson.id == null) {
            throw new JsonSyntaxException("Booster Pack " + id + " lacks an id");
        }
        if (bpdJson.sourceLootTables == null) {
            Battlecards.LOGGER.warn("Booster Pack " + id + " has no sourceLootTable entry");
        }
        if (bpdJson.dropRate == 0 && bpdJson.dropRateLootingFactor == 0) {
            Battlecards.LOGGER.warn("Booster Pack " + id + " has no chance to drop");
        }

        Identifier boosterPackID = new Identifier(bpdJson.id);
        BoosterPackData boosterPackData = new BoosterPackData();
        boosterPackData.mobs = new HashSet<>();
        boosterPackData.id = boosterPackID;
        boosterPackData.dropRate = bpdJson.dropRate;
        boosterPackData.dropRateLootingFactor = bpdJson.dropRateLootingFactor;
        int i = 0;
        if(bpdJson.sourceLootTables != null) {
            for (JsonElement entry :
                    bpdJson.sourceLootTables) {
                ++i;
                try {
                    boosterPackData.mobs.add(new Identifier(entry.getAsString()));
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing mob identifier {} in {}'s entity list", i, id.toString());
                }
            }
        }
        boosterPackData.commonCount = bpdJson.commonCount;
        boosterPackData.rareCount = bpdJson.rareCount;

        boosterPackData.commonCards = new HashSet<>();
        i = 0;
        if(bpdJson.commonCards != null) {
            for (JsonElement entry :
                    bpdJson.commonCards) {
                ++i;
                try {
                    boosterPackData.commonCards.add(new Identifier(entry.getAsString()));
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing common card identifier {} in {}", i, id.toString());
                }
            }
        }

        boosterPackData.rareCards = new HashSet<>();
        i = 0;
        if(bpdJson.rareCards != null) {
            for (JsonElement entry :
                    bpdJson.rareCards) {
                ++i;
                try {
                    boosterPackData.rareCards.add(new Identifier(entry.getAsString()));
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing rare card identifier {} in {}", i, id.toString());
                }
            }
        }
        return boosterPackData;
    }
}