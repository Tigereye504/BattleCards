package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardSleeve;

public abstract class CardSleeveItem extends Item implements CardSleeve {

    public CardSleeveItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack sleeveItemStack = user.getStackInHand(hand);
        ItemStack cardItemStack = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        if(cardItemStack.getItem() instanceof BattleCardItem bci){
            if(CardOwningItem.isCardOwned(cardItemStack)){
                //this prevents spending sleeves on owned (and thus temporary) cards.
                return TypedActionResult.pass(sleeveItemStack);
            }
            int sleevesToApply = Math.min(sleeveItemStack.getCount(), cardItemStack.getCount());
            ItemStack oldSleeves = BattleCardItem.getSleeve(cardItemStack);
            if(oldSleeves != ItemStack.EMPTY){
                oldSleeves.setCount(sleevesToApply);
                if(!user.giveItemStack(oldSleeves)){
                    user.dropItem(oldSleeves,true);
                }
            }
            ItemStack freshlySleevedCards = cardItemStack.copyWithCount(sleevesToApply);
            bci.setSleeve(freshlySleevedCards,sleeveItemStack.copyWithCount(1));
            if(!user.giveItemStack(freshlySleevedCards)){
                user.dropItem(freshlySleevedCards,true);
            }
            cardItemStack.decrement(sleevesToApply);
            sleeveItemStack.decrement(sleevesToApply);
            return TypedActionResult.consume(sleeveItemStack);
        }
        return TypedActionResult.pass(sleeveItemStack);
    }
}
