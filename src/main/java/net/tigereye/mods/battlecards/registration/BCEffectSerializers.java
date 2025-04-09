package net.tigereye.mods.battlecards.registration;

import net.tigereye.mods.battlecards.CardEffects.*;
import net.tigereye.mods.battlecards.CardEffects.delivery.EntitiesInRadiusEffect;
import net.tigereye.mods.battlecards.CardEffects.delivery.MeleeEffect;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardEffect;
import net.tigereye.mods.battlecards.CardEffects.modifiers.DelayedEffect;
import net.tigereye.mods.battlecards.CardEffects.modifiers.HungerScalerEffect;
import net.tigereye.mods.battlecards.CardEffects.modifiers.IfStatusEffect;
import net.tigereye.mods.battlecards.CardEffects.modifiers.TargetUserEffect;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardsEffect;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

public class BCEffectSerializers {

    public static void register(){
        //delivery methods
        CardSerializer.registerCardEffectSerializer("melee",new MeleeEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_card",new ThrowCardEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_cards",new ThrowCardsEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("entities_in_radius",new EntitiesInRadiusEffect.Serializer());
        //modifiers
        CardSerializer.registerCardEffectSerializer("delay",new DelayedEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("hunger_scaler",new HungerScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("if_status",new IfStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("target_user",new TargetUserEffect.Serializer());
        //payouts
        CardSerializer.registerCardEffectSerializer("apply_status",new ApplyStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("clear_status",new ClearStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("damage",new DamageEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("gain_mana",new GainManaEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("lifesteal",new LifestealEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_breath",new ModifyBreathEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_health",new ModifyHealthEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_hunger",new ModifyHungerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("push",new PushEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("transfer_status",new TransferStatusEffect.Serializer());
    }
}
