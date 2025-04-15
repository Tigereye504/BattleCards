package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class BoosterPackManager implements SimpleSynchronousResourceReloadListener {
    private static final String RESOURCE_LOCATION = "battlecard_booster";
    public static final String ID_NBTKEY = "booster_pack";
    public static BoosterPackManager INSTANCE = new BoosterPackManager();
    private final BoosterPackSerializer SERIALIZER = new BoosterPackSerializer();
    //the first Identifier is the loot table that drops the booster pack, the list is the booster packs it can drop
    public static Map<Identifier, List<BoosterPackData>> LootTableInjections = new HashMap<>();
    public static Set<Identifier> BoosterPacks = new HashSet<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Battlecards.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        LootTableInjections.clear();
        BoosterPacks.clear();
        Battlecards.LOGGER.info("Loading booster packs.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                Pair<BoosterPackData, List<Identifier>> boosterPair = SERIALIZER.read(id,new Gson().fromJson(reader, BoosterPackJsonFormat.class));
                BoosterPacks.add(boosterPair.getLeft().id);
                for(Identifier entity : boosterPair.getRight()) {
                    List<BoosterPackData> boosterList = LootTableInjections.getOrDefault(entity, new ArrayList<>());
                    boosterList.add(boosterPair.getLeft());
                    LootTableInjections.put(entity, boosterList);
                }

            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Loaded "+ BoosterPacks.size()+" booster packs.");
    }

    public static boolean hasEntry(Identifier id){
        return LootTableInjections.containsKey(id);
    }

    public static List<BoosterPackData> getEntry(Identifier id){
        return LootTableInjections.getOrDefault(id,List.of());
    }

    public static ItemStack generateBoosterPackItemstack(Identifier id){
        ItemStack stack = new ItemStack(BCItems.BOOSTER_PACK);
        stack.getOrCreateNbt().putString(ID_NBTKEY,id.toString());
        return stack;
    }

}
