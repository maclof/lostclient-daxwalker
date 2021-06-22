package org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling;

import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.accessor.GroundItems;
import org.lostclient.api.accessor.NPCs;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.interfaces.Interactable;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.camera.Camera;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.interactives.Player;
import org.lostclient.api.wrappers.item.GroundItem;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.Walking;
import org.lostclient.api.wrappers.walking.dax_api.walker.utils.AccurateMouse;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;

import java.util.function.Predicate;


public class InteractionHelper {

    public static boolean click(Interactable clickable, String... actions){
        return click(clickable, actions, null);
    }

    public static boolean click(Interactable clickable, String action, WaitFor.Condition condition){
        return click(clickable, new String[]{action}, condition);
    }

    /**
     * Interacts with nearby object and waits for {@code condition}.
     *
     * @param clickable clickable entity
     * @param actions actions to click
     * @param condition condition to wait for after the click action
     * @return if {@code condition} is null, then return the outcome of condition.
     *          Otherwise, return the result of the click action.
     */
    public static boolean click(Interactable clickable, String[] actions, WaitFor.Condition condition){
        if (clickable == null){
            return false;
        }

        if (clickable instanceof Item){
            if (actions.length > 1) {
                MethodProvider.logDebug("Only clicking first action on item!");
            }
            return ((Item) clickable).interact(actions[0]) && (condition == null || WaitFor.condition(Calculations.random(7000, 8000), condition) == WaitFor.Return.SUCCESS);
        }

        Tile position = ((Locatable) clickable).getTile();

        if (!isOnScreenAndInteractable(clickable)){
            Walking.setWalkFlag(position);
        }

        WaitFor.Return result = WaitFor.condition(WaitFor.getMovementRandomSleep(position), new WaitFor.Condition() {
            final long startTime = System.currentTimeMillis();
            @Override
            public WaitFor.Return active() {
                if (isOnScreenAndInteractable(clickable)){
                    return WaitFor.Return.SUCCESS;
                }
                if (MethodProvider.timeFromMark(startTime) > 2000 && !Players.localPlayer().isMoving()){
                    return WaitFor.Return.FAIL;
                }
                return WaitFor.Return.IGNORE;
            }
        });

        if (result != WaitFor.Return.SUCCESS){
            return false;
        }

        if (!AccurateMouse.click(clickable, actions)){
            if (Camera.getAngle() < 90){
//                Camera.setCameraAngle(Calculations.random(90, 100));
            }
            return false;
        }

        return condition == null || WaitFor.condition(Calculations.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static Item getItem(Predicate<Item> predicate){
        return Inventory.get(predicate);
    }

    public static NPC getNPC(Predicate<NPC> predicate){
        return NPCs.closest(predicate);
    }

    public static GameObject getGameObject(Predicate<GameObject> predicate){
        return GameObjects.closest(predicate);
    }

    public static GroundItem getGroundItem(Predicate<GroundItem> predicate){
        return GroundItems.closest(predicate);
    }

    public static boolean focusCamera(Interactable clickable){
        if (clickable == null){
            return false;
        }
        if (isOnScreenAndInteractable(clickable)){
            return true;
        }
        Tile tile = ((Locatable) clickable).getTile();
        Camera.turnTo(tile);
//        Camera.setCameraAngle(100 - (tile.distance(Players.localPlayer().getTile()) * 4));
        return isOnScreenAndInteractable(clickable);
    }

    private static boolean isOnScreenAndInteractable(Interactable clickable){
//        if (clickable instanceof Player && !((Player) clickable).isOnScreen()){
//            return false;
//        }
//        if (clickable instanceof GameObject && !((GameObject) clickable).isOnScreen()){
//            return false;
//        }
//        if (clickable instanceof GroundItem && !((GroundItem) clickable).isOnScreen()){
//            return false;
//        }
//        return clickable.isInteractable();
        return true;
    }


}
