package net.tigereye.mods.battlecards.Mixin;

import net.minecraft.entity.Entity;
import net.tigereye.mods.battlecards.Util.DelayedAction;
import net.tigereye.mods.battlecards.Util.DelayedActionTaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements DelayedActionTaker {
    @Unique
    private final List<DelayedAction> delayedActions = new LinkedList<>();
    @Unique
    private final List<DelayedAction> delayedActionsQueue = new LinkedList<>();
    @Unique
    private boolean performingDelayedActions = false;

    @Unique
    public void battleCards$addDelayedAction(DelayedAction action){
        if (performingDelayedActions)
            delayedActionsQueue.add(action);
        else
            delayedActions.add(action);
    }

    @Unique
    public List<DelayedAction> battleCards$getDelayedActions(){
        return delayedActions;
    }

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void BattlecardsLivingEntityBaseTickMixin(CallbackInfo info) {
        performingDelayedActions = true;
        for (DelayedAction action : delayedActions) {
            if (action.actOrDecrementTicks()) {
                delayedActionsQueue.add(action);
            }
        }
        delayedActions.clear();
        delayedActions.addAll(delayedActionsQueue);
        delayedActionsQueue.clear();
        performingDelayedActions = false;
    }
}
