package net.tigereye.mods.battlecards.BoosterPacks.Json;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackCardList;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackDropRate;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.registration.BCItems;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class BoosterPackManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "battlecard_booster";
    public static final String ID_NBTKEY = "booster_pack";
    public static BoosterPackManager INSTANCE = new BoosterPackManager();
    private final BoosterPackSerializer SERIALIZER = new BoosterPackSerializer();
    //the first String is the loot table that drops the booster pack, the list is the booster packs it can drop
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, List<BoosterPackDropRate>> lootTableInjections = new HashMap<>();
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, BoosterPackCardList> boosterPackCardList = new HashMap<>();
    public static Map<String, ItemStack> boosterPackScrapValue = new HashMap<>();
    public static Set<Identifier> boosterPacks = new HashSet<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Battlecards.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        lootTableInjections.clear();
        boosterPacks.clear();
        Battlecards.LOGGER.info("Loading booster packs.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                BoosterPackData boosterPackData = SERIALIZER.read(id,new Gson().fromJson(reader, BoosterPackJsonFormat.class));
                boosterPacks.add(boosterPackData.id);
                boosterPackData.sourceLootTables.forEach((sourceID, dropRates) -> {
                    String deIdentifiedEntity = sourceID.toString();
                    List<BoosterPackDropRate> boosterList = lootTableInjections.getOrDefault(deIdentifiedEntity, new ArrayList<>());
                    boosterList.add(dropRates);
                    lootTableInjections.put(deIdentifiedEntity, boosterList);
                });
                boosterPackCardList.put(boosterPackData.id.toString(),boosterPackData.getCardList());
                boosterPackScrapValue.put(boosterPackData.id.toString(),boosterPackData.scrapValue);

            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Loaded "+ boosterPacks.size()+" booster packs.");
    }

    public static boolean hasEntry(Identifier id){
        return lootTableInjections.containsKey(id.toString());
    }

    public static List<BoosterPackDropRate> getEntry(Identifier id){
        return lootTableInjections.getOrDefault(id.toString(),List.of());
    }

    public static ItemStack generateBoosterPackItemstack(Identifier id){
        ItemStack stack = new ItemStack(BCItems.BOOSTER_PACK);
        stack.getOrCreateNbt().putString(ID_NBTKEY,id.toString());
        return stack;
    }

    public static List<ItemStack> generateBoosterPackContents(Identifier id, PlayerEntity user){
        float luck = user.getLuck() + (user.hasStatusEffect(BCStatusEffects.CARDFETTI_SACRIFICE) ?
                user.getStatusEffect(BCStatusEffects.CARDFETTI_SACRIFICE).getAmplifier()+1 : 0);
        user.removeStatusEffect(BCStatusEffects.CARDFETTI_SACRIFICE);
        return generateBoosterPackContents(id,luck,user.getRandom());
    }

    private static List<ItemStack> generateBoosterPackContents(Identifier id, float luck, Random random){
        List<ItemStack> chosenCards = new ArrayList<>();
        BoosterPackCardList cardList = boosterPackCardList.get(id.toString());
        if(cardList != null) {
            int upgrades = 0;
            for (int i = 0; i < cardList.commonCount; i++) {
                if(random.nextFloat() < (Battlecards.CONFIG.BOOSTER_PACK_BASE_UPGRADE_CHANCE +
                        (Battlecards.CONFIG.BOOSTER_PACK_LUCK_SCALING_UPGRADE_CHANCE *luck))/(upgrades+1)){
                    upgrades++;
                }
            }
            chosenCards.addAll(rollNCardsFromSet(cardList.commonCards,cardList.commonCount-upgrades,luck,random));
            chosenCards.addAll(rollNCardsFromSet(cardList.rareCards,cardList.rareCount+upgrades,luck,random));
        }
        return chosenCards;
    }

    private static List<ItemStack> rollNCardsFromSet(Set<Identifier> cardSet, int count, float luck, Random random){
        List<ItemStack> chosenCards = new ArrayList<>();
        int options = cardSet.size();
        List<Integer> picks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            picks.add(random.nextInt(options));
        }
        picks.sort(Integer::compareTo);
        int i = 0;
        Iterator<Identifier> iter = cardSet.iterator();
        while(iter.hasNext() && !picks.isEmpty()){
            if (i == picks.get(0)) {
                Identifier cardID = iter.next();
                do {
                    picks.remove(0);
                    chosenCards.add(CardManager.generateCardItemstack(cardID,luck,random));
                } while (!picks.isEmpty() && i == picks.get(0));
            }
            else{
                iter.next();
            }
            i++;
        }
        return chosenCards;
    }

}
