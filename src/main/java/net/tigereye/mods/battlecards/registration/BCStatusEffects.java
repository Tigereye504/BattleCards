package net.tigereye.mods.battlecards.registration;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.StatusEffects.BCStatusEffect;
import net.tigereye.mods.battlecards.StatusEffects.Undying;

public class BCStatusEffects {

    public static StatusEffect MANA = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 0x008080);
    public static StatusEffect UNDEATH = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);
    public static StatusEffect UNDYING = new Undying();
    public static StatusEffect ARMORED = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)
                    .addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "a2fb047c-a903-4763-9573-6626f33dd65c",
                            4F, EntityAttributeModifier.Operation.ADDITION);
    public static StatusEffect REINFORCED = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)
            .addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, "f5ff0ee8-28f2-4361-9d25-130bdf515375",
                    2F, EntityAttributeModifier.Operation.ADDITION);

    public static void register(){
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "mana"), MANA);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "undeath"), UNDEATH);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "undying"), UNDYING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "armored"), ARMORED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "reinforced"), REINFORCED);

    }
}
