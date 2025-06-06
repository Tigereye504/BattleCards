package net.tigereye.mods.battlecards.client.Models;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.BoosterPacks.Json.BoosterPackManager;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class GeneratedBoosterPackBakedModel implements FabricBakedModel, BakedModel {

    private static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT,Identifier.of(Battlecards.MODID,"item/booster_pack"));
    private static final Identifier DEFAULT_MODEL_ID = new Identifier(Battlecards.MODID,"battlecard/booster_pack");
    public GeneratedBoosterPackBakedModel() {

    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        if(stack.hasNbt()) {
            Identifier cardID = new Identifier(stack.getNbt().getString(BoosterPackManager.ID_NBTKEY));
            Identifier modifiedCardID = new Identifier(cardID.getNamespace(),"battlecard/"+cardID.getPath());
            BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModelManager().getModel(modifiedCardID);
            if (model == null) {
                model = MinecraftClient.getInstance().getItemRenderer().getModels().getModelManager().getModel(DEFAULT_MODEL_ID);
            }
            if (model != null) {
                model.emitItemQuads(stack, randomSupplier, context);
            }
        }
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {}

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }


    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return DEFAULT_SPRITE_ID.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}