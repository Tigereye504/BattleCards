package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BasicTestCard extends CardItem{
    public BasicTestCard(Settings settings) {
        super(settings);
    }

    public int advancedEffectCost(ItemStack stack, LivingEntity user, World world){
        return 0;
    }

    @Override
    public boolean performBasicEffect(ItemStack stack, LivingEntity user, World world) {
        return true;
    }

    @Override
    public boolean performAdvancedEffect(ItemStack stack, LivingEntity user, World world) {
        return true;
    }
}
