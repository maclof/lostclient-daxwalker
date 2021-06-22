package org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera;


import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.wrappers.input.Mouse;
import org.lostclient.api.wrappers.interactives.Entity;
import org.lostclient.api.wrappers.walking.dax_api.shared.RSTile;
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
//        RSTile tile = positionable.getTile();
//        if (tile.isOnScreen() && tile.isInteractable()){
//            return true;
//        }
//
//        if (isMiddleMouseCameraOn()){
//            DaxCamera.focus(tile);
//            return tile.isOnScreen() && tile.isInteractable();
//        } else {
//            AsynchronousCamera.focus(tile);
//            if (!HOVER_BOX.contains(Mouse.getMousePosition())) {
//                Mouse.moveBox(HOVER_BOX);
//            }
//            return WaitFor.condition(org.lostclient.api.utilities.math.Calculations.random(3000, 5000), () -> tile.isOnScreen() && tile.isInteractable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
//        }
        return false;
    }

    public static boolean focusCamera(Entity rsCharacter){
//        if (rsCharacter.isOnScreen() && rsCharacter.isInteractable()){
//            return true;
//        }
//
//        RSTile destination = rsCharacter.getTile();
//        RSTile newDestination = WalkingQueue.getWalkingTowards(rsCharacter);
//        if (newDestination != null){
//            destination = newDestination;
//        }
//
//        if (isMiddleMouseCameraOn()){
//            DaxCamera.focus(destination);
//            return rsCharacter.isOnScreen() && rsCharacter.isInteractable();
//        } else {
//            AsynchronousCamera.focus(destination);
//            if (!HOVER_BOX.contains(Mouse.getMousePosition())) {
//                Mouse.moveBox(HOVER_BOX);
//            }
//            return WaitFor.condition(org.lostclient.api.utilities.math.Calculations.random(3000, 5000), () -> rsCharacter.isOnScreen() && rsCharacter.isInteractable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
//        }
        return false;
    }


    public static boolean isMiddleMouseCameraOn() {
        return PlayerSettings.getBitValue(4134) == 0;
    }

}
