package net.tigereye.mods.battlecards.CardEffects.blockEffects;

import com.google.gson.JsonElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ReplaceBlockEffect implements CardEffect, CardTooltipNester {

    CardScalar maxBlastRes = new ConstantScalarEffect(0);
    BlockState block = Blocks.DIRT.getDefaultState();


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
        World world = pContext.user.getWorld();
        BlockState curBlock = world.getBlockState(pos);
        AutomaticItemPlacementContext ipc = new AutomaticItemPlacementContext(world,pos,pContext.user.getHorizontalFacing(), block.getBlock().asItem().getDefaultStack(),pContext.user.getHorizontalFacing());
        float blockBlastRes = curBlock.getBlock().getBlastResistance();
        float _maxBlastRes = maxBlastRes.getValue(pContext,context);
        if(curBlock.getBlock().getBlastResistance() <= maxBlastRes.getValue(pContext,context)) {
            world.setBlockState(pos, block, 3);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.replace_block",
                        maxBlastRes.appendInlineTooltip(world,tooltip,tooltipContext).getString(),
                        Text.translatable(block.getBlock().getTranslationKey()))));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {

            ReplaceBlockEffect output = new ReplaceBlockEffect();
            output.maxBlastRes = CardSerializer.readOrDefaultScalar(id,"maxBlastRes",entry,0);
            output.block = Registries.BLOCK.get(new Identifier(
                    CardSerializer.readOrDefaultString(id, "block",entry,"minecraft:dirt")))
                    .getDefaultState();
            return output;
        }
    }
}
