package net.tigereye.mods.battlecards.BoosterPacks.Json;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackCardList;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackDropRate;

import java.util.Map;
import java.util.Set;

public class BoosterPackData {
    public Identifier id;
    public Map<Identifier, BoosterPackDropRate> sourceLootTables;
    public ItemStack scrapValue;
    public int commonCount;
    public int rareCount;
    public Set<Identifier> commonCards;
    public Set<Identifier> rareCards;

    public BoosterPackCardList getCardList(){
        BoosterPackCardList contents = new BoosterPackCardList();
        contents.commonCount = commonCount;
        contents.rareCount = rareCount;
        contents.commonCards = commonCards;
        contents.rareCards = rareCards;
        return contents;
    }
}
