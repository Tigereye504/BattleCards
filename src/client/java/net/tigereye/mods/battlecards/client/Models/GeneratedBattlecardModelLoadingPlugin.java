package net.tigereye.mods.battlecards.client.Models;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import java.util.Map;

public class GeneratedBattlecardModelLoadingPlugin implements ModelLoadingPlugin {
    public static final Identifier GENERATED_BATTLECARD_MODEL = Identifier.of(Battlecards.MODID,"battlecard");
    public static final String GENERATED_BATTLECARD_MODEL_LOCATION = "models/battlecard";
    public static Map<Identifier, Resource> loadedCardModels;
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        loadedCardModels = MinecraftClient.getInstance().getResourceManager()
                .findResources(GENERATED_BATTLECARD_MODEL_LOCATION, path -> path.getPath().endsWith(".json"));
        loadedCardModels.forEach((id,resource) -> {
            String trimmedPath = id.getPath().substring(7).split("\\.")[0];
            pluginContext.addModels(new Identifier(id.getNamespace(),trimmedPath));
        });
        pluginContext.addModels();
        pluginContext.modifyModelAfterBake().register((original, context) -> {
            if (GENERATED_BATTLECARD_MODEL.equals(context.id())) {
                return new GeneratedBattlecardBakedModel();
            }
            return original;
        });
    }
}
