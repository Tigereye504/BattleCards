package net.tigereye.mods.battlecards.Cards.Json.CardEffectSerializers;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.CardEffects.interfaces.CardEffect;

public interface CardEffectSerializer {
    CardEffect readFromJson(Identifier id, JsonElement entry);
}
