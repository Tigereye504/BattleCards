package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.RetainCardEffect;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardOwningItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//TODO: display dyecolor
public class BattlecardsDeckItem extends BattlecardBundleItem implements DyeableItem, CardOwningItem {

    public static final String DECK_DRAWPILE_NBTKEY = "deck_drawpile";
    public static final String HOTBAR_STORAGE_NBTKEY = "hotbar_storage";
    public static final String HOTBAR_POSITION_NBTKEY = "hotbar_position";
    public BattlecardsDeckItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (getBundleOccupancy(itemStack) > 0) {
            if (!itemStack.getOrCreateNbt().contains(CARD_OWNER_UUID_NBTKEY)) {
                activateDeck(user, itemStack);
            } else {
                deactivateDeck(user, itemStack);
            }
        }
        return TypedActionResult.consume(itemStack);
    }

    private void activateDeck(PlayerEntity user, ItemStack deck) {
        //become active with brief cooldown (to avoid deactivating by mistake)
        user.getItemCooldownManager().set(deck.getItem(),20);
        NbtCompound nbt = deck.getOrCreateNbt();
        UUID deck_UUID = getOrCreateUUID(deck);
        //stash items from hotbar
        stashHotbar(user,deck);
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
        //TODO: make this put all decks on cooldown (use a tag)
        user.getItemCooldownManager().set(deck.getItem(),200);
        //release items from hotbar storage
        releaseHotbar(user,deck);
    }

    private void releaseHotbar(PlayerEntity user, ItemStack deck) {
        PlayerInventory inventory = user.getInventory();
        NbtCompound nbt = deck.getOrCreateNbt();
        int oldDeckPosition = nbt.getInt(HOTBAR_POSITION_NBTKEY);
        int deckPosition = inventory.indexOf(deck);
        if(oldDeckPosition >= 0 && oldDeckPosition < 9 && deckPosition >= 0 && deckPosition < 9){
            inventory.removeStack(deckPosition);
        }
        if (!nbt.contains(HOTBAR_STORAGE_NBTKEY)) {
            nbt.put(HOTBAR_STORAGE_NBTKEY, new NbtList());
        }
        NbtList nbtList = nbt.getList(HOTBAR_STORAGE_NBTKEY, NbtElement.COMPOUND_TYPE);
        int i = 0;
        while(!nbtList.isEmpty()) {
            ItemStack item = ItemStack.fromNbt((NbtCompound) nbtList.remove(0));
            if (!item.isEmpty()) {
                if(inventory.getStack(i) != ItemStack.EMPTY){
                    user.dropItem(inventory.removeStack(i),true);
                }
                if(!inventory.insertStack(i,item)) {
                    user.dropItem(item, true);
                }
            }
            i++;
        }
        if(oldDeckPosition >= 0 && oldDeckPosition < 9 && deckPosition >= 0 && deckPosition < 9) {
            inventory.setStack(oldDeckPosition, deck);
        }
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
