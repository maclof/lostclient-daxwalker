package org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera;

import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.wrappers.camera.Camera;
import org.lostclient.api.wrappers.input.Mouse;

import java.awt.*;

import static org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera.CameraCalculations.distanceBetweenTwoAngles;


public class DaxCamera {

    private static float PIXEL_TO_ANGLE_RATIO = 2.253731343283582F, PIXEL_TO_ROTATION_RATIO = 2.966666666666667F;

    public static void focus(Locatable positionable){
        positionCamera(
		        CameraCalculations.getAngleToTile(positionable), CameraCalculations.getRotationToTile(positionable));
    }

    public static void positionCamera(int angle, int rotation){
//        if (!CameraAction.isMiddleMouseCameraOn()){
//            return;
//        }
//        int currentAngle = Camera.getAngle(), currentRotation = Camera.getCameraYaw();
//
//        int cameraAngleDifference = angle - currentAngle;
//        int cameraRotationDifference =  distanceBetweenTwoAngles(currentRotation, rotation), rotationDirection;
//        if (CameraCalculations.normalizeRotation(currentRotation + cameraRotationDifference) == rotation){
//            rotationDirection = -1; //TURN RIGHT
//        } else {
//            rotationDirection = 1;
//        }
//
//        if (!getGameScreen().contains(Mouse.getMousePosition())){
//            Mouse.moveBox(Screen.getViewport());
//        }
//
//        Point startingPoint = Mouse.getMousePosition();
//        Point endingPoint = new Point(startingPoint);
//
//        int dx = rotationDirection * cameraRotationDifference;
//        int dy = cameraAngleDifference;
//
//        endingPoint.translate(rotationToPixel(dx), angleToPixel(dy));
//
//        Mouse.sendPress(startingPoint, 2);
//        Mouse.move(endingPoint);
//        Mouse.sendRelease(endingPoint, 2);
    }

    public static Rectangle getGameScreen(){
        return new Rectangle(0,0, 765, 503);
    }

    private static int rotationToPixel(int rotation){
        return (int) (rotation * PIXEL_TO_ROTATION_RATIO);
    }

    private static int angleToPixel(int angle){
        return (int) (angle * PIXEL_TO_ANGLE_RATIO);
    }

    private static Point generatePoint(Rectangle rectangle){
        return new Point(
                org.lostclient.api.utilities.math.Calculations.random(rectangle.x, rectangle.x + rectangle.width),
                org.lostclient.api.utilities.math.Calculations.random(rectangle.y, rectangle.y + rectangle.height)
        );
    }
}
