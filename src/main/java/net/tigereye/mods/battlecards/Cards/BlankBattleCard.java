package net.tigereye.mods.battlecards.Cards;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;

import java.util.List;

public class BlankBattleCard implements BattleCard {

    @Override
    public Identifier getID() {
        return new Identifier("");
    }

    @Override
    public int getChargeEffectCost() {
        return 0;
    }

    @Override
    public Text getName() {
        return Text.empty();
    }

    @Override
    public Text getBasicDescription() {
        return Text.empty();
    }

    @Override
    public Text getChargeDescription() {
        return Text.empty();
    }

    @Override
    public List<CardEffect> getQuickEffects() {
        return List.of();
    }

    @Override
    public List<CardEffect> getChargeEffects() {
        return List.of();
    }

    @Override
    public boolean performBasicEffect(LivingEntity user, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean performChargeEffect(LivingEntity user, ItemStack itemStack) {
        return false;
    }
}
