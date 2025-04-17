package net.tigereye.mods.battlecards.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.tigereye.mods.battlecards.client.Models.GeneratedBattlecardModelLoadingPlugin;
import net.tigereye.mods.battlecards.client.Projectiles.CardProjectileRenderer;
import net.tigereye.mods.battlecards.client.Render.BCGUI;
import net.tigereye.mods.battlecards.registration.BCEntities;

public class BattlecardsClient implements ClientModInitializer {

    //public static ClientCardManager CLIENT_CARD_MANAGER = new ClientCardManager();

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new GeneratedBattlecardModelLoadingPlugin());
        EntityRendererRegistry.register(BCEntities.CardProjectileEntityType, CardProjectileRenderer::new);
        BCGUI.register();
    }
}
