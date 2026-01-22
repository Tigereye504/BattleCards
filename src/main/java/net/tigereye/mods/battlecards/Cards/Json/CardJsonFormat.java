package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CardJsonFormat {
    String id;
    String art;
    String icon;
    String background;
    String quickDescription;
    String chargeDescription;
    int cost;
    JsonArray quickKeywords;
    JsonArray chargeKeywords;
    JsonArray quickEffects;
    JsonArray chargeEffects;
    JsonObject scrapValue;
}
