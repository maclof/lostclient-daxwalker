package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters;

import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.FairyRing;
import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;

public enum SecondLetter {
    I(0),
    J(3),
    K(2),
    L(1)
    ;

    public int getValue() {
        return value;
    }

    int value;
    SecondLetter(int value){
        this.value = value;
    }

    public static final int
            VARBIT = 3986,
            CLOCKWISE_CHILD = 21,
            ANTI_CLOCKWISE_CHILD = 22;

    private static int get(){
        return PlayerSettings.getBitValue(VARBIT);
    }

    public boolean isSelected(){
        return get() == this.value;
    }

    public boolean turnTo(){
        int current = get();
        int target = getValue();
        if(current == target)
            return true;
        int diff = current - target;
        int abs = Math.abs(diff);
        if(abs == 2){
            return Calculations.random(0, 1) == 1 ? turnClockwise(2) : turnAntiClockwise(2);
        } else if(diff == 3 || diff == -1){
            return turnClockwise(1);
        } else {
            return turnAntiClockwise(1);
        }
    }

    public static boolean turnClockwise(int rotations){
        if(rotations == 0)
            return true;
        WidgetChild iface = getClockwise();
        final int value = get();
        return iface != null && iface.interact()
                && MethodProvider.sleepUntil(() -> get() != value ,2500)
                && turnClockwise(--rotations);
    }

    public static boolean turnAntiClockwise(int rotations){
        if(rotations == 0)
            return true;
        WidgetChild iface = getAntiClockwise();
        final int value = get();
        return iface != null && iface.interact()
                && MethodProvider.sleepUntil(() -> get() != value ,2500)
                && turnAntiClockwise(--rotations);
    }

    private static WidgetChild getClockwise() {
        return Widgets.getWidgetChild(FairyRing.INTERFACE_MASTER, CLOCKWISE_CHILD);
    }
    private static WidgetChild getAntiClockwise() {
        return Widgets.getWidgetChild(FairyRing.INTERFACE_MASTER, ANTI_CLOCKWISE_CHILD);
    }

}
