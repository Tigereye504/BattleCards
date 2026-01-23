package net.tigereye.mods.battlecards.Cards.Json;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.BlankBattleCard;
import net.tigereye.mods.battlecards.Cards.GeneratedBattleCard;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager implements SimpleSynchronousResourceReloadListener {
    //TODO: move chances to config
    private static final float VARIANT_ART_BASE_CHANCE = 0.05f;
    private static final float VARIANT_ART_BASE_CHANCE_PER_LUCK = 0.01f;
    private static final String RESOURCE_LOCATION = "battlecard";
    public static final String ID_KEY = "battlecard";
    public static final String VARIANT_KEY = "battlecard_variant";
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
                //TODO: let art variants be merged into existing cards.
                CardSerializerOutput output = SERIALIZER.read(id,new Gson().fromJson(reader,CardJsonFormat.class));
                if (!GeneratedCards.containsKey(output.id)) {
                    GeneratedCards.put(output.id,output.battleCard);
                }
                else {
                    BattleCard oldCard = GeneratedCards.get(output.id);
                    if(output.replace){
                        for(Identifier variant : oldCard.getVariants()) {
                            output.battleCard.addVariant(variant);
                        }
                        GeneratedCards.put(output.id,output.battleCard);
                    }
                    else{
                        for(Identifier variant : output.battleCard.getVariants()) {
                            oldCard.addVariant(variant);
                        }
                        GeneratedCards.put(output.id,oldCard);
                    }
                }
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
            Identifier cardID = new Identifier(nbt.getString(ID_KEY));
            if(hasEntry(cardID)) {
                return getEntry(cardID);
            }
        }
        return new BlankBattleCard();
    }

    public static ItemStack generateCardItemstack(Identifier id, float luck, Random random){
        BattleCard card = GeneratedCards.get(id);
        Identifier varID = null;
        if(card != null){
            List<Identifier> variantList = card.getVariants();
            if(!(card.getVariants().isEmpty()) && random.nextFloat() < VARIANT_ART_BASE_CHANCE+(VARIANT_ART_BASE_CHANCE_PER_LUCK*luck)){
                varID = variantList.get(random.nextInt(card.getVariants().size()));
            }
        }
        return generateCardItemstack(id, varID);
    }

    public static ItemStack generateCardItemstack(Identifier id){
        return generateCardItemstack(id, null);
    }
    public static ItemStack generateCardItemstack(Identifier id, Identifier varID){
        ItemStack stack = new ItemStack(BCItems.BATTLECARD);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(ID_KEY,id.toString());
        if(varID != null) {
            nbt.putString(VARIANT_KEY, varID.toString());
        }
        return stack;
    }

}
