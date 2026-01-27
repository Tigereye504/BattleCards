package net.tigereye.mods.battlecards.CardEffects.blockEffects;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class SnapBlockPosToSurface implements CardEffect, CardTooltipNester {

    CardScalar maxBackwardDistance = new ConstantScalarEffect(0);
    CardScalar maxDistance = new ConstantScalarEffect(3);
    CardScalar distanceFromSurface = new ConstantScalarEffect(0);
    Direction direction = null;
    boolean liquidsAreAir = false;
    List<CardEffect> effects = new ArrayList<>();

    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        Direction usedDirection = direction;
        if(usedDirection == null){
            usedDirection = context.direction;
        }
        if(usedDirection == null){
            usedDirection = Direction.DOWN;
        }
        if (context.target != null) {
            apply(pContext, context, direction, context.target.getBlockPos());
        }
        else if(context.blockPos != null){
            apply(pContext, context, direction, context.blockPos);
        }
        else if(context.hitResult != null){
            apply(pContext, context, direction, BlockPos.ofFloored(context.hitResult.getPos()));
        }
        else {
            apply(pContext, context, direction, pContext.user.getBlockPos());
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
        searchingPos = searchingPos.add(snapDirection.getOpposite().getVector().multiply((int)distanceFromSurface.getValue(pContext,context)));
        for(CardEffect effect : effects){
            CardEffectContext newContext = context.clone();
            newContext.target = null;
            newContext.blockPos = searchingPos;
            effect.apply(pContext,newContext);
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
            output.direction = Direction.DOWN;//TODO: replace with direction reader
            output.liquidsAreAir = CardSerializer.readOrDefaultBoolean(id,"liquidsAreAir",entry,false);
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            return output;
        }
    }
}
