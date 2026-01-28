package net.tigereye.mods.battlecards.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.client.Models.GeneratedBattlecardModelLoadingPlugin;
import net.tigereye.mods.battlecards.client.Projectiles.CardProjectileRenderer;
import net.tigereye.mods.battlecards.client.Render.BCGUI;
import net.tigereye.mods.battlecards.registration.BCEntities;
import net.tigereye.mods.battlecards.registration.BCItems;

public class BattlecardsClient implements ClientModInitializer {

    //public static ClientCardManager CLIENT_CARD_MANAGER = new ClientCardManager();

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new GeneratedBattlecardModelLoadingPlugin());
        EntityRendererRegistry.register(BCEntities.CardProjectileEntityType, CardProjectileRenderer::new);
        BCGUI.register();
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if(tintIndex == 0) {
                NbtCompound display = stack.getSubNbt("display");
                return display != null ? display.getInt("color") : 0xFFFFFF;
            }
            return 0xFFFFFF;
        }, BCItems.DECK);
        ModelPredicateProviderRegistry.register(BCItems.DECK, Identifier.of("battlecards","deck_state"), (itemStack, clientWorld, livingEntity, seed) -> {
            if(itemStack.hasNbt()) {
                if (itemStack.getNbt().contains("hotbar_storage")) {
                    return 0.1F; //active
                }
                if (itemStack.getNbt().contains("deck_drawpile")) {
                    return 0.2F; //paused
                }
            }
            return 0.0F; //inactive
        });
    }
}
