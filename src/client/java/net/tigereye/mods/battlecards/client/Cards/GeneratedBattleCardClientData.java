package net.tigereye.mods.battlecards.client.Cards;

import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.client.Cards.json.BattleCardClientData;

public class GeneratedBattleCardClientData implements BattleCardClientData {
    Identifier art;
    Identifier icon;
    Identifier background;

    public Identifier getArt() {
        return art;
    }
    public void setArt(Identifier texture) {
        this.art = texture;
    }
    public Identifier getIcon() {
        return icon;
    }
    public void setIcon(Identifier texture) {
        this.icon = texture;
    }
    public Identifier getBackground() {
        return background;
    }
    public void setBackground(Identifier texture) {
        this.background = texture;
    }
}
