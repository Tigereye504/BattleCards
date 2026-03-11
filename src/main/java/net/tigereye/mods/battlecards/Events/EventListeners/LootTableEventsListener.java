package net.tigereye.mods.battlecards.Events.EventListeners;

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
import net.tigereye.mods.battlecards.BoosterPacks.DropRateData;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.PrebuiltDecks.PrebuiltDeckManager;
import net.tigereye.mods.battlecards.registration.BCItems;

import java.util.List;

public class LootTableEventsListener {

    public static void injectBoosterPacks(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder supplier, LootTableSource setter){
        if(BoosterPackManager.lootTableInjections.isEmpty()){
            BoosterPackManager.INSTANCE.reload(resourceManager);
        }
        List<DropRateData> packDropRates = BoosterPackManager.lootTableInjections.get(id.toString());
        if(packDropRates != null) {
            for (DropRateData data : packDropRates) {
                NbtCompound nbt = new NbtCompound();
                nbt.putString(BoosterPackManager.ID_NBTKEY, data.id);
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(data.rolls))
                        .with(ItemEntry.builder(BCItems.BOOSTER_PACK)
                                .conditionally(RandomChanceWithLootingLootCondition.builder(data.rate, data.lootingRate))
                                .apply(SetNbtLootFunction.builder(nbt)));
                supplier.pool(poolBuilder);
            }
        }
    }

    public static void injectPrebuiltDecks(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder supplier, LootTableSource setter){
        if(PrebuiltDeckManager.lootTableInjections.isEmpty()){
            PrebuiltDeckManager.INSTANCE.reload(resourceManager);
        }
        List<DropRateData> packDropRates = PrebuiltDeckManager.lootTableInjections.get(id.toString());
        if(packDropRates != null) {
            for (DropRateData data : packDropRates) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(data.rolls))
                        .with(ItemEntry.builder(BCItems.DECK)
                                .conditionally(RandomChanceWithLootingLootCondition.builder(data.rate, data.lootingRate))
                                .apply(SetNbtLootFunction.builder(PrebuiltDeckManager.prebuiltDecks.get(data.id).getNbt())));
                supplier.pool(poolBuilder);
            }
        }
    }
}
