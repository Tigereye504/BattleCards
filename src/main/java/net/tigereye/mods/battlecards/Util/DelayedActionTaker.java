package net.tigereye.mods.battlecards.Util;

import java.util.List;

public interface DelayedActionTaker {
    void battleCards$addDelayedAction(DelayedAction action);
    List<DelayedAction> battleCards$getDelayedActions();
}
