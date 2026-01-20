package net.tigereye.mods.battlecards.BoosterPacks.Json;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackCardList;
import net.tigereye.mods.battlecards.BoosterPacks.BoosterPackDropRates;

import java.util.Set;

public class BoosterPackData {
    public Identifier id;
    public Set<Identifier> mobs;
    public ItemStack scrapValue;
    public float dropRate;
    public float dropRateLootingFactor;
    public int commonCount;
    public int rareCount;
    public Set<Identifier> commonCards;
    public Set<Identifier> rareCards;

    public BoosterPackDropRates getDropRates(){
        BoosterPackDropRates dropRates = new BoosterPackDropRates();
        dropRates.id = id;
        dropRates.dropRate = dropRate;
        dropRates.dropRateLootingFactor = dropRateLootingFactor;
        return dropRates;
    }

    public BoosterPackCardList getCardList(){
        BoosterPackCardList contents = new BoosterPackCardList();
        contents.commonCount = commonCount;
        contents.rareCount = rareCount;
        contents.commonCards = commonCards;
        contents.rareCards = rareCards;
        return contents;
    }
}
