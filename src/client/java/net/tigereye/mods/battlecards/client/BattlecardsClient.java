package net.tigereye.mods.battlecards.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.resource.ResourceType;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.client.Projectiles.CardProjectileRenderer;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

public class BattlecardsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BCEntities.CardProjectileEntityType, CardProjectileRenderer::new);
        //BuiltinItemRendererRegistry.INSTANCE.register(BCItems.BATTLECARD,new CardRenderer());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CardManager());
    }
}
