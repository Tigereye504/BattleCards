package net.tigereye.mods.battlecards.CardEffects.delivery;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.RetainCardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class MeleeEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> onEntityHitEffects = new ArrayList<>();
    double reach = 3.5;
    boolean retainOnMiss = true;

    public void addEffectOnEntityHit(CardEffect effect){
        onEntityHitEffects.add(effect);
    }
    public void addEffectsOnEntityHit(List<CardEffect> effects){
        onEntityHitEffects.addAll(effects);
    }

    @Override
    public void apply(Entity user, ItemStack item, BattleCard battleCard, CardEffectContext context) {
        //TODO: raycast out to reach (configured, by default use the user's reach)
        Vec3d boxMin = user.getEyePos().add(-reach,-reach,-reach);
        Vec3d boxMax = user.getEyePos().add(reach,reach,reach);
        EntityHitResult ehr = ProjectileUtil.raycast(user,
                user.getEyePos(),
                user.getEyePos().add(user.getRotationVec(1).multiply(reach)),
                user.getBoundingBox().stretch(user.getRotationVec(1.0f).multiply(reach)).expand(reach), entity -> entity != user,reach*reach);
        if(ehr != null){
            if(user instanceof PlayerEntity pEntity){
                pEntity.swingHand(pEntity.getActiveHand());
            }
            CardEffectContext onHitContext = new CardEffectContext();
            onHitContext.target = ehr.getEntity();
            for (CardEffect effect : onEntityHitEffects){
                effect.apply(user,item,battleCard,onHitContext);
            }
        }
        else if(retainOnMiss){
            new RetainCardEffect().apply(user,item,battleCard,context);
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.melee")));
        if(!onEntityHitEffects.isEmpty()){
            tooltip.add(Text.literal(" ".repeat(depth)).append(
                    Text.translatable("card.battlecards.tooltip.on_hit")));
            for(CardEffect effect : onEntityHitEffects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public MeleeEffect readFromJson(Identifier id, JsonElement entry) {
            MeleeEffect output = new MeleeEffect();
            output.reach = CardSerializer.readOrDefaultFloat(id,"reach",entry,3.5f);
            output.retainOnMiss = CardSerializer.readOrDefaultBoolean(id,"retainOnMiss",entry,true);
            output.addEffectsOnEntityHit(CardSerializer.readCardEffects(id, "onHit",entry));
            if (output.onEntityHitEffects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on melee hit in {}!",id);
            }
            return output;
        }
    }
}
