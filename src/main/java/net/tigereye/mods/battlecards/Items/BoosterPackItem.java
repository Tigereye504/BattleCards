package net.tigereye.mods.battlecards.Items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;

import java.util.List;

public class BoosterPackItem extends Item {


    public BoosterPackItem(Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if(stack.hasNbt()) {
            String boosterPackID = stack.getNbt().getString(BoosterPackManager.ID_NBTKEY);
            if(boosterPackID != null) {
                String[] splitID = boosterPackID.split(":",2);
                if(splitID.length > 1) {
                    return Text.translatable("boosterpack." + splitID[0] + "." + splitID[1]);
                }
            }
        }
        return Text.translatable("boosterpack.battlecards.blank");
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        //get id from nbt
        if(stack.hasNbt()) {
            String boosterPackIDString = stack.getNbt().getString(BoosterPackManager.ID_NBTKEY);
            if(boosterPackIDString != null) {
                Identifier boosterPackId = new Identifier(boosterPackIDString);
                //TODO: use ID to lookup loot table
                if (world instanceof ServerWorld sWorld){
                    LootContextParameterSet.Builder LCPSBuilder = new LootContextParameterSet.Builder(sWorld)
                            .add(LootContextParameters.ORIGIN, user.getPos());
                    LootTable lootTable = sWorld.getServer().getLootManager().getLootTable(boosterPackId);
                    //TODO: generate loot
                    LootContextParameterSet LCPS = LCPSBuilder.build(LootContextTypes.CHEST);
                    List<ItemStack> list = lootTable.generateLoot(LCPS);
                    //TODO: drop loot into inventory, or on ground if can't
                    PlayerInventory playerInventory = user.getInventory();
                    for (ItemStack itemStack:
                            list) {
                        if(!playerInventory.insertStack(itemStack)){
                            user.dropItem(itemStack,true,true);
                        }
                    }
                }
            }
        }
        stack.decrement(1);
        return TypedActionResult.consume(stack);
    }
}
