package net.tigereye.mods.battlecards.CardEffects.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.tigereye.mods.battlecards.Cards.Json.BattleCard;
import net.tigereye.mods.battlecards.Items.BattleCardItem;

import java.util.Map;

public interface CardEffect {
    void apply(Entity user, ItemStack item, BattleCard battleCard);
    //void apply(Entity user, ItemStack item, BattleCard battleCard, Map<String,Object> args);

    /*default void apply(Entity user, Entity target, ItemStack item, BattleCard battleCard){
        Map<String,Object> args = new HashMap<>();
        args.put("target",target);
        apply(user,item,battleCard,args);
    }

    default void apply(Entity user, HitResult hitResult, ItemStack item, BattleCard battleCard){
        Map<String,Object> args = new HashMap<>();
        args.put("hitResult",hitResult);
        apply(user,item,battleCard,args);
    }*/
}
