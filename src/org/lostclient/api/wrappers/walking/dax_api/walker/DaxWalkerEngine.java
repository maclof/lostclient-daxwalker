package org.lostclient.api.wrappers.walking.dax_api.walker;

import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.impl.DefaultObjectHandler;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.impl.DefaultWalkHandler;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.PassiveAction;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.special_cases.SpecialCaseHandler;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.special_cases.SpecialCaseHandlers;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.DaxLogger;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.MoveTask;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.MoveActionResult;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.Situation;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.path.DaxPathFinder;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.path.PathUtils;

import java.util.ArrayList;
import java.util.List;

public class DaxWalkerEngine implements DaxLogger {

    private List<PassiveAction> passiveActions;

    public DaxWalkerEngine() {
        passiveActions = new ArrayList<>();
    }

    public void addPassiveAction(PassiveAction passiveAction) {
        passiveActions.add(passiveAction);
    }

    public List<PassiveAction> getPassiveActions() {
        return passiveActions;
    }

    public boolean walkPath(List<Tile> path) {
        int failAttempts = 0;

        while (failAttempts < 3) {
            MoveActionResult moveActionResult = walkNext(path);
            if (reachedEnd(path)) return true;
            if (moveActionResult == MoveActionResult.FATAL_ERROR) break;
            if (moveActionResult == MoveActionResult.SUCCESS) {
                failAttempts = 0;
            } else {
                log(String.format("Failed action [%d]", ++failAttempts));
            }
        }

        return false;
    }

    private boolean reachedEnd(List<Tile> path) {
        if (path == null || path.size() == 0) return true;
        Tile tile = Game.getDestination();
        return tile != null && tile.equals(path.get(path.size() - 1));
    }

    private MoveActionResult walkNext(List<Tile> path) {
        MoveTask moveTask = determineNextAction(path);
        debug("Move task: " + moveTask);

        SpecialCaseHandler specialCaseHandler = SpecialCaseHandlers.getSpecialCaseHandler(moveTask);
        if (specialCaseHandler != null) {
            log(String.format("Overriding normal behavior with special handler: %s", specialCaseHandler.getName()));
            return specialCaseHandler.handle(moveTask, passiveActions);
        }

        switch (moveTask.getSituation()) {

            case COLLISION_BLOCKING:
            case DISCONNECTED_PATH:
                return new DefaultObjectHandler().handle(moveTask, passiveActions);

            case NORMAL_PATH_HANDLING:
                return new DefaultWalkHandler().handle(moveTask, passiveActions);

            case PATH_TOO_FAR:

            default:
                return MoveActionResult.FAILED;
        }
    }

    private MoveTask determineNextAction(List<Tile> path) {
        Tile furthestInteractable = PathUtils.getFurthestReachableTileInMinimap(path);
        if (furthestInteractable == null) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        Tile next;
        try {
            next = PathUtils.getNextTileInPath(furthestInteractable, path);
        } catch (PathUtils.NotInPathException e) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        if (next != null) {
            if (furthestInteractable.distanceDouble(next) >= 2D) {
                return new MoveTask(Situation.DISCONNECTED_PATH, furthestInteractable, next);
            }

            if (!DaxPathFinder.canReach(next)) {
                return new MoveTask(Situation.COLLISION_BLOCKING, furthestInteractable, next);
            }
        }

        return new MoveTask(Situation.NORMAL_PATH_HANDLING, furthestInteractable, next);
    }


}
