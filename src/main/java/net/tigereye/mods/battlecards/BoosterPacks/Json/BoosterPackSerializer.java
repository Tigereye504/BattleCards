package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackDropRate;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.HashMap;
import java.util.HashSet;

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
        boosterPackData.sourceLootTables = new HashMap<>();
        boosterPackData.id = boosterPackID;


        int i = 0;
        if(bpdJson.sourceLootTables != null) {
            for (JsonElement entry :
                    bpdJson.sourceLootTables) {
                ++i;
                try {
                    JsonObject jObject = entry.getAsJsonObject();
                    BoosterPackDropRate dropRate = new BoosterPackDropRate();
                    dropRate.id = boosterPackData.id;
                    dropRate.rate = jObject.get("rate").getAsFloat();
                    dropRate.lootingRate = jObject.get("lootingRate").getAsFloat();
                    if (dropRate.rate == 0 && dropRate.lootingRate == 0) {
                        Battlecards.LOGGER.warn("Booster Pack {} has no chance to drop from{}", id, jObject.get("id").getAsString());
                    }
                    boosterPackData.sourceLootTables.put(new Identifier(jObject.get("id").getAsString()), dropRate);
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

        if(bpdJson.scrapValue != null){
            try{
                Item item = Registries.ITEM.get(Identifier.tryParse(bpdJson.scrapValue.get("item").getAsString()));
                if(item == Items.AIR){
                    Battlecards.LOGGER.error("Scrap value of {} is air. This is most likely due to a misspelled identifier", id.toString());
                }
                int count = bpdJson.scrapValue.get("count").getAsInt();
                boosterPackData.scrapValue = new ItemStack(item,count);
            }
            catch (Exception e){
                Battlecards.LOGGER.error("Error parsing scrap value of {}", id.toString());
                boosterPackData.scrapValue = null;
            }
        }
        else{
            boosterPackData.scrapValue = new ItemStack(BCItems.CARDFETTI,boosterPackData.commonCount+boosterPackData.rareCount);
        }

        return boosterPackData;
    }
}