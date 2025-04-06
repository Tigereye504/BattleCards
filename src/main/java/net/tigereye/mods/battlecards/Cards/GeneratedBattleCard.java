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
    Identifier art;
    Identifier icon;
    Identifier background;
    int cost;
    List<CardEffect> basicEffects = new ArrayList<>();
    List<CardEffect> advancedEffects = new ArrayList<>();
    String basicDescription = "";
    String chargeDescription = "";

    public Identifier getArt() {
        return art;
    }
    public void setArt(Identifier texture) {
        this.art = texture;
    }
    public Identifier getIcon() {
        return icon;
    }
    public void setIcon(Identifier texture) {
        this.icon = texture;
    }
    public Identifier getBackground() {
        return background;
    }
    public void setBackground(Identifier texture) {
        this.background = texture;
    }

    @Override
    public Text getBasicDescription() {
        return Text.translatable(basicDescription);
    }
    @Override
    public Text getChargeDescription() {
        return Text.translatable(chargeDescription);
    }

    public void setBasicDescription(String translationKey) {
       this.basicDescription = translationKey;
    }
    public void setChargeDescription(String translationKey) {
        this.chargeDescription = translationKey;
    }

    @Override
    public int getChargeEffectCost() {
        return cost;
    }

    public void setChargeEffectCost(int cost) {
        this.cost = cost;
    }

    @Override
    public List<CardEffect> getBasicEffects() {
        return basicEffects;
    }

    @Override
    public List<CardEffect> getChargeEffects() {
        return advancedEffects;
    }

    public void setBasicEffects(List<CardEffect> effects) {
        basicEffects = effects;
    }

    public void setChargeEffects(List<CardEffect> effects) {
        advancedEffects = effects;
    }

    public void addBasicEffects(CardEffect effect) {
        basicEffects.add(effect);
    }

    public void addChargeEffects(CardEffect effect) {
        advancedEffects.add(effect);
    }

    @Override
    public boolean performBasicEffect(LivingEntity user, ItemStack stack) {
        basicEffects.forEach((cardEffect -> cardEffect.apply(user, stack, this)));
        return true;
    }

    @Override
    public boolean performChargeEffect(LivingEntity user, ItemStack stack) {
        advancedEffects.forEach((cardEffect -> cardEffect.apply(user, stack, this)));
        return false;
    }
}
