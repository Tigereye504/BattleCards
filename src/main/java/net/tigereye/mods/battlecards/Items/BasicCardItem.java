package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public abstract class BasicCardItem extends Item implements BattleCard {

    private static final int COOLDOWN = 100;
    public BasicCardItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean goOnCooldown;

        if(this.getMaxUseTime(stack) - remainingUseTicks > 20){
            //TODO: extract energy cost
            goOnCooldown = performAdvancedEffect(stack, user, world);
        }
        else{
            goOnCooldown = performBasicEffect(stack,user,world);
        }
        if(goOnCooldown && user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(stack.getItem(),COOLDOWN);
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
}
