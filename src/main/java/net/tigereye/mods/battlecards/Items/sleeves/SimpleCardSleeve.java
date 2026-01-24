package net.tigereye.mods.battlecards.Items.sleeves;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.Items.interfaces.CardSleeve;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class SimpleCardSleeve extends CardSleeveItem{
    private final float damageMultiplier;
    public SimpleCardSleeve(Settings settings, float damageMultiplier) {
        super(settings);
        this.damageMultiplier = damageMultiplier;
    }

    public float modifyDamage(PersistantCardEffectContext pContext, Entity target, CardEffectContext context, float amount, ItemStack sleeve){
        return amount*damageMultiplier;
    }

    public void preparePersistentContext(PersistantCardEffectContext pContext, Entity user, ItemStack sleeve, boolean quickElseCharge) {
        pContext.modifyDamageListeners.add((pContext2,target,context,amount)
                -> modifyDamage(pContext,target,context,amount, sleeve));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.battlecards.sleeve.simple.desc",(int)((damageMultiplier-1)*100)));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
