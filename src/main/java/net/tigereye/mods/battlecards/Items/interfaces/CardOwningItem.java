package net.tigereye.mods.battlecards.Items.interfaces;

import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.BiConsumer;

public interface CardOwningItem {
    public static final String CARD_OWNER_UUID_NBTKEY = "card_owner_uuid";
    boolean isOwnerOfCard(ItemStack owner, ItemStack card);
    void afterOwnedCardPlayed(World world, PlayerEntity user, ItemStack cardOwningItem, ItemStack card);

    public static boolean findOwningItem(ItemStack card, PlayerEntity entity, BiConsumer<CardOwningItem,ItemStack> consumer){
        NbtCompound nbt = card.getNbt();
        if(nbt != null && nbt.containsUuid(CARD_OWNER_UUID_NBTKEY)){
            UUID cardUUID = nbt.getUuid(CARD_OWNER_UUID_NBTKEY);
            PlayerInventory inv = entity.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                ItemStack invItem = inv.getStack(i);
                if(invItem.getItem() instanceof CardOwningItem coi && coi.isOwnerOfCard(invItem,card)){
                    consumer.accept(coi,invItem);
                    return true;
                }
            }
        }
        return false;
    }
}
