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
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistantCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Events.ModifyDamageCardEffectCallback;

import java.util.ArrayList;
import java.util.List;

public class DamageEffect implements CardEffect, CardTooltipNester {

    float scalingDamage = 0;
    CardScalar damage;
    RegistryKey<DamageType> damageType;
    List<CardEffect> afterDamageEffects = new ArrayList<>();

    @Override
    public void apply(PersistantCardEffectContext pContext, CardEffectContext context) {
        if(context.target != null){
            apply(pContext,context.target, context);
        }
        else {
            apply(pContext, pContext.user, context);
        }
    }

    private void apply(PersistantCardEffectContext pContext, Entity target, CardEffectContext context) {
        if(target != null) {
            if(damageType == null){
                Battlecards.LOGGER.warn("Missing damage type on {} damage effect. Replacing null with 'generic' damage type.",pContext.cardItem.getName());
                damageType = DamageTypes.GENERIC;
            }
            if(target instanceof LivingEntity lEntity) {
                float targetHealth = lEntity.getHealth() + lEntity.getAbsorptionAmount();
                float modifiedDamage = ModifyDamageCardEffectCallback.EVENT.invoker()
                        .modifyDamage(pContext,target,context,damage.getValue(pContext,context)+(scalingDamage*context.scalar));
                target.damage(target.getDamageSources().create(damageType, pContext.user), modifiedDamage);
                float damageDealt = targetHealth - (lEntity.getHealth() + lEntity.getAbsorptionAmount());
                //TODO: call post damage event
                CardEffectContext postDamageContext = new CardEffectContext();
                postDamageContext.target = target;
                postDamageContext.scalar = damageDealt;
                for(CardEffect effect : afterDamageEffects){
                    effect.apply(pContext,postDamageContext);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.damage",damage.appendInlineTooltip(world,tooltip,tooltipContext).getString(),
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
            output.damage = CardSerializer.readOrDefaultScalar(id,"amount",entry,0);
            output.damageType = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(
                    CardSerializer.readOrDefaultString(id,"damageType",entry,"")));
            output.afterDamageEffects = CardSerializer.readCardEffects(id, "afterDamage",entry);

            return output;
        }
    }
}
