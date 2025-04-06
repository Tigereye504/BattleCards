package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
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
        cardID = new Identifier(cardJson.id);

        //set texture paths
        if (cardJson.art == null) {
            Battlecards.LOGGER.warn("Card {} is missing art texture!", id);
            cardJson.art = DEFAULT_TEXTURE;
        }
        generatedBattleCard.setArt(new Identifier(cardJson.art));

        if (cardJson.icon == null) {
            Battlecards.LOGGER.warn("Card {} is missing icon texture!", id);
            cardJson.icon = DEFAULT_TEXTURE;
        }
        generatedBattleCard.setIcon(new Identifier(cardJson.icon));

        if (cardJson.background == null) {
            Battlecards.LOGGER.warn("Card {} is missing background texture!", id);
            cardJson.background = DEFAULT_TEXTURE;
        }
        generatedBattleCard.setBackground(new Identifier(cardJson.background));

        //set effect cost
        generatedBattleCard.setChargeEffectCost(cardJson.cost);

        //get descriptions
        if(cardJson.quickDescription == null) {
            Battlecards.LOGGER.warn("Card {} is missing quick description!", id);
        }
        else{
            generatedBattleCard.setBasicDescription(cardJson.quickDescription);
        }
        if(cardJson.chargeDescription == null) {
            Battlecards.LOGGER.warn("Card {} is missing charge description!", id);
        }
        else {
            generatedBattleCard.setChargeDescription(cardJson.chargeDescription);
        }

        //set quick effects
        if (cardJson.quickEffects == null) {
            Battlecards.LOGGER.warn("Card {} is missing quick effects!", id);
        }
        else{
            generatedBattleCard.setBasicEffects(readCardEffects(id,cardJson.quickEffects));
        }
        //set advanced effects
        if (cardJson.advancedEffects == null) {
            Battlecards.LOGGER.warn("Card {} is missing charge effects!", id);
        }
        else{
            generatedBattleCard.setChargeEffects(readCardEffects(id,cardJson.advancedEffects));
        }
        Battlecards.LOGGER.warn(id.toTranslationKey());
        return new Pair<>(cardID,generatedBattleCard);
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

    public static void registerCardEffectSerializer(String id, CardEffectSerializer serializer){
        effectSerializers.put(id,serializer);
    }
}