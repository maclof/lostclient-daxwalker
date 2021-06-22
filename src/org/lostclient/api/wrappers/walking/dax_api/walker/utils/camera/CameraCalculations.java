package org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera;

public class CameraCalculations {

    public static int normalizeAngle(int angle) {
        return Calculations.limitRange(angle, 0, 100);
    }

    public static int normalizeRotation(int rotation) {
        return (rotation) % 360;
    }

    public static int distanceBetweenTwoAngles(int alpha, int beta) {
        int phi = Math.abs(beta - alpha) % 360;       // This is either the distance or 360 - distance
        return phi > 180 ? 360 - phi : phi;
    }

    public static int getAngleToTile(Locatable tile) {
        return 100 - (int) (Math.min(Players.localPlayer().getTile().distanceDouble(tile), 15) * 4);
    }

    public static int getRotationToTile(RSCharacter target) {
        RSCharacter.DIRECTION direction = target.getWalkingDirection();
        int cameraRotation = Camera.getTileAngle(target);
        switch (direction) {
            case N:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(0, 1));
                break;
            case E:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(1, 0));
                break;
            case S:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(0, -1));
                break;
            case W:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(-1, 0));
                break;
            case NE:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(1, 1));
                break;
            case NW:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(-1, 1));
                break;
            case SE:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(1, -1));
                break;
            case SW:
                cameraRotation = Camera.getTileAngle(target.getTile().translate(-1, -1));
                break;

        }
        int currentCameraRotation = Camera.getCameraRotation();
        return cameraRotation + (distanceBetweenTwoAngles(cameraRotation + 45, currentCameraRotation) < distanceBetweenTwoAngles(cameraRotation - 45, currentCameraRotation) ? 45 : -45);
    }

    public static int getRotationToTile(Locatable target) {
        int cameraRotation = Camera.getTileAngle(target);
        int currentCameraRotation = Camera.getCameraRotation();
        return cameraRotation + (distanceBetweenTwoAngles(cameraRotation + 45, currentCameraRotation) < distanceBetweenTwoAngles(cameraRotation - 45, currentCameraRotation) ? 45 : -45);
    }

}
