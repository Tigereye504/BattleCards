package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class CardItem extends Item implements Card {

    private static final int COOLDOWN = 100;
    public CardItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean goOnCooldown = false;
        if(remainingUseTicks <= 0){
            goOnCooldown = performAdvancedEffect(stack, user, world);
        }
        else{
            goOnCooldown = performBasicEffect(stack,user,world);
        }
        if(goOnCooldown && user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(stack.getItem(),100);
        }
    }
}
