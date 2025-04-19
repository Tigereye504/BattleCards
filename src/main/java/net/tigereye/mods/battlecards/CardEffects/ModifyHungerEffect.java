package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyHungerEffect implements CardEffect, CardTooltipNester {

    int hunger = 0;
    int saturation = 0;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(context.target);
        }
        else {
            apply(pContext.user);
        }
    }

    private void apply(Entity target) {
        if(target instanceof PlayerEntity pEntity){
            HungerManager manager = pEntity.getHungerManager();
            manager.setFoodLevel(manager.getFoodLevel()+hunger);
            manager.setSaturationLevel(manager.getSaturationLevel()+saturation);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.hunger",hunger, saturation)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyHungerEffect output = new ModifyHungerEffect();
            output.hunger = CardSerializer.readOrDefaultInt(id, "hunger",entry,0);
            output.saturation = CardSerializer.readOrDefaultInt(id, "saturation",entry,0);
            return output;
        }
    }
}
