package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.GeneratedBattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardSerializer {
    private static final String DEFAULT_TEXTURE = "battlecards:item/battlecard";
    private static final Map<String, CardEffectSerializer> effectSerializers = new HashMap<>();

    public Pair<Identifier, BattleCard> read(Identifier id, CardJsonFormat cardJson) {
        GeneratedBattleCard generatedBattleCard = new GeneratedBattleCard();

        //set internal id
        Identifier cardID;
        if (cardJson.id == null) {
            Battlecards.LOGGER.warn("Card {} is missing an id! Defaulting to directory path.", id);
            cardID = id;
        }
        else {
            cardID = new Identifier(cardJson.id);
        }
        generatedBattleCard.setID(cardID);

        //set effect cost
        generatedBattleCard.setChargeEffectCost(cardJson.cost);

        //set quick effects
        if (cardJson.quickEffects == null) {
            Battlecards.LOGGER.warn("Card {} is missing quick effects!", id);
        }
        else{
            generatedBattleCard.setQuickEffects(readCardEffects(id,cardJson.quickEffects));
        }
        //set advanced effects
        if (cardJson.advancedEffects == null) {
            Battlecards.LOGGER.warn("Card {} is missing charge effects!", id);
        }
        else{
            generatedBattleCard.setChargeEffects(readCardEffects(id,cardJson.advancedEffects));
        }
        return new Pair<>(cardID,generatedBattleCard);
    }

    public static List<CardEffect> readCardEffects(Identifier id, String name, JsonElement element){
        try {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has(name)){
                JsonArray jsonArray = obj.get(name).getAsJsonArray();
                return readCardEffects(id, jsonArray);
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading element {} in {}",name,id);
        }
        return List.of();
    }

    public static List<CardEffect> readCardEffects(Identifier id, JsonArray json){
        List<CardEffect> cardEffects = new ArrayList<>();
        for (JsonElement entry:
                json) {
            try{
                JsonObject obj = entry.getAsJsonObject();
                if (!obj.has("effect")) {
                    Battlecards.LOGGER.error("Missing effect type component in {}",id.toString());
                }
                else{
                    String effectType = obj.get("effect").getAsString();
                    if(effectSerializers.containsKey(effectType)){
                        cardEffects.add(effectSerializers.get(effectType).readFromJson(id, entry));
                    }
                    else {
                        Battlecards.LOGGER.error("No matching effect serializer for {} in {}.",effectType,id.toString());
                    }
                }
            }
            catch (Exception e){
                Battlecards.LOGGER.error("Error parsing {}'s effects!",id);
            }
        }
        return cardEffects;
    }

    public static int readOrDefaultInt(Identifier id, String name, JsonElement entry, int defaultValue) {
        try {
            JsonObject obj = entry.getAsJsonObject();
            if (obj.has(name)) {
                return obj.get(name).getAsInt();
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading integer in entry {} in {}!",name,id);
        }
        return defaultValue;
    }

    public static float readOrDefaultFloat(Identifier id, String name, JsonElement entry, float defaultValue) {
        try{
            JsonObject obj = entry.getAsJsonObject();
            if (obj.has(name)) {
                return obj.get(name).getAsFloat();
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading float in entry {} in {}!",name,id);
        }
        return defaultValue;
    }

    public static boolean readOrDefaultBoolean(Identifier id, String name, JsonElement entry, boolean defaultValue) {
        try{
            JsonObject obj = entry.getAsJsonObject();
            if (obj.has(name)) {
                return obj.get(name).getAsBoolean();
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading boolean in entry {} in {}!",name,id);
        }
        return defaultValue;
    }

    public static String readOrDefaultString(Identifier id, String name, JsonElement entry, String defaultValue) {
        try{
            JsonObject obj = entry.getAsJsonObject();
            if (obj.has(name)) {
                return obj.get(name).getAsString();
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading boolean in entry {} in {}!",name,id);
        }
        return defaultValue;
    }

    public static void registerCardEffectSerializer(String id, CardEffectSerializer serializer){
        effectSerializers.put(id,serializer);
    }
}