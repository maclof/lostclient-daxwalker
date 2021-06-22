package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.Client;
import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.bank.Bank;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.InteractionHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;

public class BankHelper {

    private static final Predicate<GameObject> BANK_OBJECT_PREDICATE = new Predicate<GameObject>() {
        @Override
        public boolean test(GameObject gameObject) {
            String[] names = new String[] {"bank", "Bank", "Exchange booth", "Open chest"};
            String[] actions = new String[] {"Collect", "Bank"};
            MethodProvider.logDebug("BANK_OBJECT_PREDICATE NOT FULLY IMPLEMENTED!");

            return Arrays.stream(names).anyMatch((n) -> {
                if (!gameObject.getName().contains(n)) {
                    return false;
                }
                return true;
            });
        }
    };

//    private static final Predicate<GameObject> BANK_OBJECT_FILTER = GameObjectHelper.nameContainsPredicate("bank", "Bank", "Exchange booth", "Open chest")
//            .and(GameObjectHelper.actionsContainsPredicate("Collect"), true)
//            .and(GameObjectHelper.actionsContainsPredicate("Bank"), true);

    public static boolean isInBank(){
        return isInBank(Players.localPlayer());
    }

    public static boolean isInBank(Locatable positionable){
        GameObject bankObject = GameObjects.closest((o) -> o.distance() <= 15 && BANK_OBJECT_PREDICATE.test(o));
        HashSet<Tile> building = getBuilding(bankObject);
        return building.contains(positionable.getTile()) || (building.size() == 0 && positionable.getTile().distance(bankObject) < 5);
    }

    /**
     *
     * @return whether if the action succeeded
     */
    public static boolean openBank() {
        return Bank.isOpen() || InteractionHelper.click(InteractionHelper.getGameObject(BANK_OBJECT_PREDICATE), "Bank");
    }

    /**
     *
     * @return bank screen is open
     */
    public static boolean openBankAndWait(){
        if (Bank.isOpen()){
            return true;
        }
        GameObject object = InteractionHelper.getGameObject(BANK_OBJECT_PREDICATE);
        return InteractionHelper.click(object, "Bank") && waitForBankScreen(object);
    }

    public static HashSet<Tile> getBuilding(Locatable positionable){
        MethodProvider.logDebug("Call to Client.getClient().getTiles_renderFlags()");
        return computeBuilding(positionable, Client.getClient().getTiles_renderFlags(), new HashSet<>());
    }

    private static HashSet<Tile> computeBuilding(Locatable positionable, byte[][][] sceneFlags, HashSet<Tile> tiles){
        try {
            Tile local = positionable.getTile();
            int localX = local.getX(), localY = local.getY(), localZ = local.getZ();
            if (localX < 0 || localY < 0 || localZ < 0){
                return tiles;
            }
            if (sceneFlags.length <= localZ || sceneFlags[localZ].length <= localX || sceneFlags[localZ][localX].length <= localY){ //Not within bounds
                return tiles;
            }
            if (sceneFlags[localZ][localX][localY] < 4){ //Not a building
                return tiles;
            }
            MethodProvider.logDebug("computeBuilding NOT FULLY IMPLEMENTED");
//            if (!tiles.add(local.toWorldTile())){ //Already computed
//                return tiles;
//            }
//            computeBuilding(new Tile(localX, localY + 1, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
//            computeBuilding(new Tile(localX + 1, localY, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
//            computeBuilding(new Tile(localX, localY - 1, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
//            computeBuilding(new Tile(localX - 1, localY, localZ, Tile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        return tiles;
    }

    private static boolean isInBuilding(Tile localTile, byte[][][] sceneFlags) {
        return !(sceneFlags.length <= localTile.getZ()
                    || sceneFlags[localTile.getZ()].length <= localTile.getX()
                    || sceneFlags[localTile.getZ()][localTile.getX()].length <= localTile.getY())
                && sceneFlags[localTile.getZ()][localTile.getX()][localTile.getY()] >= 4;
    }

    private static boolean waitForBankScreen(GameObject object){
        return WaitFor.condition(WaitFor.getMovementRandomSleep(object), ((WaitFor.Condition) () -> Bank.isOpen() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE).combine(WaitFor.getNotMovingCondition())) == WaitFor.Return.SUCCESS;
    }

}
