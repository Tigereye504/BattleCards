package net.tigereye.mods.battlecards.CardEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.tigereye.mods.battlecards.Items.BattleCard;

public class DamageCardEffect implements CardEffect {

    private int damage;
    private RegistryKey<DamageType> damageType;

    public DamageCardEffect(int damage, RegistryKey<DamageType> damageType){
        this.damage = damage;
        this.damageType = damageType;
    }

    public DamageCardEffect(int damage, Identifier damageType){
        this.damage = damage;
        this.damageType = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,damageType);
    }
    @Override
    public void apply(LivingEntity user, LivingEntity target, BattleCard battleCard) {
        //TODO: apply enchantments (perhaps with a hook or event for better compatibility?)
        //TODO: apply sleeve modifiers (using an event for this sounds better and better)
        target.damage(target.getDamageSources().create(damageType,user),damage);
    }
}
