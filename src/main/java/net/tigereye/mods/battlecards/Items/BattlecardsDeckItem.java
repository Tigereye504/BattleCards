package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.RetainCardEffect;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//TODO: display dyecolor
public class BattlecardsDeckItem extends BattlecardBundleItem implements DyeableItem, CardOwningItem {

    public static final String DECK_DRAWPILE_NBTKEY = "deck_drawpile";
    public static final String HOTBAR_STORAGE_NBTKEY = "hotbar_storage";
    public static final String MANA_STORAGE_NBTKEY = "mana_storage";
    public static final String HOTBAR_POSITION_NBTKEY = "hotbar_position";
    public BattlecardsDeckItem(Settings settings) {
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

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if(user instanceof PlayerEntity pEntity) {
            performQuickEffect(pEntity, stack);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(user instanceof PlayerEntity pEntity) {
            performChargeEffect(pEntity, stack);
        }
        return stack;
    }

    private void performQuickEffect(PlayerEntity user, ItemStack itemStack) {
        //there are three states a deck can be in; Active, Inactive, and Paused
        //When Inactive, a quick use will store the hotbar, draw a new hand, make the deck Active (activateDeck)
        //When Active, a quick use will store the current hand, and retrieve the hotbar, make the deck Paused (pauseDeck)
        //When Paused, a quick use will store the hotbar, retrieve the stored hand, and resume play (resumeDeck)
        if (getBundleOccupancy(itemStack) > 0) {
            //if the deck lacks an owner UUID, it is inactive
            if (!itemStack.getOrCreateNbt().contains(CARD_OWNER_UUID_NBTKEY)) {
                activateDeck(user, itemStack);
            }
            //if it has a stored hotbar, it is active
            else if(itemStack.getOrCreateNbt().contains(HOTBAR_STORAGE_NBTKEY)){
                pauseDeck(user, itemStack);
            }
            //if it has a UUID but not a hotbar, it is paused
            else {
                resumeDeck(user, itemStack);
            }
        }
    }

    private void performChargeEffect(PlayerEntity user, ItemStack itemStack) {
        if (getBundleOccupancy(itemStack) > 0) {
            if (!itemStack.getOrCreateNbt().contains(CARD_OWNER_UUID_NBTKEY)) {
                activateDeck(user, itemStack);
            } else {
                deactivateDeck(user, itemStack);
            }
        }
    }

    private void activateDeck(PlayerEntity user, ItemStack deck) {
        //become active with brief cooldown (to avoid deactivating by mistake)
        user.getItemCooldownManager().set(deck.getItem(),20);
        NbtCompound nbt = deck.getOrCreateNbt();
        UUID deck_UUID = getOrCreateUUID(deck);
        //stash items from hotbar
        stashHotbar(user,deck);
        //swap current mana supply with deck's
        swapMana(user,deck);
        //duplicate the inventory into the draw pile
        List<ItemStack> drawPile = new ArrayList<>();
        getBundledStacks(deck).forEach((card) -> {
            ItemStack tempCard = card.copy();
            tempCard.setCount(1);
            tempCard.getOrCreateNbt().putUuid(CARD_OWNER_UUID_NBTKEY,deck_UUID);
            //TODO: for each card in the draw pile, check for onOpenDeck effects (like leather card sleeves)
            for (int i = 0; i < card.getCount(); i++) {
                drawPile.add(tempCard);
            }
        });
        //shuffle the draw pile
        Collections.shuffle(drawPile);
        //save the draw pile
        pushDrawPile(nbt,drawPile);
        //for each empty slot in the hotbar, draw a card
        //I do not like how inobvious this line is, but to be clear:
        //until drawCardToHotbar returns false, meaning it didn't find an open spot to put a new card or has no more cards to draw,
        //repeat drawCardToHotbar
        while(drawCardToHotbar(user, deck));
    }

    private void stashHotbar(PlayerEntity user, ItemStack deck) {
        NbtCompound nbt = deck.getOrCreateNbt();
        if (!nbt.contains(HOTBAR_STORAGE_NBTKEY)) {
            nbt.put(HOTBAR_STORAGE_NBTKEY, new NbtList());
        }
        NbtList nbtList = nbt.getList(HOTBAR_STORAGE_NBTKEY, NbtElement.COMPOUND_TYPE);
        PlayerInventory inventory = user.getInventory();
        int hotbarPosition = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.removeStack(i);
            if(item == deck) {
                hotbarPosition = i;
                item = ItemStack.EMPTY;
            }
            NbtCompound itemNBT = new NbtCompound();
            item.writeNbt(itemNBT);
            nbtList.add(i, itemNBT);
        }
        nbt.putInt(HOTBAR_POSITION_NBTKEY,hotbarPosition);
        if(hotbarPosition != -1) {
            inventory.setStack(8, deck);
        }
    }

