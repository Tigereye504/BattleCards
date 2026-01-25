package net.tigereye.mods.battlecards.CardEffects.delivery;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.CardEffects.scalar.ConstantScalarEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.CardScalar;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

import java.util.ArrayList;
import java.util.List;

public class EntitiesInRadiusEffect implements CardEffect, CardTooltipNester {

    List<CardEffect> effects = new ArrayList<>();
    CardScalar radius = new ConstantScalarEffect(1);
    boolean targetUser = false;
    boolean sphereElseCylinder = false;

    public void addEffectOnEntityInRange(CardEffect effect){
        effects.add(effect);
    }
    public void addEffectsOnEntityInRange(List<CardEffect> effects){
        this.effects.addAll(effects);
    }

    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if (context.target != null) {
            apply(pContext, context, context.target);
        }
        else if(context.hitResult != null){
            apply(pContext, context, context.hitResult.getPos());
        }
        else {
            apply(pContext, context, pContext.user);
        }
    }

    private void apply(PersistentCardEffectContext pContext, CardEffectContext context, Entity target) {
        apply(pContext, context, target.getEyePos());
    }

    private void apply(PersistentCardEffectContext pContext, CardEffectContext context, Vec3d center) {
        float radius = this.radius.getValue(pContext,context);
        Box box = new Box(center,center).expand(radius);
        List<LivingEntity> entityList = pContext.user.getWorld().getNonSpectatingEntities(LivingEntity.class,
                box);
        double radiusSquared = radius*radius;
        for(LivingEntity entity: entityList){
            if(!targetUser && entity == pContext.user){
                continue;
            }
            boolean inRange = false;
            if(sphereElseCylinder && entity.squaredDistanceTo(center) < radiusSquared){
                inRange = true;
            }
            else if(!sphereElseCylinder){
                double diffX = entity.getX()-center.getX();
                double diffY = entity.getY()-center.getY();
                inRange = (diffX*diffX)+(diffY*diffY) < radiusSquared;
            }
            if(inRange){
                CardEffectContext newContext = context.clone();
                newContext.target = entity;
                for (CardEffect effect : effects) {
                    effect.apply(pContext, newContext);
                }
            }
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.radius",radius.appendInlineTooltip(world,tooltip,tooltipContext),
                        sphereElseCylinder ? "sphere" : "cylinder")));
        if(!effects.isEmpty()){
            for(CardEffect effect : effects){
                if(effect instanceof CardTooltipNester nester){
                    nester.appendNestedTooltip(world, tooltip, tooltipContext, depth+1);
                }
            }
        }
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public EntitiesInRadiusEffect readFromJson(Identifier id, JsonElement entry) {
            EntitiesInRadiusEffect output = new EntitiesInRadiusEffect();
            output.radius = CardSerializer.readOrDefaultScalar(id,"radius",entry,1f);
            output.targetUser = CardSerializer.readOrDefaultBoolean(id,"targetUser",entry,false);
            output.sphereElseCylinder = CardSerializer.readOrDefaultBoolean(id,"sphereElseCylinder",entry,false);
            output.addEffectsOnEntityInRange(CardSerializer.readCardEffects(id, "effects",entry));
            if (output.effects.isEmpty()) {
                Battlecards.LOGGER.error("no effects on entities in radius in {}!",id);
            }
            return output;
        }
    }
}
