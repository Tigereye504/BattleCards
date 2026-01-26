package net.tigereye.mods.battlecards.client.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Items.interfaces.BattleCardItem;
import net.tigereye.mods.battlecards.registration.BCStatusEffects;

public class BCGUI {

    public static final Identifier MANA_TEXTURE = new Identifier(Battlecards.MODID,"textures/gui/mana.png");
    private static final int MAX_MANA = 10;
    private static final int Y_OFFSET = 30;

    public static void register(){
        HudRenderCallback.EVENT.register(BCGUI::renderMana);
    }


    public static void renderMana(DrawContext drawContext, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null) {
            ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);
            BattleCardItem cardItem = null;
            if(heldItem.getItem() instanceof BattleCardItem asCard){
                cardItem = asCard;
            }
            else {
                heldItem = player.getStackInHand(Hand.OFF_HAND);
                if(heldItem.getItem() instanceof BattleCardItem asCard){
                    cardItem = asCard;
                }
            }

            if(cardItem != null) {
                int manaCost = cardItem.getChargeEffectCost(player, heldItem,true);
                client.getProfiler().push("health");
                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();

                int mana = player.hasStatusEffect(BCStatusEffects.MANA)
                        ? player.getStatusEffect(BCStatusEffects.MANA).getAmplifier() + 1 : 0;

                //middle of the screen, offset left by half of max mana pips (40 pixels)
                int x = scaledWidth / 2 - (MAX_MANA * 4);
                //over the xp meter, lined up with the hearts
                int y = scaledHeight - Y_OFFSET;

                RenderSystem.enableBlend();
                for (int i = 0; i < MAX_MANA; i++) {

                    int posX = x + i * 8;
                    if(i < manaCost) {
                        drawContext.drawTexture(MANA_TEXTURE, posX, y, i < mana ? 0 : 8, 0, 8, 8, 32, 8);
                    }else{
                        drawContext.drawTexture(MANA_TEXTURE, posX, y, i < mana ? 16 : 24, 0, 8, 8, 32, 8);
                    }
                }
                client.getProfiler().pop();
            }
        }
    }
}
