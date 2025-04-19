package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;

public class CardSleeveItem extends Item implements CardSleeve{

    public CardSleeveItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack sleeveItemStack = user.getStackInHand(hand);
        ItemStack otherItemStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        if(otherItemStack.getItem() instanceof BattleCardItem bci){
            if(otherItemStack.hasNbt() && otherItemStack.getNbt().containsUuid(CardOwningItem.CARD_OWNER_UUID_NBTKEY)){
                //this prevents spending sleeves on owned (and thus temporary) cards.
                return TypedActionResult.pass(sleeveItemStack);
            }
            int sleevesToApply = Math.min(sleeveItemStack.getCount(),otherItemStack.getCount());
            ItemStack oldSleeves = bci.getSleeve(otherItemStack);
            if(oldSleeves != ItemStack.EMPTY){
                oldSleeves.setCount(sleevesToApply);
                if(!user.getInventory().insertStack(oldSleeves)){
                    user.dropItem(oldSleeves,true);
                }
            }
            ItemStack fleshlySleevedCards = otherItemStack.copyWithCount(sleevesToApply);
            bci.setSleeve(fleshlySleevedCards,sleeveItemStack);
            otherItemStack.decrement(sleevesToApply);
            sleeveItemStack.decrement(sleevesToApply);
            if(!user.getInventory().insertStack(fleshlySleevedCards)){
                user.dropItem(fleshlySleevedCards,true);
            }
            return TypedActionResult.consume(sleeveItemStack);
        }
        return TypedActionResult.pass(sleeveItemStack);
    }
}
