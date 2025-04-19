package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
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
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;

import java.util.List;

public class GainManaEffect implements CardEffect, CardTooltipNester {

    private int amount;

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(pContext.cardItem.getItem() instanceof BattleCardItem bci) {
            bci.gainMana(pContext.user, pContext.cardItem, amount);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.gain_mana",amount)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public GainManaEffect readFromJson(Identifier id, JsonElement entry) {
            GainManaEffect output = new GainManaEffect();
            output.amount = CardSerializer.readOrDefaultInt(id,"amount",entry,1);
            return output;
        }
    }
}
