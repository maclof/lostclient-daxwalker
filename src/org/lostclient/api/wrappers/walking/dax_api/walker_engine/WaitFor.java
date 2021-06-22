package org.lostclient.api.wrappers.walking.dax_api.walker_engine;

import org.lostclient.api.accessor.Players;
import org.lostclient.api.interfaces.Interactable;
import org.lostclient.api.interfaces.Locatable;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.Walking;

import java.util.Random;

public class WaitFor {

    private static final Random random = new Random();

    public static Condition getNotMovingCondition(){
        return new Condition() {
            final Tile initialTile = Players.localPlayer().getTile();
            final long movingDelay = 1300, startTime = System.currentTimeMillis();

            @Override
            public Return active() {
                if (MethodProvider.timeFromMark(startTime) > movingDelay && initialTile.equals(Players.localPlayer().getTile()) && !Players.localPlayer().isMoving()) {
                    return Return.FAIL;
                }
                return Return.IGNORE;
            }
        };
    }

    public static int getMovementRandomSleep(Locatable positionable){
        return getMovementRandomSleep((int)Players.localPlayer().getTile().distance(positionable));
    }

    public static int getMovementRandomSleep(int distance){
        final double multiplier = Walking.isRunEnabled() ? 0.3 : 0.6;
        final int base = random(1800, 2400);
        if (distance > 25){
            return base;
        }
        int sleep = (int) (multiplier * distance);
//        return (int)Calculations.randomSD(base * .8, base * 1.2, base, base * 0.1) + sleep;
        return (int)Calculations.random(base * .8, base * 1.2) + sleep;
    }


    public static Return isOnScreenAndInteractable(Interactable clickable){
/*        Locatable positionable = (Locatable) clickable;
        return WaitFor.condition(getMovementRandomSleep(positionable), () -> (
                clickable instanceof RSCharacter ? ((RSCharacter) clickable).isOnScreen() :
                clickable instanceof GameObject ? ((GameObject) clickable).isOnScreen() :
                clickable instanceof RSGroundItem && ((RSGroundItem) clickable).isOnScreen())
                && clickable.isInteractable() ? Return.SUCCESS : Return.IGNORE);*/
        return Return.SUCCESS;
    }

    public static Return condition(int timeout, Condition condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            switch (condition.active()){
                case SUCCESS: return Return.SUCCESS;
                case FAIL: return Return.FAIL;
                case IGNORE: milliseconds(75);
            }
        }
        return Return.TIMEOUT;
    }

    /**
     *
     * @param timeout
     * @param condition
     * @param <V>
     * @return waits {@code timeout} for the return value to not be null.
     */
    public static <V> V getValue(int timeout, ReturnCondition<V> condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            V v = condition.getValue();
            if (v != null){
                return v;
            }
            milliseconds(25);
        }
        return null;
    }

    public static int random(int low, int high) {
        return random.nextInt((high - low) + 1) + low;
    }

    public static Return milliseconds(int low, int high){
        try {
            Thread.sleep(random(low, high));
        } catch (InterruptedException e){
            throw new IllegalStateException("Break out");
        }
        return Return.IGNORE;
    }

    public static Return milliseconds(int amount){
        return milliseconds(amount, amount);
    }


    public enum Return {
        TIMEOUT,    //EXIT CONDITION BECAUSE OF TIMEOUT
        SUCCESS,    //EXIT CONDITION BECAUSE SUCCESS
        FAIL,       //EXIT CONDITION BECAUSE OF FAILURE
        IGNORE      //NOTHING HAPPENS, CONTINUE CONDITION

    }

    public interface ReturnCondition <V> {
        V getValue();
    }

    public interface Condition{
        Return active();
        default Condition combine(Condition a){
            Condition b = this;
            return () -> {
                Return result = a.active();
                return result != Return.IGNORE ? result : b.active();
            };
        }
    }

}
