package org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.impl;

import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.move_task.MoveTaskHandler;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.PassiveAction;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.DaxLogger;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.MoveTask;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.ActionResult;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.MoveActionResult;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.AccurateMouse;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.GenericUtil;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.path.DaxPathFinder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultObjectHandler implements MoveTaskHandler, DaxLogger {

    private static final List<String> MATCHES = Arrays.asList("Open", "Climb", "Climb-down", "Climb-up");

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        log("Starting...");

        if (!moveTask.getDestination().isInteractable()) {
            if (!AccurateMouse.clickMinimap(moveTask.getDestination())) {
                return MoveActionResult.FAILED;
            }

            if (!waitForConditionOrNoMovement(() -> DaxPathFinder.distance(moveTask.getDestination()) < 6, 15000, passiveActionList).isSuccess()) {
                log("We did not reach our destination.");
                return MoveActionResult.FAILED;
            }
        }

        GameObject closest = getClosest(moveTask);
        if (closest == null) {
            log("Failed to grab closest object to handle.");
            return MoveActionResult.FAILED;
        }

        log("We are interacting with " + GenericUtil.getName(closest));
        if (!handle(moveTask, closest, MATCHES)) {
            log("Failed to interact with closest object.");
            return MoveActionResult.FAILED;
        }

        ActionResult actionResult = waitForConditionOrNoMovement(() -> DaxPathFinder.canReach(moveTask.getNext()), 15000, passiveActionList);
        log("Interaction result: " + actionResult);
        if (!actionResult.isSuccess()) {
            log("Action resulted in failed state.");
            return MoveActionResult.FAILED;
        }

        return MoveActionResult.SUCCESS;
    }

    private GameObject getClosest(MoveTask moveTask) {
        GameObject[] objects = getValid(moveTask);
        return objects.length > 0 ? objects[0] : null;
    }

    private GameObject[] getValid(MoveTask moveTask) {
        GameObject[] objects = GameObjects.all((o) -> o.distance() <= 15 && Arrays.stream(o.getActions()).anyMatch(MATCHES::contains)).toArray(GameObject[]::new);

        if (getDirection(moveTask) == Direction.UP) {
            log("This object is leading us upwards.");
            objects = Arrays.stream(objects).filter(object -> GenericUtil.getActions(object).stream().anyMatch(s -> s.toLowerCase().contains("up")))
                    .toArray(GameObject[]::new);
        }

        if (getDirection(moveTask) == Direction.DOWN) {
            log("This object is leading us downwards.");
            objects = Arrays.stream(objects).filter(object -> GenericUtil.getActions(object).stream().anyMatch(s -> s.toLowerCase().contains("down")))
                    .toArray(GameObject[]::new);
        }

        Arrays.sort(objects, Comparator.comparingDouble(o -> o.getTile().distance(moveTask.getDestination())));
        return objects;
    }

    private boolean handle(MoveTask moveTask, GameObject object, List<String> actions) {
        return handle(moveTask, object, actions.toArray(new String[0]));
    }

    private boolean handle(MoveTask moveTask, GameObject object, String... action) {
//        if (!object.isOnScreen() || !object.isInteractable()) {
//            DaxCamera.focus(object);
//        }

        String[] clickActions = action;

        if (getDirection(moveTask) == Direction.UP) {
            clickActions = GenericUtil.getActions(object).stream().filter(s -> s.toLowerCase().contains("up")).toArray(
		            String[]::new);
        }

        if (getDirection(moveTask) == Direction.DOWN) {
            clickActions = GenericUtil.getActions(object).stream().filter(s -> s.toLowerCase().contains("down")).toArray(
		            String[]::new);
        }

        log(String.format("Clicking %s with %s", GenericUtil.getName(object), Arrays.toString(clickActions)));
        return AccurateMouse.click(object, clickActions);
    }

    private Direction getDirection(MoveTask moveTask) {
        int playerPlane = Players.localPlayer().getTile().getZ();
        int plane = moveTask.getNext() != null ? moveTask.getNext().getZ() : playerPlane;
        if (plane > playerPlane) return Direction.UP;
        if (plane < playerPlane) return Direction.DOWN;
        return Direction.SAME_FLOOR;
    }

    private enum Direction {
        UP,
        DOWN,
        SAME_FLOOR
    }
}
