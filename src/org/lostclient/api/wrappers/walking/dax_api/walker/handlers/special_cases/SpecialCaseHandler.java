package org.lostclient.api.wrappers.walking.dax_api.walker.handlers.special_cases;

import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.PassiveAction;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.MoveTask;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.MoveActionResult;

import java.util.List;

public interface SpecialCaseHandler {

    boolean shouldHandle(MoveTask moveTask);

    MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList);

    default String getName() {
        return this.getClass().getSimpleName();
    }

}
