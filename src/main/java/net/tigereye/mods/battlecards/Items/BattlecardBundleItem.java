package net.tigereye.mods.battlecards.Items;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

//TODO: display dyecolor
public class BattlecardBundleItem extends Item{
    
    public BattlecardBundleItem(Settings settings) {
        super(settings);
    }

    /*********Deckbuilding Functions**********/

    @Override
    public boolean onStackClicked(ItemStack bundleItemStack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack cardStack = slot.getStack();
            if (cardStack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeFirstCard(bundleItemStack).ifPresent(removedStack -> addToBundle(bundleItemStack, slot.insertStack(removedStack)));
            } else if (cardStack.getItem() instanceof BattleCardItem) {
                int j = addToBundle(bundleItemStack, slot.takeStackRange(cardStack.getCount(), 64, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            if (otherStack.isEmpty()) {
                removeFirstCard(stack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set(itemStack);
                });
            } else {
                int i = addToBundle(stack, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.decrement(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BattleCardItem) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }
            int k = stack.getCount();
            if (k == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = (NbtCompound)optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    itemStack.increment(k);
                    itemStack.writeNbt(nbtCompound2);
                    nbtList.remove(nbtCompound2);
                    nbtList.add(0, nbtCompound2);
                } else {
                    ItemStack itemStack2 = stack.copyWithCount(k);
                    NbtCompound nbtCompound3 = new NbtCompound();
                    itemStack2.writeNbt(nbtCompound3);
                    nbtList.add(0, nbtCompound3);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    protected static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        return stack.isOf(Items.BUNDLE)
                ? Optional.empty()
                : items.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack))
                .findFirst();
    }

    protected static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(ItemStack::getCount).sum();
    }

    protected static Optional<ItemStack> removeFirstCard(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack cardStack = ItemStack.fromNbt(nbtCompound2);
                //ItemStack singleCard = cardStack.copyWithCount(1);
                //if(cardStack.getCount() == 1) {
                    nbtList.remove(0);
                //}
                //else{
                //    cardStack.decrement(1);
                //    nbtList.setElement(0,cardStack.writeNbt(new NbtCompound()));
                //}
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                //return Optional.of(singleCard);
                return Optional.of(cardStack);
            }
        }
    }

    protected static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        getBundledStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, getBundleOccupancy(stack)));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlecards.deck.decksize", getBundleOccupancy(stack)).formatted(Formatting.GRAY));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    protected void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    protected void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
}
