package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils;

import org.lostclient.api.Client;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.map.Area;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.GameObjectHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.InteractionHelper;

public class ShipUtils {

    private static final Tile[] SPECIAL_CASES = new Tile[]{new Tile(2663, 2676, 1)};

    public static boolean isOnShip() {
        Tile playerPos = Players.localPlayer().getTile();
        for (Tile specialCase : SPECIAL_CASES){
            if (new Area(specialCase, 5).contains(playerPos)){
                return true;
            }
        }
        return getGangplank() != null
                && Players.localPlayer().getTile().getZ() == 1
                && GameObjectHelper.find(10, GameObjectHelper.nameEqualsPredicate("Ship's wheel", "Ship's ladder", "Anchor")).length > 0;
    }

    public static boolean crossGangplank() {
        GameObject gangplank = getGangplank();
        if (gangplank == null){
            return false;
        }
        if (!gangplank.interact("Cross")){
            return false;
        }
        if (WaitFor.condition(1000, () -> Client.getClient().getMouseCrossState() == 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
            return false;
        }
        return WaitFor.condition(Calculations.random(2500, 3000), () -> !ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static GameObject getGangplank(){
        return InteractionHelper.getGameObject(GameObjectHelper.nameEqualsPredicate("Gangplank").and(GameObjectHelper.actionsContainsPredicate("Cross")));
    }

}
