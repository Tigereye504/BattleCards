package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardAfterDamageEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class DamageEntityEffect implements CardEffect, CardTargetEntityEffect, CardTooltipNester {

    private float damage;
    private RegistryKey<DamageType> damageType;
    private List<CardAfterDamageEffect> afterDamageEffects = new ArrayList<>();

    public DamageEntityEffect(float damage, RegistryKey<DamageType> damageType){
        this.damage = damage;
        this.damageType = damageType;
    }

    public DamageEntityEffect(float damage, Identifier damageType){
        this.damage = damage;
        this.damageType = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,damageType);
    }

    public DamageEntityEffect(float damage, String damageType){
        this.damage = damage;
        this.damageType = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,new Identifier(damageType));
    }

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        apply(user,user,item,battleCard);
    }

    @Override
    public void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard) {
        //TODO: apply enchantments (perhaps with a hook or event for better compatibility?)
        //TODO: apply sleeve modifiers (using an event for this sounds better and better)
        if(target != null) {
            if(damageType == null){
                Battlecards.LOGGER.warn("Missing damage type on battlecard damage effect. Replacing null with 'generic' damage type.");
                damageType = DamageTypes.GENERIC;
            }
            if(target instanceof LivingEntity lEntity) {
                float targetHealth = lEntity.getHealth() + lEntity.getAbsorptionAmount();
                target.damage(target.getDamageSources().create(damageType, user), damage);
                float damageDealt = targetHealth - (lEntity.getHealth() + lEntity.getAbsorptionAmount());

            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.damage",damage)));
        if(!afterDamageEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.after_damage")));
            for(CardAfterDamageEffect effect : afterDamageEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public List<CardAfterDamageEffect> getAfterDamageEffects() {
        return afterDamageEffects;
    }

    public void addAfterDamageEffect(CardAfterDamageEffect afterDamageEffect) {
        afterDamageEffects.add(afterDamageEffect);
    }
    public void setAfterDamageEffects(List<CardAfterDamageEffect> afterDamageEffects) {
        this.afterDamageEffects = afterDamageEffects;
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public CardEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                JsonObject obj = entry.getAsJsonObject();
                float damage = 0;
                String damageType = null;
                if (!obj.has("amount")) {
                    Battlecards.LOGGER.error("Damage effect missing amount!");
                }
                else{
                    damage = obj.get("amount").getAsInt();
                }
                if (!obj.has("damageType")) {
                    Battlecards.LOGGER.error("Damage effect missing damage type!");
                }
                else{
                    damageType = obj.get("damageType").getAsString();
                }
                DamageEntityEffect output = new DamageEntityEffect(damage,damageType);
                if (obj.has("afterDamage")) {
                    JsonArray onHitJson = obj.get("afterDamage").getAsJsonArray();
                    List<CardEffect> afterDamageEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : afterDamageEffectsRaw){
                        if(effect instanceof CardAfterDamageEffect cadEffect){
                            output.addAfterDamageEffect(cadEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An afterDamage CardEffect in {} cannot target entity!",id);
                        }
                    }
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing damage effect!");
                return new DamageEntityEffect(0,"generic");
            }
        }
    }
}
