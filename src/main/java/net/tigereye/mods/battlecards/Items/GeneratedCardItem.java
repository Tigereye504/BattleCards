package net.tigereye.mods.battlecards.Items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;

import java.util.List;

public class GeneratedCardItem extends Item implements BattleCardItem {


    public GeneratedCardItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 5;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        performBasicEffect(stack,user,world);
        if(user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(stack.getItem(), 10);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof PlayerEntity playerEntity){
            playerEntity.getItemCooldownManager().set(stack.getItem(),10);
        }
        return stack;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public boolean performBasicEffect(ItemStack stack, LivingEntity user, World world) {
        if(stack.hasNbt()){
            Identifier cardID = new Identifier(stack.getNbt().getString(CardManager.NBT_KEY));
            return CardManager.getEntry(cardID).performBasicEffect(user,stack);
        }
        return false;
    }

    @Override
    public boolean performChargeEffect(ItemStack stack, LivingEntity user, World world) {
        if(stack.hasNbt()){
            Identifier cardID = new Identifier(stack.getNbt().getString(CardManager.NBT_KEY));
            BattleCard card = CardManager.getEntry(cardID);
            if(payManaCost(user,stack,card.getChargeEffectCost())){
                card.performChargeEffect(user,stack);
                return true;
            }
        }
        return false;
    }

    @Override
    public Text getName(ItemStack stack) {
        if(stack.hasNbt()) {
            String cardID = stack.getNbt().getString(CardManager.NBT_KEY);
            if(cardID != null) {
                String[] splitID = cardID.split(":",2);
                if(splitID.length > 1) {
                    return Text.translatable("card." + splitID[0] + "." + splitID[1]);
                }
            }
        }
        return Text.translatable("card.battlecards.blank");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        Identifier cardID = new Identifier(itemStack.getNbt().getString(CardManager.NBT_KEY));
        BattleCard card = CardManager.getEntry(cardID);
        card.appendTooltip(itemStack,world,tooltip,tooltipContext);
    }
}
