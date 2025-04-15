package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class TransferStatusEffect implements CardEffect, CardTooltipNester {

    StatusEffect type = null;
    int count = 0;
    boolean targetPositive = true;
    boolean targetNegative = true;
    boolean userToTarget = true;

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
        Entity donor = userToTarget ? user : target;
        Entity reciever = userToTarget ? target : user;
        if(donor instanceof LivingEntity leDonor) {
            List<StatusEffectInstance> toMove = new ArrayList<>();
            if (type != null && leDonor.hasStatusEffect(type)){
                toMove.add(leDonor.getStatusEffect(type));
            }
            else {
                int amountToMove = 0;
                List<StatusEffectInstance> effectInstances = new ArrayList<>(leDonor.getStatusEffects());
                effectInstances.sort((first, second) ->{
                    if(first.getAmplifier() > second.getAmplifier()){
                        return 1;
                    }
                    else if (first.getAmplifier() < second.getAmplifier()){
                        return -1;
                    }
                    else if (first.getDuration() > second.getDuration()){
                        return 1;
                    }
                    else if (first.getDuration() < second.getDuration()){
                        return -1;
                    }
                    return 0;
                });
                for(StatusEffectInstance instance : leDonor.getStatusEffects()){
                    if(amountToMove >= count){
                        break;
                    }
                    if((instance.getEffectType().isBeneficial() && targetPositive)
                            || (!instance.getEffectType().isBeneficial() && targetNegative)){
                        toMove.add(instance);
                        amountToMove++;
                    }
                }
            }
            for(StatusEffectInstance instance : toMove){
                leDonor.removeStatusEffect(instance.getEffectType());
                if(reciever instanceof LivingEntity leReciever){
                    leReciever.addStatusEffect(instance);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        if(type != null){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.transfer_status", type.getName())));
        }
        else {
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.transfer_status_count", count)));
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public TransferStatusEffect readFromJson(Identifier id, JsonElement entry) {
            TransferStatusEffect output = new TransferStatusEffect();

            String statusEffect = CardSerializer.readOrDefaultString(id,"type",entry,"");
            if(!statusEffect.isEmpty()) {
                Identifier statusEffectID = new Identifier(statusEffect);
                output.type = Registries.STATUS_EFFECT.get(statusEffectID);
                if (output.type == null) {
                    Battlecards.LOGGER.error("Could not find status effect {}!", statusEffectID);
                }
            }

            output.count = CardSerializer.readOrDefaultInt(id,"count",entry,0);
            output.targetPositive = CardSerializer.readOrDefaultBoolean(id,"targetPositive",entry,true);
            output.targetNegative = CardSerializer.readOrDefaultBoolean(id,"targetNegative",entry,true);
            output.userToTarget = CardSerializer.readOrDefaultBoolean(id,"userToTarget",entry,true);

            return output;
        }
    }
}
