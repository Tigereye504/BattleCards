package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Card {


    //on non-crouch use, perform the card's basic effect

    //on crouch-use, if the user has enough energy goOnCooldown it for the card's advanced effect

    //these are best defined via datapack... but that can be a later step

    public int advancedEffectCost(ItemStack stack, LivingEntity user, World world);
    public boolean performBasicEffect(ItemStack stack, LivingEntity user, World world);
    public boolean performAdvancedEffect(ItemStack stack, LivingEntity user, World world);
}
