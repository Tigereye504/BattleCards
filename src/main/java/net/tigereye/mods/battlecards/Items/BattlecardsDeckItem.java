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

    private static final String DECK_ACTIVE_NBTKEY = "deck_active";
    public static final String DECK_DRAWPILE_NBTKEY = "deck_drawpile";
    public BattlecardsDeckItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);
        if(getBundleOccupancy(itemStack) > 0) {
            if (!itemStack.getOrCreateNbt().contains(DECK_ACTIVE_NBTKEY)) {
                activateDeck(world, user, itemStack);
            }
            else {
                deactivateDeck(world, user, itemStack);
            }
        }
        return TypedActionResult.consume(itemStack);
    }

    private void activateDeck(World world, PlayerEntity user, ItemStack itemStack) {
        //become active with brief cooldown (to avoid deactivating by mistake)
        user.getItemCooldownManager().set(itemStack.getItem(),20);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putBoolean(DECK_ACTIVE_NBTKEY,true);
        UUID deck_UUID;
        if(nbt.containsUuid(CARD_OWNER_UUID_NBTKEY)){
            deck_UUID = nbt.getUuid(CARD_OWNER_UUID_NBTKEY);
        }
        else{
            deck_UUID = UUID.randomUUID();
            nbt.putUuid(CARD_OWNER_UUID_NBTKEY,deck_UUID);
        }
        //duplicate the inventory into the draw pile
        List<ItemStack> drawPile = new ArrayList<>();
        getBundledStacks(itemStack).forEach((card) -> {
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
        while(drawCardToHotbar(world, user, itemStack));
    }

    private void deactivateDeck(World world, PlayerEntity user, ItemStack deck) {
        NbtCompound nbt = deck.getOrCreateNbt();
        nbt.remove(DECK_ACTIVE_NBTKEY);
        //destroy all deck-owned BattleCardItems
        PlayerInventory inv = user.getInventory();
        UUID deckUUID = nbt.getUuid(CARD_OWNER_UUID_NBTKEY);
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
        user.getItemCooldownManager().set(deck.getItem(),600);
    }

    public boolean drawCardToHotbar(World world, PlayerEntity user, ItemStack deck){
        //find an empty slot. return false if cant
        PlayerInventory inv = user.getInventory();
        int emptySlot = inv.getEmptySlot();
        if(emptySlot >= 9){
            return false;
        }
        //drawCard
        ItemStack card = drawCard(deck);
        //if draw card returned null, return false.
        // also, check if any owned cards are left on the hotbar. If not, deactivate the deck.
        if(card == null){
            boolean deactivate = true;
            for (int i = 0; i < 9; i++) {
                ItemStack item = inv.getStack(i);
                UUID deckUUID = deck.getOrCreateNbt().getUuid(CARD_OWNER_UUID_NBTKEY);
                if(item.getItem() instanceof BattleCardItem) {
                    NbtCompound nbt = inv.getStack(i).getNbt();
                    if (nbt != null && nbt.getUuid(CARD_OWNER_UUID_NBTKEY).compareTo(deckUUID) == 0){
                        deactivate = false;
                        break;
                    }
                }
            }
            if(deactivate){
                deactivateDeck(world, user, deck);
            }
            return false;
        }

        //place card on hotbar
        //return true;
        inv.insertStack(emptySlot,card);
        return true;
    }

    public ItemStack drawCard(ItemStack deck){
        //if the draw pile isn't empty:
        //pop top card of draw pile
        //return card
        //if the draw pile is empty, return null
        ItemStack card = popDrawPile(deck.getOrCreateNbt());
        if(card != null){
            return card;
        }
        return null;
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
            drawCardToHotbar(world, user, deck);
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

    /*********Deck Management**********/
    //TODO: block the add/remove card functions while active
}
