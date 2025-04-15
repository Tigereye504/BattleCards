package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.BlankBattleCard;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class CardManager implements SimpleSynchronousResourceReloadListener {
    private static final String RESOURCE_LOCATION = "battlecard";
    public static final String NBT_KEY = "battlecard";
    private final CardSerializer SERIALIZER = new CardSerializer();
    public static Map<Identifier, BattleCard> GeneratedCards = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Battlecards.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        GeneratedCards.clear();
        Battlecards.LOGGER.info("Loading cards.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                Pair<Identifier, BattleCard> cardPair = SERIALIZER.read(id,new Gson().fromJson(reader,CardJsonFormat.class));
                GeneratedCards.put(cardPair.getLeft(),cardPair.getRight());
            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Loaded "+ GeneratedCards.size()+" cards.");
    }

    public static boolean hasEntry(Identifier id){
        return GeneratedCards.containsKey(id);
    }

    public static BattleCard getEntry(Identifier id){
        return GeneratedCards.getOrDefault(id,new BlankBattleCard());
    }

    public static BattleCard readNBTBattleCard(ItemStack itemStack) {
        return readNBTBattleCard(itemStack.getNbt());
    }

    public static BattleCard readNBTBattleCard(NbtCompound nbt) {
        if(nbt != null) {
            Identifier cardID = new Identifier(nbt.getString(NBT_KEY));
            if(hasEntry(cardID)) {
                return getEntry(cardID);
            }
        }
        return new BlankBattleCard();
    }

    public static ItemStack generateCardItemstack(Identifier id){
        ItemStack stack = new ItemStack(BCItems.BATTLECARD);
        stack.getOrCreateNbt().putString(NBT_KEY,id.toString());
        return stack;
    }

}
