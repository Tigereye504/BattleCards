package net.tigereye.mods.battlecards.BoosterPacks;

import net.minecraft.util.Identifier;

import java.util.Set;

public class BoosterPackCardList {
    public int commonCount;
    public int rareCount;
    public Set<Identifier> commonCards;
    public Set<Identifier> rareCards;

    public static BoosterPackCardList mergeContents(BoosterPackCardList first, BoosterPackCardList second){
        first.commonCards.addAll(second.commonCards);
        first.rareCards.addAll(second.rareCards);
        return first;
    }
}
