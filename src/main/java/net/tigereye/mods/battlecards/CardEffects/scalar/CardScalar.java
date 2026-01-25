package net.tigereye.mods.battlecards.CardEffects.scalar;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;

import java.util.List;

public interface CardScalar {
    float getValue(PersistentCardEffectContext pContext, CardEffectContext context);
    default Text appendInlineTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext){return Text.literal("<missing>");}
}
