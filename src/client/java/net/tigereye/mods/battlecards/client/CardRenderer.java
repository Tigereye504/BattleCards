package net.tigereye.mods.battlecards.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ItemRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.BattleCardItem;
import net.tigereye.mods.battlecards.Items.GeneratedCardItem;
import net.tigereye.mods.battlecards.client.Cards.json.BattleCardClientData;
import net.tigereye.mods.battlecards.client.Cards.json.ClientCardManager;

public class CardRenderer /*implements BuiltinItemRendererRegistry.DynamicItemRenderer*/ {
    /*@Override
    public void render(ItemStack stack, ModelTransformationMode modelTransformationMode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        if(stack.hasNbt()) {
            Identifier cardID = new Identifier(stack.getNbt().getString(CardManager.NBT_KEY));
            BattleCard card = CardManager.getEntry(cardID);

        }
    }*/

    public static void RenderItemOverlay(net.minecraft.client.gui.DrawContext drawContext, ItemStack stack, GeneratedCardItem gci, int x, int y) {
        if(stack.hasNbt()) {
            Identifier cardID = new Identifier(stack.getNbt().getString(ClientCardManager.NBT_KEY));
             BattleCardClientData card = ClientCardManager.getEntry(cardID);
            if(card != null) {
                if (card.getBackground() != null) {
                    drawContext.drawTexture(card.getBackground(), x, y, 0, 0, 16, 16,16,16);
                }
                if (card.getArt() != null) {
                    drawContext.drawTexture(card.getArt(), x, y, 0, 0, 16, 16,16,16);
                }
                if (card.getIcon() != null) {
                    drawContext.drawTexture(card.getIcon(), x, y, 0, 0, 16, 16,16,16);
                }
            }
        }
    }
}
