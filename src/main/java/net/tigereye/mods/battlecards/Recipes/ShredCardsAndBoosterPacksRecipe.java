package net.tigereye.mods.battlecards.Recipes;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.BoosterPackItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;
import net.tigereye.mods.battlecards.registration.BCItems;

public class ShredCardsAndBoosterPacksRecipe extends SpecialCraftingRecipe {

    public ShredCardsAndBoosterPacksRecipe(Identifier id, CraftingRecipeCategory craftingRecipeCategory) {
        super(id, craftingRecipeCategory);
    }

    public boolean matches(RecipeInputInventory craftingInventory, World world) {
        boolean found = false;
        for(int i = 0; i < craftingInventory.getWidth(); ++i) {
            for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                ItemStack itemStack = craftingInventory.getStack(i + j * craftingInventory.getWidth());
                Item item = itemStack.getItem();
                if (item == BCItems.BOOSTER_PACK || item == BCItems.BATTLECARD) {
                    if(found || CardOwningItem.isCardOwned(itemStack)){
                        return false;
                    }
                    found = true;
                }
                else if (item != Items.AIR) {
                    return false;
                }
            }
        }
        return found;
    }

    public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager registryManager) {
        ItemStack found = null;
        for(int i = 0; i < craftingInventory.getWidth(); ++i) {
            for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                ItemStack itemStack = craftingInventory.getStack(i + j * craftingInventory.getWidth());
                Item item = itemStack.getItem();
                if (item == BCItems.BOOSTER_PACK || item == BCItems.BATTLECARD) {
                    if(found != null || CardOwningItem.isCardOwned(itemStack)){
                        return ItemStack.EMPTY;
                    }
                    found = itemStack;
                }
                else if (item != Items.AIR) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if(found == null){
            return ItemStack.EMPTY;
        }

        if(found.getItem() == BCItems.BOOSTER_PACK){
            return BoosterPackManager.boosterPackScrapValue.getOrDefault(
                    BoosterPackItem.getBoosterPackID(found),ItemStack.EMPTY).copy();
        }
        BattleCard card = CardManager.readNBTBattleCard(found);
        if(card != null){
            //TODO: return the card's sleeve to the player, if any
            return card.getScrapValue();
        }

        return ItemStack.EMPTY;
    }

    public boolean fits(int width, int height) {
        return (width >= 3 && height >= 3);
    }

    public RecipeSerializer<?> getSerializer() {
        return BCItems.SHRED_CARD_BOOSTER_PACKS_RECIPE_SERIALIZER;
    }
}
