package org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera;

import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.wrappers.camera.Camera;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.lostclient.api.wrappers.walking.dax_api.walker.utils.camera.CameraCalculations.getRotationToTile;

public class AsynchronousCamera {

    private static AsynchronousCamera instance = null;
    private ExecutorService executorService;
    private Future angleTask, rotationTask;

    private static AsynchronousCamera getInstance(){
        return instance != null ? instance : (instance = new AsynchronousCamera());
    }

    private AsynchronousCamera(){
        executorService = Executors.newFixedThreadPool(2);
    }

    public static Future focus(Locatable positionable){
        Future rotation = setCameraRotation(getRotationToTile(positionable), 0);
        Future angle = setCameraAngle(CameraCalculations.getAngleToTile(positionable), 0);
        return rotation;
    }

    public static synchronized Future setCameraAngle(int angle, int tolerance){
        if (getInstance().angleTask != null && !getInstance().angleTask.isDone()){
            return null;
        }
        MethodProvider.logDebug("DaxWalker Unimplemented Method: AsynchronousCamera.setCameraAngle");
//        Camera.setRotationMethod(Camera.ROTATION_METHOD.ONLY_KEYS);
        return getInstance().angleTask = getInstance().executorService.submit(() -> {
//            Camera.setCameraAngle(CameraCalculations.normalizeAngle(angle + org.lostclient.api.utilities.math.Calculations.random(-tolerance, tolerance)));
        });
    }

    public static synchronized Future setCameraRotation(int degrees, int tolerance){
        if (getInstance().rotationTask != null && !getInstance().rotationTask.isDone()){
            return null;
        }
        MethodProvider.logDebug("DaxWalker Unimplemented Method: AsynchronousCamera.setCameraRotation");
//        Camera.setRotationMethod(Camera.ROTATION_METHOD.ONLY_KEYS);
        return getInstance().rotationTask = getInstance().executorService.submit(() -> {
//            Camera.setCameraRotation(CameraCalculations.normalizeRotation(degrees + org.lostclient.api.utilities.math.Calculations.random(-tolerance, tolerance)));
        });
    }

}
