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
    private final float damageMultiplier;
    private final float statusEffectDurationMultiplier;
    private final int statusEffectMagnitudeAdder;

    public SimpleCardSleeve(Settings settings, float damageMultiplier) {
        this(settings,damageMultiplier,1,0);
    }

    public SimpleCardSleeve(Settings settings, float damageMultiplier, float statusEffectDurationMultiplier, int statusEffectMagnitudeAdder) {
        super(settings);
        this.damageMultiplier = damageMultiplier;
        this.statusEffectDurationMultiplier = statusEffectDurationMultiplier;
        this.statusEffectMagnitudeAdder = statusEffectMagnitudeAdder;
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

    public void preparePersistentContext(PersistentCardEffectContext pContext, Entity user, ItemStack sleeve, boolean quickElseCharge) {
        if(damageMultiplier != 1) {
            pContext.modifyDamageCallbacks.add((pContext2, target, context, amount)
                    -> modifyDamage(pContext, target, context, amount, sleeve));
        }
        if(statusEffectDurationMultiplier != 1 || statusEffectMagnitudeAdder != 0) {
            pContext.modifyStatusEffectListeners.add((pContext2, target, context, instance)
                    -> modifyStatusEffect(pContext, target, context, instance, sleeve));
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
        super.appendTooltip(stack, world, tooltip, context);
    }
}
