package net.tigereye.mods.battlecards.CardEffects.blockEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalarCoordinates;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class BlocksInListEffect implements CardEffect, CardTooltipNester {

    List<CardScalarCoordinates> blocklist = new ArrayList<>();
    List<CardEffect> effects = new ArrayList<>();
    //TODO: make direction sensitive based on projectile or user facing

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
        for(CardEffect effect : effects){
            blocklist.forEach((listPos) -> {
                CardEffectContext newContext = context.clone();
                newContext.target = null;
                newContext.blockPos = new BlockPos(
                        pos.getX()+(int)listPos.x.getValue(pContext,context),
                        pos.getY()+(int)listPos.y.getValue(pContext,context),
                        pos.getZ()+(int)listPos.z.getValue(pContext,context)
                );
                effect.apply(pContext,newContext);
            });
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(!effects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.blocks_in_list",blocklist.size())));
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

            BlocksInListEffect output = new BlocksInListEffect();
            output.blocklist = CardSerializer.readOrDefaultCoordinatesList(id,"blocklist",entry,new ArrayList<>());
            output.effects = CardSerializer.readCardEffects(id, "effects",entry);
            return output;
        }
    }
}
