package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalarCoordinates;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.GeneratedBattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardSerializer {
    private static final Map<String, CardEffectSerializer> effectSerializers = new HashMap<>();

    public CardSerializerOutput read(Identifier id, CardJsonFormat cardJson) {
        CardSerializerOutput output = new CardSerializerOutput();
        output.battleCard = new GeneratedBattleCard();

        //set internal id
        if (cardJson.id == null) {
            Battlecards.LOGGER.warn("Card {} is missing an id! Defaulting to directory path.", id);
            output.id = id;
        }
        else {
            output.id = new Identifier(cardJson.id);
        }
        output.battleCard.setID(output.id);

        //set effect cost
        output.battleCard.setChargeEffectCost(cardJson.cost);

        //set quick keywords
        if (cardJson.quickKeywords != null) {
            if(!cardJson.replace){
                Battlecards.LOGGER.warn("Card {} has quick keywords in supplemental json. These will likely be overwritten.", id);
            }
            for (JsonElement entry:
                    cardJson.quickKeywords) {
                output.battleCard.addQuickKeyword(entry.getAsString());
            }
        }

        //set charge keywords
        if (cardJson.chargeKeywords != null) {
            if(!cardJson.replace){
                Battlecards.LOGGER.warn("Card {} has charge keywords in supplemental json. These will likely be overwritten.", id);
            }
            for (JsonElement entry:
                    cardJson.chargeKeywords) {
                output.battleCard.addChargeKeyword(entry.getAsString());
            }
        }

        //set quick effects
        if (cardJson.quickEffects == null) {
            if(cardJson.replace) {
                Battlecards.LOGGER.warn("Card {} is missing quick effects!", id);
            }
        }
        else{
            if(!cardJson.replace){
                Battlecards.LOGGER.warn("Card {} has quick effects in supplemental json. These will likely be overwritten.", id);
            }
            output.battleCard.setQuickEffects(readCardEffects(id,cardJson.quickEffects));
        }

        //set advanced effects
        if (cardJson.chargeEffects == null) {
            if(cardJson.replace) {
                Battlecards.LOGGER.warn("Card {} is missing charge effects!", id);
            }
        }
        else{
            if(!cardJson.replace){
                Battlecards.LOGGER.warn("Card {} has charge effects in supplemental json. These will likely be overwritten.", id);
            }
            output.battleCard.setChargeEffects(readCardEffects(id,cardJson.chargeEffects));
        }

        if(cardJson.scrapValue != null){
            if(!cardJson.replace) {
                Battlecards.LOGGER.warn("Card {} has scrap value in supplemental json. This will likely be overwritten.", id);
            }
            try{
                Item item = Registries.ITEM.get(Identifier.tryParse(cardJson.scrapValue.get("item").getAsString()));
                if(item == Items.AIR){
                    Battlecards.LOGGER.error("Scrap value of {} is air. This is most likely due to a misspelled identifier", id.toString());
                }
                int count = cardJson.scrapValue.get("count").getAsInt();
                output.battleCard.setScrapValue(new ItemStack(item,count));
            }
            catch (Exception e){
                Battlecards.LOGGER.error("Error parsing scrap value of {}. Defaulting to 1 Cardfetti.", id.toString());
                output.battleCard.setScrapValue(new ItemStack(BCItems.CARDFETTI,1));
            }
        }
        else{
            output.battleCard.setScrapValue(new ItemStack(BCItems.CARDFETTI,1));
        }

        if(cardJson.variants != null){
            int i = 0;
            for (JsonElement entry:
                    cardJson.variants) {
                i++;
                try{
                    output.battleCard.addVariant(Identifier.tryParse(entry.getAsString()));
                }
                catch (Exception e){
                    Battlecards.LOGGER.error("Error parsing variant {} of card {}.", i, id.toString());
                }
            }
        }

        output.replace = cardJson.replace;

        return output;
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
            CardEffect effect = readCardEffect(entry,id);
            if(effect != null){
                cardEffects.add(effect);
            }
        }
        return cardEffects;
    }

    public static CardEffect readCardEffect(JsonElement entry, Identifier id){
        try{
            JsonObject obj = entry.getAsJsonObject();
            if (!obj.has("effect")) {
                Battlecards.LOGGER.error("Missing effect type component in {}",id.toString());
            }
            else{
                String effectType = obj.get("effect").getAsString();
                if(effectSerializers.containsKey(effectType)){
                    return effectSerializers.get(effectType).readFromJson(id, entry);
                }
                else {
                    Battlecards.LOGGER.error("No matching effect serializer for {} in {}.",effectType,id.toString());
                }
            }
        }
        catch (Exception e){
            Battlecards.LOGGER.error("Error parsing {}'s effects!",id);
        }
        return null;
    }

    public static CardScalar readOrDefaultScalar(Identifier id, String name, JsonElement entry, CardScalar defaultScalar) {
        JsonObject obj = entry.getAsJsonObject();
        if (obj.has(name)) {
            JsonElement namedElement = obj.get(name);
            CardScalar output = readScalar(id, namedElement);
            if (output != null) {
                return output;
            }
            else{
                Battlecards.LOGGER.error("Failed to read scalar {} in {}!",name, id);
            }
        }
        return defaultScalar;
    }

    public static CardScalar readOrDefaultScalar(Identifier id, String name, JsonElement entry, float defaultValue) {
        return readOrDefaultScalar(id,name,entry,new ConstantScalarEffect(defaultValue));
    }

    private static CardScalar readScalar(Identifier id, JsonElement namedElement) {
        if (namedElement.isJsonPrimitive()) {
            try {
                return new ConstantScalarEffect(namedElement.getAsFloat());
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error reading primitive scalar in {}!", id);
            }
        } else {
            try {
                CardEffect effect = readCardEffect(namedElement, id);
                if (effect instanceof CardScalar scalar) {
                    return scalar;
                } else {
                    Battlecards.LOGGER.error("Non scalar CardEffect {} used as scalar in {}!",((JsonObject)namedElement).get("effect").getAsString(), id);
                }
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error reading scalar in {}!", id);
            }
        }
        return null;
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
            Battlecards.LOGGER.error("Error reading string in entry {} in {}!",name,id);
        }
        return defaultValue;
    }

    public static List<String> readOrDefaultStringList(Identifier id, String name, JsonElement entry, ArrayList<String> defaultList) {
        JsonObject obj = entry.getAsJsonObject();
        if (obj.has(name)) {
            JsonElement namedElement = obj.get(name);
            if (namedElement.isJsonPrimitive()) {
                try {
                    List<String> output = new ArrayList<>();
                    output.add(namedElement.getAsString());
                    return output;
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error reading primitive string in entry {} in {}!", name, id);
                }
            }
            else if (namedElement.isJsonArray()) {
                try {
                    JsonArray jArray = namedElement.getAsJsonArray();
                    List<String> output = new ArrayList<>();
                    for (JsonElement element:
                            jArray) {
                        output.add(element.getAsString());
                    }
                    return output;
                }
                catch(Exception e){
                Battlecards.LOGGER.error("Error reading string array in entry {} in {}!", name, id);
                }
            }
            else{
                Battlecards.LOGGER.error("Non string, non list found when string or list expected in entry {} in {}!", name, id);
            }
        }
        return defaultList;
    }

    public static List<CardScalarCoordinates> readOrDefaultCoordinatesList(Identifier id, String name, JsonElement element, ArrayList<CardScalarCoordinates> defaultList) {
        try {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has(name)){
                JsonArray jsonArray = obj.get(name).getAsJsonArray();
                ArrayList<CardScalarCoordinates> posList = new ArrayList<>();
                int i = 0;
                for (JsonElement entry:
                        jsonArray) {
                    i++;
                    CardScalarCoordinates newCoord = readCoordinates(id,entry);
                    if(newCoord != null) {
                        posList.add(readCoordinates(id, entry));
                    }
                    else{
                        Battlecards.LOGGER.error("Error reading block position entry {} in {} in {}",i,name,id);
                    }
                }
                return posList;
            }
        } catch (Exception e) {
            Battlecards.LOGGER.error("Error reading block position list {} in {}",name,id);
        }
        return defaultList;
    }

    private static CardScalarCoordinates readCoordinates(Identifier id, JsonElement entry) {
        try {
            boolean err = false;
            JsonArray coords = entry.getAsJsonArray();
            CardScalarCoordinates output = new CardScalarCoordinates();
            output.x = readScalar(id,coords.get(0));
            output.y = readScalar(id,coords.get(1));
            output.z = readScalar(id,coords.get(2));
            if(output.x == null){
                Battlecards.LOGGER.error("Failed to read x coordinate in {}!", id);
                err = true;
            }
            if(output.y == null){
                Battlecards.LOGGER.error("Failed to read y coordinate in {}!", id);
                err = true;
            }
            if(output.z == null){
                Battlecards.LOGGER.error("Failed to read z coordinate in {}!", id);
                err = true;
            }
            if(err){
                return null;
            }
            return output;
        }
        catch (Exception e){
            Battlecards.LOGGER.error("Error reading block position {}",id);
        }
        return null;
    }

    public static void registerCardEffectSerializer(String id, CardEffectSerializer serializer){
        effectSerializers.put(id,serializer);
    }
}