package net.tigereye.mods.battlecards.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.tigereye.mods.battlecards.registration.BCEntities;

public class BattlecardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BCEntities.CardProjectileEntityType, FlyingItemEntityRenderer::new);
    }
}
