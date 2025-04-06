package net.tigereye.mods.battlecards.CardEffects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardOnCollisionEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTargetEntityEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Items.BattleCardItem;

import java.util.ArrayList;
import java.util.List;

public class MeleeEffect implements CardEffect, CardTooltipNester {

    List<CardTargetEntityEffect> onEntityHitEffects = new ArrayList<>();
    double reach = 3.5;

    public void addEffectOnEntityHit(CardTargetEntityEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardTargetEntityEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard) {
        //TODO: raycast out to reach (configured, by default use the user's reach)
        Vec3d boxMin = user.getEyePos().add(-reach,-reach,-reach);
        Vec3d boxMax = user.getEyePos().add(reach,reach,reach);
        EntityHitResult ehr = ProjectileUtil.raycast(user,
                user.getEyePos(),
                user.getEyePos().add(user.getRotationVec(1).multiply(reach)),
                user.getBoundingBox().stretch(user.getRotationVec(1.0f).multiply(reach)).expand(reach), entity -> entity != user,reach*reach);
        //TODO: on entity hit, apply effects
        if(ehr != null){
            if(user instanceof PlayerEntity pEntity){
                pEntity.swingHand(pEntity.getActiveHand());
            }
            for (CardTargetEntityEffect effect : onEntityHitEffects){
                effect.apply(user,ehr.getEntity(),item,battleCard);
            }
        }
        //TODO: block targeting stuff could be a different 'onTouch' event. Or not.
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.melee")));
        if(!onEntityHitEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_hit")));
            for(CardTargetEntityEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public MeleeEffect readFromJson(Identifier id, JsonElement entry) {
            try {
                MeleeEffect output = new MeleeEffect();
                JsonObject obj = entry.getAsJsonObject();
                if (obj.has("reach")){
                    output.reach = obj.get("reach").getAsFloat();
                }
                if (obj.has("onHit")) {
                    JsonArray onHitJson = obj.get("onHit").getAsJsonArray();
                    List<CardEffect> onHitEffectsRaw = CardSerializer.readCardEffects(id, onHitJson);
                    for(CardEffect effect : onHitEffectsRaw){
                        if(effect instanceof CardTargetEntityEffect cteEffect){
                            output.addEffectOnEntityHit(cteEffect);
                        }
                        else{
                            Battlecards.LOGGER.error("An onHit CardEffect in {} cannot target entity!",id);
                        }
                    }
                }
                return output;
            } catch (Exception e) {
                Battlecards.LOGGER.error("Error parsing melee effect!");
                return new MeleeEffect();
            }
        }
    }
}
