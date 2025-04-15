package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.List;

public class ModifyBreathEffect implements CardEffect, CardTooltipNester {

    int amount;

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        if(context.target != null){
            apply(user,context.target,item,battleCard);
        }
        else {
            apply(user, user, item, battleCard);
        }
    }

    private void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        target.setAir(target.getAir()+amount);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.breath",amount)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            ModifyBreathEffect output = new ModifyBreathEffect();
            output.amount = CardSerializer.readOrDefaultInt(id,"amount",entry,0);
            return output;
        }
    }
}
