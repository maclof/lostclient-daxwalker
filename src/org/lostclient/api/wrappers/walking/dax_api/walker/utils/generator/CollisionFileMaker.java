package org.lostclient.api.wrappers.walking.dax_api.walker.utils.generator;

import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.AStarNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CollisionFileMaker {

    public static void getCollisionData(){
        try {
            int[][] collisionData = PathFinding.getCollisionData();
            if(collisionData == null)
                return;
            int baseX = Game.getBaseX();
            int baseY = Game.getBaseY();
            int baseZ = Players.localPlayer().getTile().getZ();

            File file = new File(Util.getWorkingDirectory().getAbsolutePath() + File.separator + baseX + "x" + baseY + "x" + baseZ + ".cdata");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (int x = 0; x < collisionData.length; x++) {
                for (int y = 0; y < collisionData[x].length; y++) {
                    int flag = collisionData[x][y];
                    Tile tile = new Tile(x, y, baseZ, Tile.TYPES.LOCAL).toWorldTile();
                    CollisionTile collisionTile = new CollisionTile(
                            tile.getX(), tile.getY(), tile.getZ(),
                            AStarNode.blockedNorth(flag),
                            AStarNode.blockedEast(flag),
                            AStarNode.blockedSouth(flag),
                            AStarNode.blockedWest(flag),
                            !AStarNode.isWalkable(flag),
                            false,
                            !AStarNode.isInitialized(flag));
                    bufferedWriter.write(collisionTile.toString());
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
