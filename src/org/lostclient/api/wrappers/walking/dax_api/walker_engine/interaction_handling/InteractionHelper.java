package org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling;

import org.lostclient.api.interfaces.Interactable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.item.Item;
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
            Walking.blindWalkTo(position);
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
            if (Camera.getCameraAngle() < 90){
                Camera.setCameraAngle(Calculations.random(90, 100));
            }
            return false;
        }

        return condition == null || WaitFor.condition(Calculations.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static Item getItem(Predicate<Item> filter){
        Item[] rsItems = Inventory.find(filter);
        return rsItems.length > 0 ? rsItems[0] : null;
    }

    public static NPC getNPC(Predicate<NPC> filter){
        NPC[] rsnpcs = NPCs.findNearest(filter);
        return rsnpcs.length > 0 ? rsnpcs[0] : null;
    }

    public static GameObject getGameObject(Predicate<GameObject> filter){
        GameObject[] objects = Objects.findNearest(15, filter);
        return objects.length > 0 ? objects[0] : null;
    }

    public static RSGroundItem getRSGroundItem(Filter<RSGroundItem> filter){
        RSGroundItem[] groundItems = GroundItems.findNearest(filter);
        return groundItems.length > 0 ? groundItems[0] : null;
    }

    public static boolean focusCamera(Interactable clickable){
        if (clickable == null){
            return false;
        }
        if (isOnScreenAndInteractable(clickable)){
            return true;
        }
        Tile tile = ((Locatable) clickable).getTile();
        Camera.turnToTile(tile);
        Camera.setCameraAngle(100 - (tile.distance(Players.localPlayer().getTile()) * 4));
        return isOnScreenAndInteractable(clickable);
    }

    private static boolean isOnScreenAndInteractable(Interactable clickable){
        if (clickable instanceof RSCharacter && !((RSCharacter) clickable).isOnScreen()){
            return false;
        }
        if (clickable instanceof GameObject && !((GameObject) clickable).isOnScreen()){
            return false;
        }
        if (clickable instanceof RSGroundItem && !((RSGroundItem) clickable).isOnScreen()){
            return false;
        }
        return clickable.isInteractable();
    }


}
