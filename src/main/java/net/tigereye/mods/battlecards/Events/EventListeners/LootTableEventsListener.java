package net.tigereye.mods.battlecards.Events.EventListeners;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackData;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.registration.BCItems;

public class LootTableEventsListener {

    public static void injectBoosterPacks(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder supplier, LootTableSource setter){
        if(BoosterPackManager.LootTableInjections.isEmpty()){
            BoosterPackManager.INSTANCE.reload(resourceManager);
        }
        for(Identifier targetedLootPool : BoosterPackManager.LootTableInjections.keySet()){
            if (targetedLootPool.equals(id)) {
                for(BoosterPackData data : BoosterPackManager.LootTableInjections.get(targetedLootPool)) {
                    NbtCompound nbt = new NbtCompound();
                    nbt.putString(BoosterPackManager.ID_NBTKEY,data.id.toString());
                    LootPool.Builder poolBuilder = LootPool.builder()
                            .conditionally(RandomChanceWithLootingLootCondition.builder(data.dropRate, data.dropRateLootingFactor))
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(BCItems.BOOSTER_PACK)
                                    .apply(SetNbtLootFunction.builder(nbt)));

                    supplier.pool(poolBuilder);
                }
            }
        }
    }
}
