package org.lostclient.api.wrappers.walking.dax_api.walker.utils.movement;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.interactives.Entity;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.interactives.Player;
import org.lostclient.api.wrappers.map.Tile;

import java.util.ArrayList;


public class WalkingQueue {

    public static boolean isWalkingTowards(Tile tile){
        Tile tile1 = getWalkingTowards();
        return tile1 != null && tile1.equals(tile);
    }

    public static Tile getWalkingTowards(){
        ArrayList<Tile> tiles = getWalkingQueue();
        return tiles.size() > 0 && !tiles.get(0).equals(Players.localPlayer().getTile()) ? tiles.get(0) : null;
    }

    public static ArrayList<Tile> getWalkingQueue(){
        return getWalkingQueue(Players.localPlayer());
    }

    public static Tile getWalkingTowards(Entity rsCharacter){
        ArrayList<Tile> tiles = getWalkingQueue(rsCharacter);
        return tiles.size() > 0 && !tiles.get(0).equals(rsCharacter.getTile()) ? tiles.get(0) : null;
    }

    public static ArrayList<Tile> getWalkingQueue(Entity rsCharacter){
        ArrayList<Tile> walkingQueue = new ArrayList<>();
        if (rsCharacter == null){
            return walkingQueue;
        }
        int[] xIndex, yIndex;
        if (rsCharacter instanceof Player) {
            xIndex = ((Player) rsCharacter).getPlayer().getPathX();
            yIndex = ((Player) rsCharacter).getPlayer().getPathX();
        } else if (rsCharacter instanceof NPC) {
            xIndex = ((NPC) rsCharacter).getNpc().getPathX();
            yIndex = ((NPC) rsCharacter).getNpc().getPathX();
        } else {
            MethodProvider.logDebug("No walking queue returned for: " + rsCharacter.getClass().getName());
            return walkingQueue;
        }
        int plane = rsCharacter.getTile().getZ();

        for (int i = 0; i < xIndex.length && i < yIndex.length; i++) {
//            walkingQueue.add(new Tile(xIndex[i], yIndex[i], plane, Tile.TYPES.LOCAL).toWorldTile());
            walkingQueue.add(new Tile(xIndex[i], yIndex[i], plane));
        }
        return walkingQueue;
    }

}
