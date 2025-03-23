package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Items.BasicTestCard;

public class BCItems {
    public static final Item BASIC_CARD = new BasicTestCard(new FabricItemSettings().maxCount(1)) {
    };

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "basic_card"), BASIC_CARD);
    }
}
