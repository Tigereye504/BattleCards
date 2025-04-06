package net.tigereye.mods.battlecards.Items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public abstract class ExperimentalCardItem extends Item implements BattleCardItem {

    private static final int BASIC_COOLDOWN = 20;
    private static final int ADVANCED_COOLDOWN = 100;
    public ExperimentalCardItem(Settings settings) {
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
        int cooldownTime = 0;

        if(this.getMaxUseTime(stack) - remainingUseTicks > 20){
            //TODO: extract energy cost
            if(performAdvancedEffect(stack, user, world)){
                cooldownTime = ADVANCED_COOLDOWN;
            }
        }
        else{
            if(performBasicEffect(stack,user,world)){
                cooldownTime = BASIC_COOLDOWN;
            }
        }
        if(cooldownTime > 0 && user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(stack.getItem(),cooldownTime);
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
}
