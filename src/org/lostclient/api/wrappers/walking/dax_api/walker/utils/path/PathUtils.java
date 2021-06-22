package org.lostclient.api.wrappers.walking.dax_api.walker.utils.path;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.wrappers.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PathUtils {

    public static Tile getNextTileInPath(Tile current, List<Tile> path) {
        int index = path.indexOf(current);

        if (index == -1) {
            return null;
        }

        int next = index + 1;
        return next < path.size() ? path.get(next) : null;
    }

    public static Tile getClosestTileInPath(List<Tile> path) {
        Tile player = Players.localPlayer().getTile();
        return path.stream().min(Comparator.comparingDouble(o -> o.distance(player))).orElse(null);
    }

    public static Tile getFurthestReachableTileInMinimap(List<Tile> path) {
        List<Tile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (Tile tile : reversed) {
//            Point point = Projection.tileToMinimap(tile);
//            if (point == null) {
//                continue;
//            }
            if (DaxPathFinder.canReach(map, tile)) {// && Projection.isInMinimap(point)) {
                return tile;
            }
        }
        return null;
    }

    public static Tile getFurthestReachableTileOnScreen(List<Tile> path) {
        List<Tile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (Tile tile : reversed) {
            if (DaxPathFinder.canReach(map, tile) && tile.isOnScreen() && tile.isInteractable()) {
                return tile;
            }
        }
        return null;
    }

    public static void drawDebug(Graphics graphics, List<Tile> path) {
        Graphics2D g = (Graphics2D) graphics;
        Tile player = Players.localPlayer().getTile();

        g.setColor(new Color(0, 191, 23, 80));
        for (Tile tile : path) {
            if (tile.distance(player) > 25) {
                continue;
            }
            Polygon polygon = Projection.getTileBoundsPoly(tile, 0);
            if (polygon == null) {
                continue;
            }
            g.fillPolygon(polygon);
        }

        Tile closest = getClosestTileInPath(path);
        if (closest != null) {
            Polygon polygon = Projection.getTileBoundsPoly(closest, 0);
            if (polygon != null) {
                g.setColor(new Color(205, 0, 255, 80));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Closest In Path", polygon.xpoints[0] - 24, polygon.ypoints[1] + 1);
                g.setColor(Color.WHITE);
                graphics.drawString("Closest In Path", polygon.xpoints[0] - 25, polygon.ypoints[1]);
            }
        }

        Tile furthestScreenTile = getFurthestReachableTileOnScreen(path);
        if (furthestScreenTile != null) {
            Polygon polygon = Projection.getTileBoundsPoly(furthestScreenTile, 0);
            if (polygon != null) {
                g.setColor(new Color(255, 0, 11, 157));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Furthest Screen Tile", polygon.xpoints[0] - 24, polygon.ypoints[1] + 30);
                g.setColor(Color.WHITE);
                graphics.drawString("Furthest Screen Tile", polygon.xpoints[0] - 25, polygon.ypoints[1] + 30);
            }
        }

        Tile furthestMapTile = getFurthestReachableTileInMinimap(path);
        if (furthestMapTile != null) {
            Point p = Projection.tileToMinimap(furthestMapTile);
            if (p != null) {
                g.setColor(new Color(255, 0, 11, 157));
                g.fillRect(p.x - 3, p.y - 3, 6, 6);

                g.setColor(Color.BLACK);
                graphics.drawString("Furthest Map Tile", p.x + 1, p.y + 14);
                g.setColor(Color.WHITE);
                graphics.drawString("Furthest Map Tile", p.x, p.y + 15);
            }
        }

        Tile nextTile = getNextTileInPath(furthestMapTile, path);
        if (nextTile != null) {
            Polygon polygon = Projection.getTileBoundsPoly(nextTile, 0);
            if (polygon != null) {
                g.setColor(new Color(255, 242, 0, 157));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Next Tile", polygon.xpoints[0] - 24, polygon.ypoints[1]);
                g.setColor(Color.WHITE);
                graphics.drawString("Next Tile", polygon.xpoints[0] - 25, polygon.ypoints[1]);
            }
        }


    }

    public static class NotInPathException extends RuntimeException {
        public NotInPathException() {
        }
    }

}
