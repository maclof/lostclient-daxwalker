package org.lostclient.api.wrappers.walking.dax_api.shared.helpers;

import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;

import java.util.*;

public class InterfaceHelper {

    public static boolean isInterfaceSubstantiated(int... ids) {
        WidgetChild widgetChild = Widgets.getWidgetChild(ids);
        return widgetChild != null && widgetChild.isVisible();
    }

    /**
     *
     * @param ids
     * @return never null
     */
    public static List<WidgetChild> getAllInterfaces(int... ids){
        ArrayList<WidgetChild> interfaces = new ArrayList<>();

        for (int id : ids) {
            Queue<WidgetChild> queue = new LinkedList<>();
            WidgetChild master = Widgets.getWidgetChild(id);

            if (master == null) {
                return interfaces;
            }

            queue.add(master);
            WidgetChild[] components = master.getChildren().toArray(new WidgetChild[0]);
            if (components != null) {
                Collections.addAll(queue, components);
            }

            while (!queue.isEmpty()) {
                WidgetChild rsInterface = queue.poll();
                interfaces.add(rsInterface);
                WidgetChild[] children = rsInterface.getChildren().toArray(new WidgetChild[0]);
                if (children != null) {
                    Collections.addAll(queue, children);
                }
            }
        }

        return interfaces;
    }

    public static List<WidgetChild> getAllInterfaces(WidgetChild parent){
        ArrayList<WidgetChild> interfaces = new ArrayList<>();
        Queue<WidgetChild> queue = new LinkedList<>();

        if (parent == null){
            return interfaces;
        }

        queue.add(parent);
        while (!queue.isEmpty()){
            WidgetChild rsInterface = queue.poll();
            interfaces.add(rsInterface);
            WidgetChild[] children = rsInterface.getChildren().toArray(new WidgetChild[0]);
            if (children != null) {
                Collections.addAll(queue, children);
            }
        }

        return interfaces;
    }

    public static boolean textEquals(WidgetChild rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.equals(match);
    }

    public static boolean textContains(WidgetChild rsInterface, String match){
        String text = rsInterface.getText();
        return text != null && text.contains(match);
    }

    public static boolean textMatches(WidgetChild rsInterface, String match){
        if (rsInterface == null){
            return false;
        }
        String text = rsInterface.getText();
        return text != null && text.matches(match);
    }

    public static List<String> getActions(WidgetChild rsInterface){
        if (rsInterface == null){
            return Collections.emptyList();
        }
        String[] actions = rsInterface.getActions();
        if (actions == null){
            return Collections.emptyList();
        }
        return Arrays.asList(actions);
    }

}
