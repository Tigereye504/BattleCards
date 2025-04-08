package net.tigereye.mods.battlecards.registration;

import net.tigereye.mods.battlecards.CardEffects.*;
import net.tigereye.mods.battlecards.Cards.Json.CardSerializer;

public class BCEffectSerializers {

    public static void register(){
        //delivery methods
        CardSerializer.registerCardEffectSerializer("melee",new MeleeEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_card",new ThrowCardEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("throw_cards",new ThrowCardsEffect.Serializer());
        //transformers
        CardSerializer.registerCardEffectSerializer("target_user",new TargetUserEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("delay_entity",new DelayedEntityEffect.Serializer());
        //payouts
        CardSerializer.registerCardEffectSerializer("heal",new HealEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("damage",new DamageEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("lifesteal",new LifestealEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("apply_status",new ApplyStatusEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("clear_status",new ClearStatusEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("transfer_status",new TransferStatusEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("push",new PushEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("hunger",new HungerEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("breath",new BreathEntityEffect.Serializer());
        CardSerializer.registerCardEffectSerializer("gain_mana",new GainManaEffect.Serializer());
    }
}
