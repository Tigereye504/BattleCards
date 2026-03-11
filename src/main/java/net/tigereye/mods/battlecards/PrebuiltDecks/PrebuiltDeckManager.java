package net.tigereye.mods.battlecards.PrebuiltDecks;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.DropRateData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class PrebuiltDeckManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "battlecard_prebuilt";
    public static PrebuiltDeckManager INSTANCE = new PrebuiltDeckManager();
    private final PrebuiltDeckSerializer SERIALIZER = new PrebuiltDeckSerializer();
    //the first String is the loot table that drops the booster pack, the list is the booster packs it can drop
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, List<DropRateData>> lootTableInjections = new HashMap<>();
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, ItemStack> prebuiltDecks = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Battlecards.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        lootTableInjections.clear();
        Battlecards.LOGGER.info("Loading booster packs.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                PrebuiltDeckData prebuiltDeckData = SERIALIZER.read(id,new Gson().fromJson(reader, PrebuiltDeckJsonFormat.class));
                prebuiltDecks.put(prebuiltDeckData.id,prebuiltDeckData.deck);
                prebuiltDeckData.sourceLootTables.forEach((sourceID, dropRates) -> {
                    List<DropRateData> deckList = lootTableInjections.getOrDefault(sourceID, new ArrayList<>());
                    deckList.add(dropRates);
                    lootTableInjections.put(sourceID, deckList);
                });

            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Loaded "+ prebuiltDecks.size()+" prebuilt decks.");
    }

}
