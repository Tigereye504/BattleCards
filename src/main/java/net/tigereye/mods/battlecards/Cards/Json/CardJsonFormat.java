package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.JsonArray;

public class CardJsonFormat {
    String id;
    String art;
    String icon;
    String background;
    String quickDescription;
    String chargeDescription;
    int cost;
    JsonArray quickEffects;
    JsonArray chargeEffects;
}
