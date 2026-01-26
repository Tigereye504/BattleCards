package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleCardSleeve extends CardSleeveItem{
    private float damageMultiplier = 1;
    private float statusEffectDurationMultiplier = 1;
    private int statusEffectMagnitudeAdder = 0;
    private int manaGainAdder = 0;
    private float manaCostMultiplier = 1;


    public SimpleCardSleeve(Settings settings){
        super(settings);
    }

    public SimpleCardSleeve DamageMultiplier(float damageMultiplier){
        this.damageMultiplier = damageMultiplier;
        return this;
    }
    public SimpleCardSleeve StatusEffectModifiers(float statusEffectDurationMultiplier, int statusEffectMagnitudeAdder){
        this.statusEffectDurationMultiplier = statusEffectDurationMultiplier;
        this.statusEffectMagnitudeAdder = statusEffectMagnitudeAdder;
        return this;
    }
    public SimpleCardSleeve ManaGainAdder(int manaGainAdder){
        this.manaGainAdder = manaGainAdder;
        return this;
    }
    public SimpleCardSleeve ManaCostMultiplier(float manaCostMultiplier){
        this.manaCostMultiplier = manaCostMultiplier;
        return this;
    }

    @Override
    public float modifyDamage(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, float amount, ItemStack sleeve){
        return amount*damageMultiplier;
    }

    @Override
    public StatusEffectInstance modifyStatusEffect(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, StatusEffectInstance instance, ItemStack sleeve){
        int newDuration = Math.max(0,(int) (instance.getDuration() * statusEffectDurationMultiplier));
        int newMagnitude = Math.max(0,instance.getAmplifier() + statusEffectMagnitudeAdder);
        return new StatusEffectInstance(instance.getEffectType(),newDuration,newMagnitude);
    }

    @Override
    public int modifyManaGain(PersistentCardEffectContext pContext, Entity target, CardEffectContext context, int amount, ItemStack sleeve){
        return amount+manaGainAdder;
    }

    @Override
    public int modifyManaCost(Entity user, ItemStack item, int cost, boolean forDisplay){
        return (int) Math.ceil(cost*manaCostMultiplier);
    }

    public void preparePersistentContext(PersistentCardEffectContext pContext, Entity user, ItemStack sleeve, boolean quickElseCharge) {
        if(damageMultiplier != 1) {
            pContext.modifyDamageCallbacks.add((pContext2, target, context, amount)
                    -> modifyDamage(pContext, target, context, amount, sleeve));
        }
        if(statusEffectDurationMultiplier != 1 || statusEffectMagnitudeAdder != 0) {
            pContext.modifyStatusEffectCallbacks.add((pContext2, target, context, instance)
                    -> modifyStatusEffect(pContext, target, context, instance, sleeve));
        }
        if(manaGainAdder != 0){
            pContext.manaGainCallbacks.add((pContext2, target, context, amount)
                    -> modifyManaGain(pContext, target, context, amount, sleeve));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(damageMultiplier != 1) {
            tooltip.add(Text.translatable("item.battlecards.sleeve.simple.damageDesc",
                    damageMultiplier > 1 ? "+" : "", (int) ((damageMultiplier - 1) * 100)));
        }
        if(statusEffectDurationMultiplier != 1) {
            tooltip.add(Text.translatable("item.battlecards.sleeve.simple.statusDurationDesc",
                    statusEffectDurationMultiplier > 1 ? "+" : "", (int) ((statusEffectDurationMultiplier - 1) * 100)));
        }
        if(statusEffectMagnitudeAdder != 0) {
            tooltip.add(Text.translatable("item.battlecards.sleeve.simple.statusMagnitudeDesc",
                    statusEffectMagnitudeAdder > 0 ? "+" : "", statusEffectMagnitudeAdder));
        }
        if(manaGainAdder != 0){
            tooltip.add(Text.translatable("item.battlecards.sleeve.simple.manaGainDesc",
                    manaGainAdder > 0 ? "+" : "", manaGainAdder));
        }
        if(manaCostMultiplier != 1){
            tooltip.add(Text.translatable("item.battlecards.sleeve.simple.manaCostDesc",
                    manaCostMultiplier > 1 ? "+" : "", (int) ((manaCostMultiplier - 1) * 100)));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
