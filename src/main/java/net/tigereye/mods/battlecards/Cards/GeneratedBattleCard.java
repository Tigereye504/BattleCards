package net.tigereye.mods.battlecards.Cards;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.Events.PreparePersistentContextCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GeneratedBattleCard implements BattleCard {
    Identifier id;
    int cost;
    Collection<Identifier> variants = new ArrayList<>();
    Collection<String> quickKeywords = new HashSet<>();
    Collection<String> chargeKeywords = new HashSet<>();
    List<CardEffect> quickEffects = new ArrayList<>();
    List<CardEffect> chargeEffects = new ArrayList<>();
    ItemStack scrapValue = ItemStack.EMPTY;

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
    public List<Identifier> getVariants() {
        return variants.stream().toList();
    }

    @Override
    public void addVariant(Identifier id){
        variants.add(id);
    }

    @Override
    public Collection<String> getQuickKeywords() {
        return quickKeywords;
    }
    public void addQuickKeyword(String string) {
        quickKeywords.add(string);
    }

    @Override
    public Collection<String> getChargeKeywords() {
        return chargeKeywords;
    }
    public void addChargeKeyword(String string) {
        chargeKeywords.add(string);
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
    public boolean performQuickEffect(LivingEntity user, ItemStack stack) {
        PersistantCardEffectContext pContext = new PersistantCardEffectContext(user,this,stack);
        PreparePersistentContextCallback.EVENT.invoker().preparePersistentContext(pContext,user,true);
        quickEffects.forEach((cardEffect -> cardEffect.apply(pContext)));
        return true;
    }

    @Override
    public boolean performChargeEffect(LivingEntity user, ItemStack stack) {
        PersistantCardEffectContext pContext = new PersistantCardEffectContext(user,this,stack);
        PreparePersistentContextCallback.EVENT.invoker().preparePersistentContext(pContext,user,false);
        chargeEffects.forEach((cardEffect -> cardEffect.apply(pContext)));
        return false;
    }

    @Override
    public ItemStack getScrapValue() {
        return scrapValue.copy();
    }

    public void setScrapValue(ItemStack scrapValue){
        this.scrapValue = scrapValue.copy();
    }
}
