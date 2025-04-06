package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Cards.Json.CardManager;
import net.tigereye.mods.battlecards.Items.GeneratedCardItem;

import java.util.EnumSet;
import java.util.Set;

public class BCItems {
    public static final Item BATTLECARD = new GeneratedCardItem(new FabricItemSettings().maxCount(64));

    public static final ItemGroup BATTLECARDS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(BATTLECARD))
            .displayName(Text.translatable("itemgroup.battlecards.battlecards"))
            .entries(((displayContext, entries) -> {
                CardManager.GeneratedCards.forEach((id,battlecard) -> {
                    entries.add(CardManager.generateCardItemstack(id));
                });
            }))
            .build();

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Battlecards.MODID, "battlecard"), BATTLECARD);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(Battlecards.MODID,"battlecards_item_group"), BATTLECARDS_ITEM_GROUP);
    }
}
