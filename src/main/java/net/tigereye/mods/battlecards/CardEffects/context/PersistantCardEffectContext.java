package net.tigereye.mods.battlecards.CardEffects.context;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Events.EventListeners.ModifyDamageCardEffectListener;

import java.util.ArrayList;
import java.util.List;

public class PersistantCardEffectContext {
    //TODO: decide wither to use this... or perhaps something more event based
    public Entity user = null;
    public BattleCard card = null;
    public ItemStack cardItem = null;
    public List<ModifyDamageCardEffectListener> modifyDamageListeners = new ArrayList<>();
    //other listeners as needed

    public PersistantCardEffectContext(Entity user, BattleCard card, ItemStack cardItem){
        this.user = user;
        this.card = card;
        this.cardItem = cardItem;
    }
}
