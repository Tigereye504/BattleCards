package net.tigereye.mods.battlecards.registration;

import net.tigereye.mods.battlecards.CardEffects.*;
import net.tigereye.mods.battlecards.CardEffects.delivery.EntitiesInRadiusEffect;
import net.tigereye.mods.battlecards.CardEffects.delivery.MeleeEffect;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardEffect;
import net.tigereye.mods.battlecards.CardEffects.modifiers.*;
import net.tigereye.mods.battlecards.CardEffects.delivery.ThrowCardsEffect;
import net.tigereye.mods.battlecards.CardEffects.scalar.*;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

public class BCEffectSerializers {

    public static void register(){
        //delivery methods
        CardSerializer.registerCardEffectSerializer("melee",new MeleeEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_card",new ThrowCardEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_cards",new ThrowCardsEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("entities_in_radius",new EntitiesInRadiusEffect.Serializer());
        //modifiers
        CardSerializer.registerCardEffectSerializer("if_grounded",new IfGroundedEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("if_status",new IfStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("if_scalar",new IfScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("target_user",new TargetUserEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("target_nearest_entity",new TargetNearestEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("target_tracked_entity",new TargetTrackedEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("repeat",new RepeatEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("delay",new DelayedEffect.Serializer());
        //scalars
        CardSerializer.registerCardEffectSerializer("constant_scalar",new ConstantScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("addition_scalar",new AdditionScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("bounding_scalar",new BoundingScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("multiplication_scalar",new MultiplicationScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("absorption_scalar",new AbsorptionScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("health_scalar",new HealthScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("hunger_scalar",new HungerScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("random_scalar",new RandomScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("distance_to_target_scalar",new DistanceToTargetScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("yaw_to_target_scalar",new YawToTargetScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("pitch_to_target_scalar",new PitchToTargetScalarEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("x_scalar",new XScalerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("status_magnitude_scalar",new StatusLevelScalarEffect.Serializer());
        //payouts
        CardSerializer.registerCardEffectSerializer("apply_status",new ApplyStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("clear_status",new ClearStatusEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("damage",new DamageEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("gain_mana",new GainManaEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("lifesteal",new LifestealEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_absorption",new ModifyAbsorptionEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_breath",new ModifyBreathEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_health",new ModifyHealthEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("modify_hunger",new ModifyHungerEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("push",new PushEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("retain",new RetainCardEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("transfer_status",new TransferStatusEffect.Serializer());
    }
}
