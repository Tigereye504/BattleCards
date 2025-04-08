package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Util.DelayedAction;
import net.tigereye.mods.battlecards.Util.DelayedActionTaker;

import java.util.ArrayList;
import java.util.List;

public class DelayedEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    int delay = 1;
    List<CardTargetEntityEffect> effects = new ArrayList<>();

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        if(user instanceof DelayedActionTaker dat){
            dat.battleCards$addDelayedAction(new DelayedCardAction(user,target,item,battleCard,effects,delay));
        }
    }

    private void addEffect(CardTargetEntityEffect cteEffect) {
        effects.add(cteEffect);
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.delay_entity",delay)));
        if(!effects.isEmpty()){
            for(CardTargetEntityEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    private static class DelayedCardAction extends DelayedAction {

        Entity user;
        Entity target;
        ItemStack item;
        BattleCard card;
        List<CardTargetEntityEffect> effects;


        DelayedCardAction(Entity user, Entity target, ItemStack item, BattleCard card, List<CardTargetEntityEffect> effects, int delay){
            this.user = user;
            this.target = target;
            this.item = item;
            this.card = card;
            this.effects = effects;
            setTicks(delay);
        }

        @Override
        public void act() {
            for(CardTargetEntityEffect effect : effects){
                effect.apply(user,target,item,card);
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                DelayedEntityEffect output = new DelayedEntityEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (!obj.has("after_delay")) {
                    Battlecards.LOGGER.error("Delay Entity Effect missing effect!");
                }
                else{
                    JsonArray onHitJson = obj.get("after_delay").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        if(effect instanceof CardTargetEntityEffect cteEffect){
                            output.addEffect(cteEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("A CardEffect in {} cannot target entity!",id);
                        }
                    }
                }
                if (obj.has("delay")) {
                    output.delay = obj.get("delay").getAsInt();
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing delay effect!");
                return new DelayedEntityEffect();
            }
        }
    }
}
