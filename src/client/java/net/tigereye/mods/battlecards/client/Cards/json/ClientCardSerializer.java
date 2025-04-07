package net.tigereye.mods.battlecards.client.Cards.json;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.client.Cards.GeneratedBattleCardClientData;

public class ClientCardSerializer {
    private static final String DEFAULT_TEXTURE = "battlecards:item/battlecard";

    public Pair<Identifier, BattleCardClientData> read(Identifier id, ClientCardJsonFormat cardJson) {
        GeneratedBattleCardClientData clientData = new GeneratedBattleCardClientData();

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
        clientData.setArt(new Identifier(cardJson.art));

        if (cardJson.icon == null) {
            Battlecards.LOGGER.warn("Card {} is missing icon texture!", id);
            cardJson.icon = DEFAULT_TEXTURE;
        }
        clientData.setIcon(new Identifier(cardJson.icon));

        if (cardJson.background == null) {
            Battlecards.LOGGER.warn("Card {} is missing background texture!", id);
            cardJson.background = DEFAULT_TEXTURE;
        }
        clientData.setBackground(new Identifier(cardJson.background));

        return new Pair<>(cardID,clientData);
    }
}