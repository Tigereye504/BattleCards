package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Items.BattleCardItem;

import java.util.List;

public class GainManaEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    private int amount;

    public GainManaEffect(int amount){
        this.amount = amount;
    }

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }
    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(item.getItem() instanceof BattleCardItem bci) {
            bci.gainMana(user, item, amount);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.gain_mana",amount)));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public GainManaEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                JsonObject obj = entry.getAsJsonObject();
                int amount = 0;
                if (!obj.has("amount")) {
                    Battlecards.LOGGER.error("Mana effect missing amount!");
                }
                else{
                    amount = obj.get("amount").getAsInt();
                }
                return new GainManaEffect(amount);
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing mana effect!");
                return new GainManaEffect(0);
            }
        }
    }
}
