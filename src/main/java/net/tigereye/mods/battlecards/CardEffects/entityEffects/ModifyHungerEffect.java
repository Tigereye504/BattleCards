package net.tigereye.mods.battlecards.CardEffects.entityEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyHungerEffect implements CardEffect, CardTooltipNester {

    CardScalar hunger;
    CardScalar saturation;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        Entity target;
        if(context.target != null){
            target = context.target;
        }
        else {
            target = pContext.user;
        }
        if(target instanceof PlayerEntity pEntity){
            HungerManager manager = pEntity.getHungerManager();
            manager.setFoodLevel(manager.getFoodLevel()+((int)hunger.getValue(pContext, context)));
            manager.setSaturationLevel(manager.getSaturationLevel()+saturation.getValue(pContext, context));
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.hunger",
                        hunger.appendInlineTooltip(world, tooltip, tooltipContext).getString(),
                        saturation.appendInlineTooltip(world, tooltip, tooltipContext).getString())));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyHungerEffect output = new ModifyHungerEffect();
            output.hunger = CardSerializer.readOrDefaultScalar(id, "hunger",entry,0);
            output.saturation = CardSerializer.readOrDefaultScalar(id, "saturation",entry,0);
            return output;
        }
    }
}
