package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

import java.util.List;

public class CardfettiItem extends Item {


    public CardfettiItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        int currentTier = user.hasStatusEffect(BCStatusEffects.CARDFETTI_SACRIFICE) ?
                user.getStatusEffect(BCStatusEffects.CARDFETTI_SACRIFICE).getAmplifier() + 1 : 0;
        int modifiedCount = (currentTier*currentTier)+stack.getCount();
        int newTier = (int) Math.floor(Math.sqrt(modifiedCount));
        int leftoverCount = modifiedCount-(newTier*newTier);
        if(user.getRandom().nextInt(((newTier+1)*(newTier+1))-(newTier*newTier)) < leftoverCount){
            newTier++;
        }
        //TODO: make cardfetti duration configurable
        user.addStatusEffect(new StatusEffectInstance(BCStatusEffects.CARDFETTI_SACRIFICE,6000,newTier-1));
        //TODO: pollute area with cardfetti particles
        stack.setCount(0);
        return TypedActionResult.consume(stack);
    }
}
