package net.tigereye.mods.battlecards.client.Cards.json;

import net.minecraft.util.Identifier;

public interface BattleCardClientData {
    public Identifier getArt();
    public void setArt(Identifier texture);
    public Identifier getIcon();
    public void setIcon(Identifier texture);
    public Identifier getBackground();
    public void setBackground(Identifier texture);
}
