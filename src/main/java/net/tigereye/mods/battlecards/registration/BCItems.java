package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.BattlecardsDeckItem;
import net.tigereye.mods.battlecards.Items.BoosterPackItem;
import net.tigereye.mods.battlecards.Items.CardfettiItem;
import net.tigereye.mods.battlecards.Items.GeneratedCardItem;
import net.tigereye.mods.battlecards.Items.sleeves.PennySleeveItem;
import net.tigereye.mods.battlecards.Items.sleeves.SimpleCardSleeve;
import net.tigereye.mods.battlecards.Recipes.PapercraftBoosterRecipe;
import net.tigereye.mods.battlecards.Recipes.ShredCardsAndBoosterPacksRecipe;

public class BCItems {
    public static final Item BATTLECARD = new GeneratedCardItem(new FabricItemSettings().maxCount(64));
    public static final Item BOOSTER_PACK = new BoosterPackItem(new FabricItemSettings().maxCount(64));
    public static final Item DECK = new BattlecardsDeckItem(new FabricItemSettings().maxCount(1));
    public static final Item CARDFETTI = new CardfettiItem(new FabricItemSettings().maxCount(64));
    public static final Item CARDFETTI_BRICK = new Item(new FabricItemSettings().maxCount(64));
    public static final Block CARDFETTI_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.BAMBOO_PLANKS));

    public static final Item SLEEVE_PENNY = new PennySleeveItem(new FabricItemSettings().maxCount(1));
    public static final Item SLEEVE_IRON = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),1.5f);
    public static final Item SLEEVE_GOLD = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),1.25f);
    public static final Item SLEEVE_DIAMOND = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),2f);
    public static final Item SLEEVE_NETHERITE = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),2.25f);
    public static final Item SLEEVE_GLOWSTONE = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),1,1,1);
    public static final Item SLEEVE_REDSTONE = new SimpleCardSleeve(new FabricItemSettings().maxCount(64),1,2,0);

    public static final SpecialRecipeSerializer<PapercraftBoosterRecipe> PAPERCRAFT_BOOSTER_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(PapercraftBoosterRecipe::new);
    public static final SpecialRecipeSerializer<ShredCardsAndBoosterPacksRecipe> SHRED_CARD_BOOSTER_PACKS_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(ShredCardsAndBoosterPacksRecipe::new);

    public static final ItemGroup BATTLECARDS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(BATTLECARD))
            .displayName(Text.translatable("itemgroup.battlecards.battlecards"))
            .entries(((displayContext, entries) -> {
                entries.add(CARDFETTI);
                entries.add(CARDFETTI_BRICK);
                entries.add(CARDFETTI_BLOCK.asItem());
                entries.add(DECK);
                entries.add(SLEEVE_PENNY);
                entries.add(SLEEVE_IRON);
                entries.add(SLEEVE_GOLD);
                entries.add(SLEEVE_DIAMOND);
                entries.add(SLEEVE_NETHERITE);
                entries.add(SLEEVE_GLOWSTONE);
                entries.add(SLEEVE_REDSTONE);
                CardManager.GeneratedCards.keySet().stream().sorted().forEachOrdered((id) -> {
                    entries.add(CardManager.generateCardItemstack(id));
                    CardManager.GeneratedCards.get(id).getVariants().forEach((varID) ->{
                        entries.add(CardManager.generateCardItemstack(id,varID));
                    });
                });
                BoosterPackManager.boosterPacks.stream().sorted().forEachOrdered((id) -> {
                    entries.add(BoosterPackManager.generateBoosterPackItemstack(id));
                });
            }))
            .build();

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "battlecard"), BATTLECARD);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "booster_pack"), BOOSTER_PACK);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "cardfetti"), CARDFETTI);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "cardfetti_brick"), CARDFETTI_BRICK);
        Registry.register(Registries.BLOCK, new Identifier(Battlecards.MODID, "cardfetti_block"), CARDFETTI_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "cardfetti_block"), new BlockItem(CARDFETTI_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "deck"), DECK);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/penny"), SLEEVE_PENNY);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/iron"), SLEEVE_IRON);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/gold"), SLEEVE_GOLD);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/diamond"), SLEEVE_DIAMOND);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/netherite"), SLEEVE_NETHERITE);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/glowstone"), SLEEVE_GLOWSTONE);
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "sleeve/redstone"), SLEEVE_REDSTONE);

        Registry.register(Registries.ITEM_GROUP, Identifier.of(Battlecards.MODID,"battlecards_item_group"), BATTLECARDS_ITEM_GROUP);

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Battlecards.MODID,"crafting_special_papercraft_booster"), PAPERCRAFT_BOOSTER_RECIPE_SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Battlecards.MODID,"crafting_special_shred_card_or_boosterpack"), SHRED_CARD_BOOSTER_PACKS_RECIPE_SERIALIZER);

        FuelRegistry.INSTANCE.add(CARDFETTI,100);
        FuelRegistry.INSTANCE.add(CARDFETTI_BRICK,400);
        FuelRegistry.INSTANCE.add(CARDFETTI_BLOCK,4000);
    }
}
