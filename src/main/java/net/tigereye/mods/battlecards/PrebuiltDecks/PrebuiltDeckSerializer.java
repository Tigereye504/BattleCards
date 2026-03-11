package net.tigereye.mods.battlecards.PrebuiltDecks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.DropRateData;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.registration.BCItems;

public class PrebuiltDeckSerializer {
    //remember: the first identifier is the entity, the second is the chest cavity type
    public PrebuiltDeckData read(Identifier fileID, PrebuiltDeckJsonFormat pbdJson) {

        if (pbdJson.id == null) {
            throw new JsonSyntaxException("Prebuilt Deck " + fileID + " lacks an fileID");
        }
        if (pbdJson.sourceLootTables == null) {
            Battlecards.LOGGER.warn("Prebuilt Deck " + fileID + " has no sourceLootTable entry");
        }

        PrebuiltDeckData prebuiltDeckData = new PrebuiltDeckData();
        prebuiltDeckData.id = pbdJson.id;
        prebuiltDeckData.deck = new ItemStack(BCItems.DECK);

        prebuiltDeckData.sourceLootTables = DropRateData.readSourceLootTablesFromJson(pbdJson.sourceLootTables, prebuiltDeckData.id,fileID.toString());

        int i = 0;
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
                    Battlecards.LOGGER.error("Error parsing card {} in {}", i, fileID.toString());
                }
            }
            prebuiltDeckData.deck.setSubNbt("Items",nbtList);
        }
        else{
            Battlecards.LOGGER.warn("No cards in Prebuilt Deck {}!", fileID.toString());
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
                    Battlecards.LOGGER.error("Error parsing color in {}", fileID.toString());
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

