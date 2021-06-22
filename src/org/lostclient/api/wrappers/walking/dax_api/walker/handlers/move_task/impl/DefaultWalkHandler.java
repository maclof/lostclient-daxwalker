package org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.impl;

import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.MoveTaskHandler;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.PassiveAction;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.MoveTask;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.MoveActionResult;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.AccurateMouse;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.path.DaxPathFinder;

import java.util.List;

public class DefaultWalkHandler implements MoveTaskHandler {

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        if (!AccurateMouse.clickMinimap(moveTask.getDestination())) {
            return MoveActionResult.FAILED;
        }
        int initialDistance = DaxPathFinder.distance(moveTask.getDestination());

        if (!waitFor(() -> {
            int currentDistance = DaxPathFinder.distance(moveTask.getDestination());
            return currentDistance <= 2 || initialDistance - currentDistance > getDistanceOffset();
        }, 3500, passiveActionList).isSuccess()) {
            return MoveActionResult.FAILED;
        }

        return MoveActionResult.SUCCESS;
    }

    private int getDistanceOffset() {
        return Options.isRunEnabled() ? Calculations.randomSD(3, 10, 7, 2) : Calculations.randomSD(2, 10, 5, 2);
    }

}
