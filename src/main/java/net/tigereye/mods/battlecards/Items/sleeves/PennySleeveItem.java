package net.tigereye.mods.battlecards.Items.sleeves;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;

import java.util.List;

public class PennySleeveItem extends Item{

    public PennySleeveItem(Settings settings) {
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
            ItemStack oldSleeves = bci.getSleeve(cardItemStack);
            if(oldSleeves != ItemStack.EMPTY){
                if(!user.giveItemStack(oldSleeves)){
                    user.dropItem(oldSleeves,true);
                }
            }
            bci.clearSleeve(cardItemStack);
            return TypedActionResult.consume(sleeveItemStack);
        }
        return TypedActionResult.pass(sleeveItemStack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.battlecards.sleeve.penny.tooltip"));
    }
}
