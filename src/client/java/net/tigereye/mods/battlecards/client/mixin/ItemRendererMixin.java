package net.tigereye.mods.battlecards.client.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.GeneratedBattleCard;
import net.tigereye.mods.battlecards.Items.BattleCardItem;
import net.tigereye.mods.battlecards.Items.GeneratedCardItem;
import net.tigereye.mods.battlecards.client.CardRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class ItemRendererMixin {

    //@Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"),method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getItemCooldownManager()Lnet/minecraft/entity/player/ItemCooldownManager;"),method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void spellboundItemRendererItemOverlayMixin(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci){
        if(stack.getItem() instanceof GeneratedCardItem gci) {
            CardRenderer.RenderItemOverlay((DrawContext)(Object)this,stack,gci,x,y);
        }
    }
}
