package net.tigereye.mods.battlecards.CardEffects.modifiers;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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

import java.util.ArrayList;
import java.util.List;

public class IfBlockCondition implements CardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    List<CardEffect> falseEffects = new ArrayList<>();
    List<Block> block = new ArrayList<>();

    public IfBlockCondition(){}

    public IfBlockCondition(List<Block> block, List<CardEffect> effects,List<CardEffect> falseEffects){
        this.block = block;
        this.effects = effects;
        this.falseEffects = falseEffects;
    }

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if (context.target != null) {
            apply(pContext, context, context.target.getBlockPos());
        } else if (context.blockPos != null) {
            apply(pContext, context, context.blockPos);
        } else if (context.hitResult != null) {
            apply(pContext, context, BlockPos.ofFloored(context.hitResult.getPos()));
        } else {
            apply(pContext, context, pContext.user.getBlockPos());
        }
    }

    public void apply(PersistentCardEffectContext pContext, CardEffectContext context, BlockPos pos) {
        boolean match = false;
        World world = pContext.user.getWorld();
        for (Block block : block){
            if(world.getBlockState(pos).getBlock() == block){
                match = true;
                break;
            }
        }
        if (match){
            for(CardEffect effect : effects) {
                effect.apply(pContext,context);
            }
        }
        else{
            for(CardEffect effect : falseEffects) {
                effect.apply(pContext,context);
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty() && !block.isEmpty()) {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_block",block.get(0).getName())));
            for (CardEffect effect : effects) {
                if (effect instanceof CardTooltipNester nester) {
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                }
            }
        }
        if(!falseEffects.isEmpty()) {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.if_block.false",
                            block.isEmpty() ? block.get(0).getName() : "")));
            for (CardEffect effect : falseEffects) {
                if (effect instanceof CardTooltipNester nester) {
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth + 1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public IfBlockCondition readFromJson(Identifier id, JsonElement entry) {
            IfBlockCondition output = new IfBlockCondition();

            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            output.falseEffects = CardSerializer.readCardEffects(id, "falseEffects",entry);
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on if scalar in {}!",id);
            }

            for(String string : CardSerializer.readOrDefaultStringList(id,"block",entry,new ArrayList<>())){
                try {
                    Block block = Registries.BLOCK.get(Identifier.tryParse(string));
                    if (block != null) {
                        output.block.add(block);
                    }
                    else{
                        Battlecards.LOGGER.error("cannot find block {} from list in {}!",string,id.toString());
                    }
                }
                catch(Exception e){
                    Battlecards.LOGGER.error("error reading block {} from list in {}!",string,id.toString());
                }
            }

            return output;
        }
    }
}