    private void swapMana(PlayerEntity user, ItemStack deck){
        NbtCompound nbt = deck.getOrCreateNbt();
        int curMana = user.hasStatusEffect(BCStatusEffects.MANA) ?
                user.getStatusEffect(BCStatusEffects.MANA).getAmplifier()+1 : 0;
        int storedMana = nbt.getInt(MANA_STORAGE_NBTKEY);
        user.removeStatusEffect(BCStatusEffects.MANA);
        if(curMana > 0) {
            nbt.putInt(MANA_STORAGE_NBTKEY, curMana);
        }
        else{
            nbt.remove(MANA_STORAGE_NBTKEY);
        }
        if(storedMana > 0) {
            user.addStatusEffect(BCStatusEffect.buildGradualFalloffStatusEffectInstance(BCStatusEffects.MANA,
                    600, 200, storedMana - 1, false, false, false));
        }
    }

    private void deactivateDeck(PlayerEntity user, ItemStack deck) {
        NbtCompound nbt = deck.getOrCreateNbt();
        //destroy all deck-owned BattleCardItems
        PlayerInventory inv = user.getInventory();
        UUID deckUUID = getOrCreateUUID(deck);
        nbt.remove(CARD_OWNER_UUID_NBTKEY);
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if(stack != deck && stack.getItem() instanceof BattleCardItem){
                NbtCompound cardNbt = stack.getNbt();
                if(cardNbt != null && cardNbt.contains(CARD_OWNER_UUID_NBTKEY) && cardNbt.getUuid(CARD_OWNER_UUID_NBTKEY).compareTo(deckUUID) == 0){
                    inv.removeStack(i);
                }
            }
        }
        // clear the draw pile
        nbt.remove(DECK_DRAWPILE_NBTKEY);
        //go on a long cooldown
        user.getItemCooldownManager().set(deck.getItem(),200);
        //release items from hotbar storage and swap mana, if active rather than paused
        if(nbt.contains(HOTBAR_STORAGE_NBTKEY)) {
            swapMana(user,deck);
            releaseHotbar(user, deck);
        }
        //if paused rather than active, just forfeit the stored mana
        else{
            nbt.remove(MANA_STORAGE_NBTKEY);
        }
    }

    private void releaseHotbar(PlayerEntity user, ItemStack deck) {
        PlayerInventory inventory = user.getInventory();
        NbtCompound nbt = deck.getOrCreateNbt();
        int oldDeckPosition = nbt.getInt(HOTBAR_POSITION_NBTKEY);
        int deckPosition = inventory.indexOf(deck);
        if(oldDeckPosition >= 0 && oldDeckPosition < 9 && deckPosition >= 0 && deckPosition < 9){
            inventory.removeStack(deckPosition);
        }
        if (nbt.contains(HOTBAR_STORAGE_NBTKEY)) {
            NbtList nbtList = nbt.getList(HOTBAR_STORAGE_NBTKEY, NbtElement.COMPOUND_TYPE);
            int i = 0;
            while (!nbtList.isEmpty()) {
                ItemStack item = ItemStack.fromNbt((NbtCompound) nbtList.remove(0));
                if (!item.isEmpty()) {
                    if (inventory.getStack(i) != ItemStack.EMPTY) {
                        user.dropItem(inventory.removeStack(i), true);
                    }
                    if (!inventory.insertStack(i, item)) {
                        user.dropItem(item, true);
                    }
                }
                i++;
            }
            nbt.remove(HOTBAR_STORAGE_NBTKEY);
        }
        if(oldDeckPosition >= 0 && oldDeckPosition < 9 && deckPosition >= 0 && deckPosition < 9) {
            if (inventory.getStack(oldDeckPosition) != ItemStack.EMPTY) {
                user.dropItem(inventory.removeStack(oldDeckPosition), true);
            }
            inventory.setStack(oldDeckPosition, deck);
        }
    }

    private void pauseDeck(PlayerEntity user, ItemStack deck) {
        //to pause a deck, we must find our owned cards and put them back on top of the deck to redraw
        NbtCompound nbt = deck.getOrCreateNbt();
        PlayerInventory inv = user.getInventory();
        UUID deckUUID = getOrCreateUUID(deck);
        for (int i = inv.size()-1; i >= 0; i--) {
            ItemStack stack = inv.getStack(i);
            if(stack != deck && stack.getItem() instanceof BattleCardItem){
                NbtCompound cardNbt = stack.getNbt();
                if(cardNbt != null && cardNbt.contains(CARD_OWNER_UUID_NBTKEY) && cardNbt.getUuid(CARD_OWNER_UUID_NBTKEY).compareTo(deckUUID) == 0){
                    inv.removeStack(i);
                    returnCard(nbt,stack);
                }
            }
        }
        //once we have done so, we can releaseHotbar, finishing the transition to a Paused deck
        releaseHotbar(user,deck);
        //swap current mana supply with deck's
        swapMana(user,deck);
    }

    private void resumeDeck(PlayerEntity user, ItemStack deck) {
        //to resume a deck, we must stash the hotbar
        stashHotbar(user,deck);
        swapMana(user,deck);
        //once we have done so, we can redraw our hand
        while(drawCardToHotbar(user,deck));
    }

    private UUID getOrCreateUUID(ItemStack deck){
        NbtCompound nbt = deck.getOrCreateNbt();
        UUID deck_UUID;
        if(nbt.containsUuid(CARD_OWNER_UUID_NBTKEY)){
            deck_UUID = nbt.getUuid(CARD_OWNER_UUID_NBTKEY);
        }
        else{
            deck_UUID = UUID.randomUUID();
            nbt.putUuid(CARD_OWNER_UUID_NBTKEY,deck_UUID);
        }
        return deck_UUID;
    }

    public boolean drawCardToHotbar(PlayerEntity user, ItemStack deck){
        //find an empty slot. return false if cant
        PlayerInventory inv = user.getInventory();
        int emptySlot = inv.getEmptySlot();
        if(emptySlot == -1 || emptySlot >= 9){
            return false;
        }
        //drawCard
        ItemStack card = drawCard(deck);
        UUID deckUUID = getOrCreateUUID(deck);
        //if draw card returned null, return false.
        // also, check if any owned cards are left on the hotbar. If not, deactivate the deck.
        if(card == null){
            boolean deactivate = true;
            for (int i = 0; i < 9; i++) {
                ItemStack item = inv.getStack(i);
                if(item.getItem() instanceof BattleCardItem) {
                    NbtCompound nbt = inv.getStack(i).getNbt();
                    if (nbt != null && nbt.containsUuid(CARD_OWNER_UUID_NBTKEY) && nbt.getUuid(CARD_OWNER_UUID_NBTKEY).compareTo(deckUUID) == 0){
                        deactivate = false;
                        break;
                    }
                }
            }
            if(deactivate){
                deactivateDeck(user, deck);
            }
            return false;
        }

        //place card on hotbar
        //return true;
        inv.insertStack(emptySlot,card);
        return true;
    }

    public ItemStack drawCard(ItemStack deck){
        return popDrawPile(deck.getOrCreateNbt());
    }

    public void afterOwnedCardPlayed(World world, PlayerEntity user, ItemStack deck, ItemStack card){
        //If the card has the retain flag, remove the flag.
        if(card.hasNbt() && card.getNbt().contains(RetainCardEffect.RETAIN_NBTKEY)){
            card.removeSubNbt(RetainCardEffect.RETAIN_NBTKEY);
        }
        else{
            //else, destroy played card.
            user.getInventory().removeOne(card);
            //draw a new card to hotbar.
            drawCardToHotbar(user, deck);
        }
    }

    protected void pushDrawPile(NbtCompound nbt, List<ItemStack> cards){
        if (!nbt.contains(DECK_DRAWPILE_NBTKEY)) {
            nbt.put(DECK_DRAWPILE_NBTKEY, new NbtList());
        }
        NbtList nbtList = nbt.getList(DECK_DRAWPILE_NBTKEY, NbtElement.COMPOUND_TYPE);
        for(ItemStack card : cards){
            NbtCompound itemNBT = new NbtCompound();
            card.writeNbt(itemNBT);
            nbtList.add(0, itemNBT);
        }

    }

    protected void pushDrawPile(NbtCompound nbt, ItemStack card){
        if (!nbt.contains(DECK_DRAWPILE_NBTKEY)) {
            nbt.put(DECK_DRAWPILE_NBTKEY, new NbtList());
        }
        NbtList nbtList = nbt.getList(DECK_DRAWPILE_NBTKEY, NbtElement.COMPOUND_TYPE);
        NbtCompound itemNBT = new NbtCompound();
        card.writeNbt(itemNBT);
        nbtList.add(0, itemNBT);
    }

    protected void returnCard(NbtCompound nbt, ItemStack card){
        if (!nbt.contains(DECK_DRAWPILE_NBTKEY)) {
            nbt.put(DECK_DRAWPILE_NBTKEY, new NbtList());
        }
        NbtList nbtList = nbt.getList(DECK_DRAWPILE_NBTKEY, NbtElement.COMPOUND_TYPE);
        NbtCompound itemNBT = new NbtCompound();
        card.writeNbt(itemNBT);
        nbtList.add(itemNBT);
    }

    protected ItemStack popDrawPile(NbtCompound nbt){
        NbtList nbtList = nbt.getList(DECK_DRAWPILE_NBTKEY, NbtElement.COMPOUND_TYPE);
        if(nbtList.isEmpty()) {
            return null;
        }
        return ItemStack.fromNbt((NbtCompound) nbtList.remove(nbtList.size()-1));
    }

    @Override
    public boolean isOwnerOfCard(ItemStack owner, ItemStack card) {
        if(!(card.getItem() instanceof BattleCardItem)){
            return false;
        }
        NbtCompound ownerNbt = owner.getNbt();
        NbtCompound cardNbt = card.getNbt();
        if(cardNbt != null && ownerNbt != null && cardNbt.contains(CARD_OWNER_UUID_NBTKEY) && ownerNbt.contains(CARD_OWNER_UUID_NBTKEY)){
            return cardNbt.getUuid(CARD_OWNER_UUID_NBTKEY).compareTo(ownerNbt.getUuid(CARD_OWNER_UUID_NBTKEY)) == 0;
        }
        return false;
    }

    //TODO: block the add/remove card functions while active
}
