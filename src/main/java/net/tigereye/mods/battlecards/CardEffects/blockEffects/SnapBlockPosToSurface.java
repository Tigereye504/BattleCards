package net.tigereye.mods.battlecards.CardEffects.blockEffects;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Util.DirectionUtil;

import java.util.ArrayList;
import java.util.List;

public class SnapBlockPosToSurface implements CardEffect, CardTooltipNester {

    private static final Direction DEFAULT_DIRECTION = Direction.DOWN;
    private static final Direction DEFAULT_DIRECTION_WHEN_RELATIVE = Direction.NORTH;

    CardScalar maxBackwardDistance = new ConstantScalarEffect(0);
    CardScalar maxDistance = new ConstantScalarEffect(3);
    CardScalar distanceFromSurface = new ConstantScalarEffect(0);
    boolean directionRelativeToEntity = false;
    Direction direction;
    boolean liquidsAreAir = false;
    boolean abortWithoutSurface = false;
    List<CardEffect> effects = new ArrayList<>();

    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        Entity target = context.target;
        if(target == null){
            target = pContext.user;
        }

        Direction usedDirection = this.direction;
        if(directionRelativeToEntity && target != null){
            usedDirection = DirectionUtil.deriveDirectionFromPolarAndRotationDirection(target.getPitch(),target.getYaw(),direction != null ? direction : DEFAULT_DIRECTION_WHEN_RELATIVE);
        }
        if(usedDirection == null){
            usedDirection = DEFAULT_DIRECTION;
        }

        if (context.target != null) {
            apply(pContext, context, usedDirection, context.target.getBlockPos());
        }
        else if(context.blockPos != null){
            apply(pContext, context, usedDirection, context.blockPos);
        }
        else if(context.hitResult != null){
            apply(pContext, context, usedDirection, BlockPos.ofFloored(context.hitResult.getPos()));
        }
        else {
            apply(pContext, context, usedDirection, pContext.user.getBlockPos());
        }
    }

    private void apply(PersistentCardEffectContext pContext, CardEffectContext context, Direction snapDirection, BlockPos blockPos) {
        World world = pContext.user.getWorld();
        BlockPos searchingPos;
        if(isAirOrMaybeLiquids(world,blockPos)){
            searchingPos = lookForward(world, snapDirection,blockPos,(int) maxDistance.getValue(pContext,context));
        }
        else{
            searchingPos = lookBackward(world, snapDirection,blockPos,(int) maxBackwardDistance.getValue(pContext,context));
        }
        if(searchingPos != null) {
            searchingPos = searchingPos.add(snapDirection.getOpposite().getVector().multiply((int)distanceFromSurface.getValue(pContext,context)));
            for (CardEffect effect : effects) {
                CardEffectContext newContext = context.clone();
                newContext.target = null;
                newContext.blockPos = searchingPos;
                effect.apply(pContext, newContext);
            }
        }
    }

    private BlockPos lookForward(World world, Direction snapDirection, BlockPos blockPos, int maxDistance) {
        if(maxDistance <= 0){
            return blockPos;
        }
        BlockPos searchingPos = blockPos;
        int distance = 0;
        while(isAirOrMaybeLiquids(world,searchingPos) && distance < maxDistance){
            searchingPos = searchingPos.add(snapDirection.getVector());
            distance++;
        }
        if(abortWithoutSurface && distance == maxDistance && isAirOrMaybeLiquids(world,searchingPos)){
            //if abortWithoutSurface is set, the maximum distance is reached, and the block is still air, abort
            return null;
        }
        return searchingPos;
    }

    private BlockPos lookBackward(World world, Direction snapDirection, BlockPos blockPos, int maxDistance) {
        if(maxDistance <= 0){
            return blockPos;
        }
        snapDirection = snapDirection.getOpposite();
        BlockPos searchingPos = blockPos;
        int distance = 0;
        while(!isAirOrMaybeLiquids(world,searchingPos) && distance < maxDistance){
            searchingPos = searchingPos.add(snapDirection.getVector());
            distance++;
        }
        if(isAirOrMaybeLiquids(world,searchingPos)){
            searchingPos = searchingPos.add(snapDirection.getOpposite().getVector());
        }
        return searchingPos;
    }

    private boolean isAirOrMaybeLiquids(World world, BlockPos pos){
        Block block = world.getBlockState(pos).getBlock();
        return block == Blocks.AIR || (liquidsAreAir && (!block.getDefaultState().getFluidState().isEmpty()));
    }

    @Override
    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.snap_block_pos_to_surface",
                            maxDistance.appendInlineTooltip(world, tooltip, tooltipContext),
                            maxBackwardDistance.appendInlineTooltip(world, tooltip, tooltipContext))));
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            SnapBlockPosToSurface output = new SnapBlockPosToSurface();
            output.maxDistance = CardSerializer.readOrDefaultScalar(id,"maxDistance",entry,3);
            output.maxBackwardDistance = CardSerializer.readOrDefaultScalar(id,"maxBackwardDistance",entry,0);
            output.distanceFromSurface = CardSerializer.readOrDefaultScalar(id,"distanceFromSurface",entry,0);
            output.direction = CardSerializer.readOrDefaultDirection(id,"direction",entry,null);
            output.directionRelativeToEntity = CardSerializer.readOrDefaultBoolean(id,"directionRelativeToEntity",entry,false);
            output.liquidsAreAir = CardSerializer.readOrDefaultBoolean(id,"liquidsAreAir",entry,false);
            output.abortWithoutSurface = CardSerializer.readOrDefaultBoolean(id,"abortWithoutSurface",entry,false);
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            return output;
        }
    }
}
