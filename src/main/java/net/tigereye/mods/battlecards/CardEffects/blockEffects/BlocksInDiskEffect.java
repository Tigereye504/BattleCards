package net.tigereye.mods.battlecards.CardEffects.blockEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlocksInDiskEffect implements CardEffect, CardTooltipNester {

    CardScalar blocks = new ConstantScalarEffect(1);
    List<CardEffect> effects = new ArrayList<>();

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if (context.target != null) {
            apply(pContext, context, context.target);
        }
        else if(context.blockPos != null){
            apply(pContext, context, context.blockPos);
        }
        else if(context.hitResult != null){
            apply(pContext, context, BlockPos.ofFloored(context.hitResult.getPos()));
        }
        else {
            apply(pContext, context, pContext.user);
        }
    }

    private void apply(PersistentCardEffectContext pContext, CardEffectContext context, Entity target) {
        apply(pContext, context, target.getBlockPos());
    }

    private void apply(PersistentCardEffectContext pContext, CardEffectContext context, BlockPos pos) {
        Set<BlockPos> blocklist = findBlocksInRange(pos,Direction.DOWN,(int)blocks.getValue(pContext, context));
        for(CardEffect effect : effects){
            blocklist.forEach((listPos) -> {
                CardEffectContext newContext = context.clone();
                newContext.target = null;
                newContext.blockPos = listPos;
                effect.apply(pContext,newContext);
            });
        }
    }

    private Set<BlockPos> findBlocksInRange(BlockPos pos, Direction dir, int blocks){
        Set<BlockPos> positions = new HashSet<>();
        positions.add(pos);
        Vec3i x;
        Vec3i z = switch (dir) {
            case UP, DOWN -> {
                x = Direction.NORTH.getVector();
                yield Direction.EAST.getVector();
            }
            case EAST, WEST -> {
                x = Direction.NORTH.getVector();
                yield Direction.UP.getVector();
            }
            default -> {
                x = Direction.UP.getVector();
                yield Direction.EAST.getVector();
            }
        };
        int radius = 1;
        int width = 0;
        while (blocks > 1) {
            addLevelOfBlocks(pos,positions,radius,width,x,z);
            if(width >= radius){
                radius++;
                width = 0;
                blocks -= 4;
            }
            else{
                if(width == 0){
                    blocks -= 4;
                }
                else{
                    blocks -= 8;
                }
                width++;
            }
        }
        return positions;
    }
    private void addLevelOfBlocks(BlockPos pos, Set<BlockPos> positions,int radius, int width, Vec3i x, Vec3i z){
        if(width == 0){
            positions.add(pos.add(x.multiply(radius)));
            positions.add(pos.add(x.multiply(-radius)));
            positions.add(pos.add(z.multiply(radius)));
            positions.add(pos.add(z.multiply(-radius)));
        }
        else{
            positions.add(pos.add(x.multiply(radius)).add(z.multiply(width)));
            positions.add(pos.add(x.multiply(-radius)).add(z.multiply(width)));
            positions.add(pos.add(z.multiply(radius)).add(x.multiply(width)));
            positions.add(pos.add(z.multiply(-radius)).add(x.multiply(width)));
            positions.add(pos.add(x.multiply(radius)).add(z.multiply(-width)));
            positions.add(pos.add(x.multiply(-radius)).add(z.multiply(-width)));
            positions.add(pos.add(z.multiply(radius)).add(x.multiply(-width)));
            positions.add(pos.add(z.multiply(-radius)).add(x.multiply(-width)));
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.blocks_in_disk", blocks.appendInlineTooltip(world, tooltip, tooltipContext))));
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

            BlocksInDiskEffect output = new BlocksInDiskEffect();
            output.blocks = CardSerializer.readOrDefaultScalar(id,"blocks",entry,0);
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            return output;
        }
    }
}
