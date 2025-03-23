package net.tigereye.mods.battlecards.registration;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.Projectiles.CardProjectileEntity;

public class BCEntities {
    public static final EntityType<CardProjectileEntity> CardProjectileEntityType = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Battlecards.MODID, "card_projectile"),
            FabricEntityTypeBuilder.<CardProjectileEntity>create(SpawnGroup.MISC, CardProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
                    .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
                    .build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
    );

    public static void register(){}
}
