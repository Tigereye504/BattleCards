package net.tigereye.mods.battlecards.Items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.RetainCardEffect;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;

import java.util.List;

public class GeneratedCardItem extends Item implements BattleCardItem {

    public static final int UNOWNED_COOLDOWN = 100;
    public static final int OWNED_COOLDOWN = 10;

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
        performQuickEffect(stack,user,world);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        performChargeEffect(stack, user, world);
        return stack;
    }

    private void afterCardEffects(ItemStack stack, World world, LivingEntity user) {
        //if owned, call the owner's afterOwnedCardPlayed function.
        if(user instanceof PlayerEntity pEntity) {
            ItemStack owner = CardOwningItem.findOwningItemInvSlot(stack, pEntity);
            if(owner != null){
                ((CardOwningItem)owner.getItem()).afterOwnedCardPlayed(world,pEntity,owner,stack);
                pEntity.getItemCooldownManager().set(stack.getItem(),OWNED_COOLDOWN);
            }
            else{
                pEntity.getItemCooldownManager().set(stack.getItem(),UNOWNED_COOLDOWN);
            }
        }
        //remove lingering 'retain' tag'. Consider moving this to a general 'aftercardplay' event
        stack.removeSubNbt(RetainCardEffect.RETAIN_NBTKEY);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(itemStack.hasNbt()){
            NbtCompound nbt = itemStack.getNbt();
            if(CardOwningItem.isCardOwned(itemStack)){
                if(CardOwningItem.findOwningItemInvSlot(itemStack,user) == null){
                    itemStack.setCount(0);
                    return TypedActionResult.consume(itemStack);
                }
            }
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public boolean performQuickEffect(ItemStack stack, LivingEntity user, World world) {
        if(stack.hasNbt()){
            Identifier cardID = new Identifier(stack.getNbt().getString(CardManager.ID_KEY));
            CardManager.getEntry(cardID).performQuickEffect(user,stack);
            afterCardEffects(stack, world, user);
            return true;
        }
        return false;
    }

    @Override
    public boolean performChargeEffect(ItemStack stack, LivingEntity user, World world) {
        if(stack.hasNbt()){
            Identifier cardID = new Identifier(stack.getNbt().getString(CardManager.ID_KEY));
            BattleCard card = CardManager.getEntry(cardID);
            if(payManaCost(user,stack,getChargeEffectCost(user,stack))){
                card.performChargeEffect(user,stack);
                afterCardEffects(stack, world, user);
                return true;
            }
        }
        return false;
    }

    @Override
    public void gainMana(Entity user, ItemStack item, int amount){
        //TODO: check for deck in user's inventory
        //if found, forward this to the deck
        //else, use the default method
        BattleCardItem.super.gainMana(user,item,amount);
    }

    @Override
    public int getCurrentMana(Entity user, ItemStack item){
        //TODO: check for owning deck in user's inventory
        //if found, forward this to the deck
        //else, use the default method
        return BattleCardItem.super.getCurrentMana(user,item);
    }

    @Override
    public int getChargeEffectCost(Entity user, ItemStack item){
        //if has owner, proceed as normal
        //else, double the cost
        if(item.hasNbt()) {
            Identifier cardID = new Identifier(item.getNbt().getString(CardManager.ID_KEY));
            BattleCard card = CardManager.getEntry(cardID);
            return item.getNbt().containsUuid(CardOwningItem.CARD_OWNER_UUID_NBTKEY) ? card.getChargeEffectCost() : card.getChargeEffectCost()*2;
        }
        return 0;
    }

    @Override
    public boolean payManaCost(Entity user, ItemStack item, int cost){
        //TODO: check for deck in user's inventory
        //if found, forward this to the deck
        //else, use the default method
        return BattleCardItem.super.payManaCost(user,item,cost);
    }

    @Override
    public Text getName(ItemStack stack) {
        if(stack.hasNbt()) {
            String cardID = stack.getNbt().getString(CardManager.ID_KEY);
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
        ItemStack sleeve = BattleCardItem.getSleeve(itemStack);
        sleeve.getItem().appendTooltip(sleeve,world,tooltip,tooltipContext);

        Identifier cardID;
        if(itemStack.hasNbt()) {
            cardID = new Identifier(itemStack.getNbt().getString(CardManager.ID_KEY));
        }
        else{
            cardID = null;
        }
        BattleCard card = CardManager.getEntry(cardID);
        card.appendTooltip(itemStack,world,tooltip,tooltipContext);
    }
}
