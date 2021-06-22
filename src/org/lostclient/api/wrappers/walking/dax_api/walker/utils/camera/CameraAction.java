package org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera;


import org.lostclient.api.wrappers.walking.dax_api.walker.utils.movement.WalkingQueue;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;

import java.awt.*;

public class CameraAction {

    private static final Rectangle HOVER_BOX = new Rectangle(140, 20, 260, 110);

    public static void moveCamera(Locatable destination){
        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(destination);
        } else {
            AsynchronousCamera.focus(destination);
        }
    }

    public static boolean focusCamera(Locatable positionable){
        Tile tile = positionable.getTile();
        if (tile.isOnScreen() && tile.isInteractable()){
            return true;
        }

        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(tile);
            return tile.isOnScreen() && tile.isInteractable();
        } else {
            AsynchronousCamera.focus(tile);
            if (!HOVER_BOX.contains(Mouse.getPos())) {
                Mouse.moveBox(HOVER_BOX);
            }
            return WaitFor.condition(Calculations.random(3000, 5000), () -> tile.isOnScreen() && tile.isInteractable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
    }

    public static boolean focusCamera(RSCharacter rsCharacter){
        if (rsCharacter.isOnScreen() && rsCharacter.isInteractable()){
            return true;
        }

        Tile destination = rsCharacter.getTile();
        Tile newDestination = WalkingQueue.getWalkingTowards(rsCharacter);
        if (newDestination != null){
            destination = newDestination;
        }

        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(destination);
            return rsCharacter.isOnScreen() && rsCharacter.isInteractable();
        } else {
            AsynchronousCamera.focus(destination);
            if (!HOVER_BOX.contains(Mouse.getPos())) {
                Mouse.moveBox(HOVER_BOX);
            }
            return WaitFor.condition(Calculations.random(3000, 5000), () -> rsCharacter.isOnScreen() && rsCharacter.isInteractable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
    }


    public static boolean isMiddleMouseCameraOn() {
        return PlayerSettings.getBitValue(4134).getValue() == 0;
    }

}
