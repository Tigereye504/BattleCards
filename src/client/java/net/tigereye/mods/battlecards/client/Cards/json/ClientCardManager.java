package net.tigereye.mods.battlecards.client.Cards.json;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ClientCardManager implements SimpleSynchronousResourceReloadListener {
    private static final String RESOURCE_LOCATION = "battlecard";
    public static final String NBT_KEY = "battlecard";
    private final ClientCardSerializer SERIALIZER = new ClientCardSerializer();
    public static Map<Identifier, BattleCardClientData> GeneratedCardDatas = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Battlecards.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        GeneratedCardDatas.clear();
        Battlecards.LOGGER.info("Loading cards.");

        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                Pair<Identifier, BattleCardClientData> cardPair = SERIALIZER.read(id,new Gson().fromJson(reader, ClientCardJsonFormat.class));
                GeneratedCardDatas.put(cardPair.getLeft(),cardPair.getRight());
            } catch(Exception e) {
                Battlecards.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Battlecards.LOGGER.info("Client Loaded "+ GeneratedCardDatas.size()+" cards.");
    }

    public static boolean hasEntry(Identifier id){
        return GeneratedCardDatas.containsKey(id);
    }

    public static BattleCardClientData getEntry(Identifier id){
        return GeneratedCardDatas.get(id);
    }

    public static BattleCardClientData readNBTBattleCard(ItemStack itemStack) {
        return readNBTBattleCard(itemStack.getNbt());
    }

    public static BattleCardClientData readNBTBattleCard(NbtCompound nbt) {
        if(nbt != null) {
            Identifier cardID = new Identifier(nbt.getString(NBT_KEY));
            if(hasEntry(cardID)) {
                return getEntry(cardID);
            }
        }
        return null;
    }

    public static ItemStack generateCardItemstack(Identifier id){
        ItemStack stack = new ItemStack(BCItems.BATTLECARD);
        stack.getOrCreateNbt().putString(NBT_KEY,id.toString());
        return stack;
    }

}
