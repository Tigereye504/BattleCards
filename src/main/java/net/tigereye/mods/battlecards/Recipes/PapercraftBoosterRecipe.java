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
import net.tigereye.mods.battlecards.registration.BCItems;

public class PapercraftBoosterRecipe extends SpecialCraftingRecipe {

    public PapercraftBoosterRecipe(Identifier id, CraftingRecipeCategory craftingRecipeCategory) {
        super(id, craftingRecipeCategory);
    }

    public boolean matches(RecipeInputInventory craftingInventory, World world) {
        if (craftingInventory.getWidth() == 3 && craftingInventory.getHeight() == 3) {
            for(int i = 0; i < craftingInventory.getWidth(); ++i) {
                for(int j = 0; j < craftingInventory.getHeight(); ++j) {
                    ItemStack itemStack = craftingInventory.getStack(i + j * craftingInventory.getWidth());
                    Item item = itemStack.getItem();
                    if(i == 1 && j == 1 || i == 1 && j == 0 || i == 1 && j == 2){
                        if (item != Items.PAPER) {
                            return false;
                        }
                    }
                    else if((i == 0 && j == 1) || (i == 2 && j == 1)
                        || (i == 0 && j == 0) || (i == 0 && j == 2) || (i == 2 && j == 0) || (i == 2 && j == 2)){
                        if (item != Items.AIR) {
                            return false;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        ItemStack output = new ItemStack(BCItems.BOOSTER_PACK);
        output.getOrCreateNbt().putString(BoosterPackManager.ID_NBTKEY,"battlecards:passable_papercraft/booster_pack");
        return output;
    }

    public boolean fits(int width, int height) {
        return (width >= 3 && height >= 3);
    }

    public RecipeSerializer<?> getSerializer() {
        return BCItems.PAPERCRAFT_BOOSTER_RECIPE_SERIALIZER;
    }
}
