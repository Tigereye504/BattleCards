package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class DamageEffect implements CardEffect, CardTooltipNester {

    float scalingDamage;
    float damage;
    RegistryKey<DamageType> damageType;
    List<CardEffect> afterDamageEffects = new ArrayList<>();

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        if(context.target != null){
            apply(user,context.target,item,battleCard, context);
        }
        else {
            apply(user, user, item, battleCard, context);
        }
    }

    private void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        //TODO: apply enchantments (perhaps with a hook or event for better compatibility?)
        //TODO: apply sleeve modifiers (using an event for this sounds better and better)
        if(target != null) {
            if(damageType == null){
                Battlecards.LOGGER.warn("Missing damage type on {} damage effect. Replacing null with 'generic' damage type.",item.getName());
                damageType = DamageTypes.GENERIC;
            }
            if(target instanceof LivingEntity lEntity) {
                float targetHealth = lEntity.getHealth() + lEntity.getAbsorptionAmount();
                target.damage(target.getDamageSources().create(damageType, user), damage+(scalingDamage*context.scalar));
                float damageDealt = targetHealth - (lEntity.getHealth() + lEntity.getAbsorptionAmount());
                CardEffectContext postDamageContext = new CardEffectContext();
                postDamageContext.target = target;
                postDamageContext.scalar = damageDealt;
                for(CardEffect effect : afterDamageEffects){
                    effect.apply(user,item,battleCard,postDamageContext);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.damage",damage,
                        scalingDamage == 0 ? "" : (" + "+scalingDamage+"X"))));
        if(!afterDamageEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.after_damage")));
            for(CardEffect effect : afterDamageEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public List<CardEffect> getAfterDamageEffects() {
        return afterDamageEffects;
    }

    public void addAfterDamageEffect(CardEffect afterDamageEffect) {
        afterDamageEffects.add(afterDamageEffect);
    }
    public void setAfterDamageEffects(List<CardEffect> afterDamageEffects) {
        this.afterDamageEffects = afterDamageEffects;
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {

            DamageEffect output = new DamageEffect();
            output.scalingDamage = CardSerializer.readOrDefaultFloat(id,"scalingAmount",entry,0);
            output.damage = CardSerializer.readOrDefaultFloat(id,"amount",entry,0);
            output.damageType = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(
                    CardSerializer.readOrDefaultString(id,"damageType",entry,"")));
            output.afterDamageEffects = CardSerializer.readCardEffects(id, "afterDamage",entry);

            return output;
        }
    }
}
