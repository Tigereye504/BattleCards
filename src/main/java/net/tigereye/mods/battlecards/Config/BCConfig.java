package net.tigereye.mods.battlecards.Config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.tigereye.mods.battlecards.Battlecards;

@Config(name = Battlecards.MODID)
public class BCConfig implements ConfigData {
    public float BOOSTER_PACK_BASE_UPGRADE_CHANCE = 0.05f;
    public float BOOSTER_PACK_LUCK_SCALING_UPGRADE_CHANCE = 0.05f;
}
