package net.tigereye.mods.battlecards.PrebuiltDecks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackCardList;
import net.tigereye.mods.battlecards.BoosterPacks.DropRateData;

import java.util.Map;
import java.util.Set;

public class PrebuiltDeckData {
    public String id;
    public Map<String, DropRateData> sourceLootTables;
    public ItemStack deck;
}
