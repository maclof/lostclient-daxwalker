package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.letters;

import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils.fairyring.FairyRing;
import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;

public enum FirstLetter {
    A(0),
    B(3),
    C(2),
    D(1)
    ;
    int value;
    FirstLetter(int value){
        this.value = value;
    }


    public int getValue(){
        return value;
    }

    public static final int
            VARBIT = 3985,
            CLOCKWISE_CHILD = 19,
            ANTI_CLOCKWISE_CHILD = 20;

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
