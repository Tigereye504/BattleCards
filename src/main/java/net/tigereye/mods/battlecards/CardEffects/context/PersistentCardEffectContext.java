package net.tigereye.mods.battlecards.CardEffects.context;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.BattleCard;
import net.tigereye.mods.battlecards.Events.DamageCardEffectCallback;
import net.tigereye.mods.battlecards.Events.ManaGainCardEffectCallback;
import net.tigereye.mods.battlecards.Events.StatusEffectCardEffectCallback;

import java.util.ArrayList;
import java.util.List;

public class PersistentCardEffectContext {
    public Entity user = null;
    public BattleCard card = null;
    public ItemStack cardItem = null;
    public List<DamageCardEffectCallback> modifyDamageCallbacks = new ArrayList<>();
    public List<StatusEffectCardEffectCallback> modifyStatusEffectCallbacks = new ArrayList<>();
    public List<ManaGainCardEffectCallback> manaGainCallbacks = new ArrayList<>();
    //other listeners as needed
    public PersistentCardEffectContext(Entity user, BattleCard card, ItemStack cardItem){
        this.user = user;
        this.card = card;
        this.cardItem = cardItem;
    }
}
