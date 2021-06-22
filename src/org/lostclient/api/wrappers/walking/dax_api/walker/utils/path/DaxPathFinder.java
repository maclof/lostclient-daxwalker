package org.lostclient.api.wrappers.walking.dax_api.walker.utils.path;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.interactives.Entity;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.interactives.Player;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.AStarNode;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.Reachable;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * For local pathing ONLY. Anything outside your region will return unexpected results.
 */
public class DaxPathFinder {

    public static class Destination {
        private Tile tile;
        private Destination parent;
        private int distance;

        public Destination(Tile tile, Destination parent, int distance) {
            this.tile = tile;
            this.parent = parent;
            this.distance = distance;
        }

        public Tile getLocalTile() {
            return tile;
        }

        public Tile getWorldTile() {
            return tile.toWorldTile();
        }

        public Destination getParent() {
            return parent;
        }

        public int getDistance() {
            return distance;
        }

        public List<Tile> getPath() {
            return DaxPathFinder.getPath(this);
        }
    }

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @return The path your character is following.
     */
    public static List<Tile> getWalkingQueue() {
        return getWalkingQueue(getMap());
    }

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @param map
     * @return The path your character is following.
     */
    public static List<Tile> getWalkingQueue(Destination[][] map) {
        Tile destination = Game.getDestination();
        if (destination == null) {
            destination = getNextWalkingTile();
        }
        return destination != null ? getPath(map, destination) : null;
    }

    /**
     *
     * Method to check if your character is walking to a destination.
     *
     * @param tile
     * @return true if your character is walking or will walk to that tile in the next game tick.
     */
    public static boolean isWalkingTowards(Tile tile){
        Tile tile1 = getNextWalkingTile();
        return tile1 != null && tile1.equals(tile);
    }

    /**
     *
     * Next tile that your character is moving to in the current/next game tick.
     *
     * @return The next tile that your character is walking to
     */
    public static Tile getNextWalkingTile(){
        ArrayList<Tile> tiles = getWalkingHistory();
        return tiles.size() > 0 && !tiles.get(0).equals(Players.localPlayer().getTile()) ? tiles.get(0) : null;
    }

    /**
     *
     * @param tile
     * @return Distance to a tile accounting for collision. Integer.MAX_VALUE if not reachable.
     */
    public static int distance(Locatable tile) {
        return distance(getMap(), tile.getTile());
    }

    public static int distance(Destination[][] map, Locatable tile) {
        Tile worldTile = tile.getTile().toLocalTile();
        int x = worldTile.getX(), y = worldTile.getY();

        if (!validLocalBounds(tile)) {
            return Integer.MAX_VALUE;
        }

        Destination destination = map[x][y];
        return destination == null ? Integer.MAX_VALUE : destination.distance;
    }

    public static boolean canReach(Tile tile) {
        return canReach(getMap(), tile);
    }

    public static boolean canReach(Destination[][] map, Tile tile) {
        if (tile.getZ() != Players.localPlayer().getTile().getZ()) return false;
        Tile worldTile = tile.getType() != Tile.TYPES.LOCAL ? tile.toLocalTile() : tile;
        int x = worldTile.getX(), y = worldTile.getY();
        if (!validLocalBounds(tile) || x > map.length || y > map[x].length) {
            return false;
        }
        Destination destination = map[x][y];
        return destination != null;
    }

    public static List<Tile> getPath(Tile tile) {
        return getPath(getMap(), tile);
    }

    public static List<Tile> getPath(Destination destination) {
        Stack<Tile> Tiles = new Stack<>();
        Destination parent = destination;
        while (parent != null) {
            Tiles.add(parent.getWorldTile());
            parent = parent.parent;
        }
        return new ArrayList<>(Tiles);
    }

    public static List<Tile> getPath(Destination[][] map, Tile tile) {
        Tile worldTile = tile.getType() != Tile.TYPES.LOCAL ? tile.toLocalTile() : tile;
        int x = worldTile.getX(), y = worldTile.getY();

        Destination destination = map[x][y];

        if (destination == null) {
            return null;
        }

        return destination.getPath();
    }

    public static Destination[][] getMap() {
        final Tile home = Players.localPlayer().getTile().toLocalTile();
        Destination[][] map = new Destination[104][104];
        int[][] collisionData = PathFinding.getCollisionData();
        if(collisionData == null || collisionData.length < home.getX() || collisionData[home.getX()].length < home.getY()){
            return map;
        }

        Queue<Destination> queue = new LinkedList<>();
        queue.add(new Destination(home, null, 0));
        map[home.getX()][home.getY()] = queue.peek();

        while (!queue.isEmpty()) {
            Destination currentLocal = queue.poll();

            int x = currentLocal.getLocalTile().getX(), y = currentLocal.getLocalTile().getY();
            Destination destination = map[x][y];

            for (Reachable.Direction direction : Reachable.Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                Tile neighbor = direction.getPointingTile(currentLocal.getLocalTile());
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();

                if (!AStarNode.isWalkable(collisionData[destinationX][destinationY])) {
                    continue;
                }

                if (map[destinationX][destinationY] != null) {
                    continue; //Traversed already
                }

                map[destinationX][destinationY] = new Destination(neighbor, currentLocal, destination.getDistance() + 1);
                queue.add(map[destinationX][destinationY]);
            }

        }
        return map;
    }

    public static void drawQueue(Destination[][] map, Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        List<Tile> path = getWalkingQueue(map);
        if (path == null) {
            return;
        }

        Tile previousTile = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Point point1 = Projection.tileToScreen(path.get(i), 0);
            Point point2 = Projection.tileToScreen(previousTile, 0);
            if (point1 == null || point1.x == -1 || point2 == null || point2.x == -1) {
                continue;
            }
            g.setColor(new Color(255, 0, 11, 116));
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(point1.x, point1.y, point2.x, point2.y);
            previousTile = path.get(i);
        }

    }

    public static void drawPaths(Destination[][] map, Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        for (Destination[] destinations : map) {
            for (Destination destination : destinations) {

                if (destination == null || destination.getParent() == null) {
                    continue;
                }

                Tile tile = destination.getWorldTile();
                Tile parent = destination.getParent().getWorldTile();

                if (!tile.isOnScreen() && !parent.isOnScreen()) {
                    continue;
                }

                Point point1 = Projection.tileToScreen(tile, 0);
                Point point2 = Projection.tileToScreen(parent, 0);

                if (point1 == null || point1.x == -1 || point2 == null || point2.x == -1) {
                    continue;
                }

                g.setColor(new Color(255, 255, 255, 60));
                g.setStroke(new BasicStroke(1));
                g.drawLine(point1.x, point1.y, point2.x, point2.y);
            }
        }
    }

    private static boolean validLocalBounds(Locatable positionable) {
        Tile tile = positionable.getTile().getType() == Tile.TYPES.LOCAL ? positionable.getTile() : positionable.getTile().toLocalTile();
        return tile.getX() >= 0 && tile.getX() < 104 && tile.getY() >= 0 && tile.getY() < 104;
    }

    private static ArrayList<Tile> getWalkingHistory(){
        return getWalkingHistory(Players.localPlayer());
    }

    private static ArrayList<Tile> getWalkingHistory(Entity rsCharacter){
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
            MethodProvider.logDebug("No walking history returned for: " + rsCharacter.getClass().getName());
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
