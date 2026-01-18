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
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackDropRates;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
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
    //the first String is the loot table that drops the booster pack, the list is the booster packs it can drop
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, List<BoosterPackDropRates>> lootTableInjections = new HashMap<>();
    //The String would be an Identifier, but Maps don't properly compare them.
    public static Map<String, BoosterPackCardList> boosterPackCardList = new HashMap<>();
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
                for(Identifier entity : boosterPackData.mobs) {
                    String deIdentifiedEntity = entity.toString();
                    List<BoosterPackDropRates> boosterList = lootTableInjections.getOrDefault(deIdentifiedEntity, new ArrayList<>());
                    boosterList.add(boosterPackData.getDropRates());
                    lootTableInjections.put(deIdentifiedEntity, boosterList);
                }
                boosterPackCardList.put(boosterPackData.id.toString(),boosterPackData.getCardList());

            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Loaded "+ boosterPacks.size()+" booster packs.");
    }

    public static boolean hasEntry(Identifier id){
        return lootTableInjections.containsKey(id);
    }

    public static List<BoosterPackDropRates> getEntry(Identifier id){
        return lootTableInjections.getOrDefault(id,List.of());
    }

    public static ItemStack generateBoosterPackItemstack(Identifier id){
        ItemStack stack = new ItemStack(BCItems.BOOSTER_PACK);
        stack.getOrCreateNbt().putString(ID_NBTKEY,id.toString());
        return stack;
    }

    public static List<ItemStack> generateBoosterPackContents(Identifier id, PlayerEntity user){
        //TODO: account for cardfetti luck
        return generateBoosterPackContents(id, user.getLuck(),user.getRandom());
    }

    public static List<ItemStack> generateBoosterPackContents(Identifier id, float luck, Random random){
        //TODO: use luck to roll to upgrade commons to rares
        List<ItemStack> chosenCards = new ArrayList<>();
        BoosterPackCardList cardList = boosterPackCardList.get(id.toString());
        if(cardList != null) {
            chosenCards.addAll(rollNCardsFromSet(cardList.commonCards,cardList.commonCount,luck,random));
            chosenCards.addAll(rollNCardsFromSet(cardList.rareCards,cardList.rareCount,luck,random));
        }
        return chosenCards;
    }

    private static List<ItemStack> rollNCardsFromSet(Set<Identifier> cardSet, int count, float luck, Random random){
        //TODO: use luck to roll for shiny/art variants (most likely this should be done in CardManager)
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
                    chosenCards.add(CardManager.generateCardItemstack(cardID));
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
