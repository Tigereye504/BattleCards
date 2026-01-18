package net.tigereye.mods.battlecards.registration;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Battlecards;
import net.tigereye.mods.battlecards.StatusEffects.*;

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
    public static StatusEffect SUNDERED = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)
            .addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "ef9caf19-7ee1-4ee2-9ed9-76a13e3e56a9",
                    -4F, EntityAttributeModifier.Operation.ADDITION);
    public static StatusEffect EVADING = new Evading();
    public static StatusEffect HIGHSTEP = new HighStep();
    public static StatusEffect OVERDRAW = new Overdraw();
    public static StatusEffect WEBWALKING = new BCStatusEffect(StatusEffectCategory.BENEFICIAL, 0xDDDDDD);

    public static void register(){
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "mana"), MANA);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "undeath"), UNDEATH);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "undying"), UNDYING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "armored"), ARMORED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "reinforced"), REINFORCED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "evading"), EVADING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "highstep"), HIGHSTEP);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "sundered"), SUNDERED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "overdraw"), OVERDRAW);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Battlecards.MODID, "webwalking"), WEBWALKING);
    }
}
