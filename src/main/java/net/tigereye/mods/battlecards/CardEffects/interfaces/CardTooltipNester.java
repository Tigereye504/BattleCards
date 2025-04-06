package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public interface CardTooltipNester {
    void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth);
}
