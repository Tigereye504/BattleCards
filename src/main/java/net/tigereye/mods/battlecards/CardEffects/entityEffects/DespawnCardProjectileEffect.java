package net.tigereye.mods.battlecards.CardEffects.entityEffects;

import com.google.gson.JsonElement;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.CardEffects.context.CardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.context.PersistentCardEffectContext;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardTooltipNester;
import net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers.CardEffectSerializer;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

import java.util.List;

public class DespawnCardProjectileEffect implements CardEffect, CardTooltipNester {
    @Override
    public void apply(PersistentCardEffectContext pContext, CardEffectContext context) {
        if(context.target instanceof CardProjectileEntity){
            context.target.discard();
        }
        if(context.trackedEntity instanceof CardProjectileEntity){
            context.trackedEntity.discard();
        }
    }

    public void appendNestedTooltip(World world, List<Text> tooltip, TooltipContext tooltipContext, int depth) {
        tooltip.add(Text.literal(" ".repeat(depth)).append(
                Text.translatable("card.battlecards.tooltip.despawn_card_projectile")));
    }

    public static class Serializer implements CardEffectSerializer {
        @Override
        public DespawnCardProjectileEffect readFromJson(Identifier id, JsonElement entry) {
            return new DespawnCardProjectileEffect();
        }
    }
}
