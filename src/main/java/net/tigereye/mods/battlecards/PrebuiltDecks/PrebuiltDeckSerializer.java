package net.tigereye.mods.battlecards.PrebuiltDecks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.DropRateData;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackData;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackJsonFormat;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.BattlecardsDeckItem;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.HashMap;
import java.util.HashSet;

public class PrebuiltDeckSerializer {
    //remember: the first identifier is the entity, the second is the chest cavity type
    public PrebuiltDeckData read(Identifier id, PrebuiltDeckJsonFormat pbdJson) {

        if (pbdJson.id == null) {
            throw new JsonSyntaxException("Prebuilt Deck " + id + " lacks an id");
        }
        if (pbdJson.sourceLootTables == null) {
            Battlecards.LOGGER.warn("Prebuilt Deck " + id + " has no sourceLootTable entry");
        }

        Identifier boosterPackID = new Identifier(pbdJson.id);
        PrebuiltDeckData prebuiltDeckData = new PrebuiltDeckData();
        prebuiltDeckData.sourceLootTables = new HashMap<>();
        prebuiltDeckData.id = pbdJson.id;


        int i = 0;
        if(pbdJson.sourceLootTables != null) {
            for (JsonElement entry :
                    pbdJson.sourceLootTables) {
                ++i;
                try {
                    JsonObject jObject = entry.getAsJsonObject();
                    DropRateData dropRate = new DropRateData();
                    dropRate.id = prebuiltDeckData.id;
                    dropRate.rate = jObject.has("rate") ? jObject.get("rate").getAsFloat() : 0;
                    dropRate.lootingRate = jObject.has("lootingRate") ? jObject.get("lootingRate").getAsFloat() : 0;
                    if (dropRate.rate == 0 && dropRate.lootingRate == 0) {
                        Battlecards.LOGGER.warn("Prebuilt Deck {} has no chance to drop from {}", id, jObject.get("id").getAsString());
                    }
                    prebuiltDeckData.sourceLootTables.put(jObject.get("id").getAsString(), dropRate);
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing mob identifier {} in {}'s entity list", i, id.toString());
                }
            }
        }
        prebuiltDeckData.deck = new ItemStack(BCItems.DECK);

        i = 0;
        if(pbdJson.cards != null) {
            NbtList nbtList = new NbtList();
            for (JsonElement entry :
                    pbdJson.cards) {
                ++i;
                try {
                    JsonObject cardJsonObj = entry.getAsJsonObject();
                    ItemStack card = readCard(cardJsonObj);
                    if(card != null) {
                        card.setCount(cardJsonObj.has("count") ? cardJsonObj.get("count").getAsInt() : 1);
                        nbtList.add(card.writeNbt(new NbtCompound()));
                    }
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing card {} in {}", i, id.toString());
                }
            }
            prebuiltDeckData.deck.setSubNbt("Items",nbtList);
        }
        else{
            Battlecards.LOGGER.warn("No cards in Prebuilt Deck {}!", id.toString());
        }

        if(pbdJson.name != null){
            prebuiltDeckData.deck.setCustomName(Text.translatable(pbdJson.name));
        }

        if(pbdJson.color != null){
            if(prebuiltDeckData.deck.getItem() instanceof DyeableItem dyeable){
                int color = 0xFFFFFF;
                try{
                    color =Integer.parseInt(pbdJson.color, 16);
                } catch (Exception e) {
                    Battlecards.LOGGER.error("Error parsing color in {}", id.toString());
                }
                dyeable.setColor(prebuiltDeckData.deck,color);
            }
        }

        return prebuiltDeckData;
    }

    private ItemStack readCard(JsonObject cardJsonObj){
        String id = cardJsonObj.has("id") ? cardJsonObj.get("id").getAsString() : null;
        String variant = cardJsonObj.has("variant") ? cardJsonObj.get("variant").getAsString() : null;
        if(id == null){return null;}
        ItemStack card = CardManager.generateCardItemstack(Identifier.tryParse(id),variant != null ? Identifier.tryParse(variant) : null);
        if(card.getItem() instanceof BattleCardItem bci && cardJsonObj.has("sleeve")){
            bci.setSleeve(card,cardJsonObj.get("sleeve").getAsString());
        }
        return card;
    }
}

