package net.tigereye.mods.battlecards.Cards;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;

import java.util.ArrayList;
import java.util.List;

public class GeneratedBattleCard implements BattleCard {
    Identifier id;
    int cost;
    List<CardEffect> quickEffects = new ArrayList<>();
    List<CardEffect> chargeEffects = new ArrayList<>();

    public Identifier getID() {
        return id;
    }
    public void setID(Identifier id) {
        this.id = id;
    }

    @Override
    public Text getName() {
        return Text.translatable("card."+getID().toTranslationKey());
    }
    @Override
    public Text getBasicDescription() {
        return Text.translatable("card."+getID().toTranslationKey()+".quick");
    }
    @Override
    public Text getChargeDescription() {
        return Text.translatable("card."+getID().toTranslationKey()+".charge");
    }

    @Override
    public int getChargeEffectCost() {
        return cost;
    }

    public void setChargeEffectCost(int cost) {
        this.cost = cost;
    }

    @Override
    public List<CardEffect> getQuickEffects() {
        return quickEffects;
    }

    @Override
    public List<CardEffect> getChargeEffects() {
        return chargeEffects;
    }

    public void setQuickEffects(List<CardEffect> effects) {
        quickEffects = effects;
    }

    public void setChargeEffects(List<CardEffect> effects) {
        chargeEffects = effects;
    }

    public void addBasicEffects(CardEffect effect) {
        quickEffects.add(effect);
    }

    public void addChargeEffects(CardEffect effect) {
        chargeEffects.add(effect);
    }

    @Override
    public boolean performBasicEffect(LivingEntity user, ItemStack stack) {
        quickEffects.forEach((cardEffect -> cardEffect.apply(user, stack, this)));
        return true;
    }

    @Override
    public boolean performChargeEffect(LivingEntity user, ItemStack stack) {
        chargeEffects.forEach((cardEffect -> cardEffect.apply(user, stack, this)));
        return false;
    }
}
