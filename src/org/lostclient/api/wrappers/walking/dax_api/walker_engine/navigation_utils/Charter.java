package org.lostclient.api.wrappers.walking.dax_api.walker_engine.navigation_utils;

import org.lostclient.api.wrappers.map.Area;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.NPCHelper;
import org.lostclient.api.wrappers.widgets.WidgetChild;
import org.lostclient.api.wrappers.widgets.Widgets;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.Loggable;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.InteractionHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling.NPCInteraction;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Charter implements Loggable {

    private static final int CHARTER_INTERFACE_MASTER = 72;
    private static Charter instance;

    private static Charter getInstance(){
        return instance != null ? instance : (instance = new Charter());
    }

    public static boolean to(LocationProperty locationProperty){
        if (locationProperty == null){
            return false;
        }
        if (!openCharterMenu()){
            getInstance().log("Failed to open charter menu.");
            return false;
        }
        HashMap<LocationProperty, Location> charterLocations = getCharterLocations();
        Location location = charterLocations.get(locationProperty);
        if (location == null){
            getInstance().log("Location: " + locationProperty + " is not available. " + charterLocations.keySet());
            return false;
        }
        if (!location.click()){
            getInstance().log("Failed to click charter location.");
            return false;
        }
        if (!NPCInteraction.waitForConversationWindow()){
            getInstance().log("Confirmation dialogue did not appear.");
        }
        NPCInteraction.handleConversation(new String[]{"Ok", "Okay"});
        return WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean openCharterMenu() {
        return Widgets.getWidget(CHARTER_INTERFACE_MASTER) != null ||
                InteractionHelper.click(InteractionHelper.getNPC(NPCHelper.actionsEqualsPredicate("Charter")), "Charter", () -> InterfaceHelper.isInterfaceSubstantiated(CHARTER_INTERFACE_MASTER) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
    }

    private static HashMap<LocationProperty, Location> getCharterLocations(){
        HashMap<LocationProperty, Location> locations = new HashMap<>();
        InterfaceHelper.getAllInterfaces(CHARTER_INTERFACE_MASTER).stream().filter(

                rsInterface -> rsInterface != null
                && rsInterface.getWidget().getFontId() == 495
                && !rsInterface.isHidden()
                && rsInterface.getWidget().isTextShadowed())

                .collect(Collectors.toList())
                .forEach(rsInterface -> locations.put(
		                LocationProperty.stringToLocation(rsInterface.getText()), new Location(rsInterface)));
        return locations;
    }

    @Override
    public String getName() {
        return "Charter";
    }

    public enum LocationProperty {
        PORT_TYRAS ("Port Tyras", null),
        PORT_PHASMATYS ("Port Phasmatys", new Tile(3702, 3503, 0)),
        CATHERBY ("Catherby", new Tile(2796, 3414, 0)),
        SHIPYARD ("Shipyard", null),
        KARAMJA ("Musa Point", new Tile(2956, 3146, 0)),
        BRIMHAVEN ("Brimhaven", new Tile(2760, 3237, 0)),
        PORT_KHAZARD ("Port Khazard", new Tile(2674, 3149, 0)),
        PORT_SARIM ("Port Sarim", new Tile(3041, 3193, 0)),
        MOS_LE_HARMLESS ("Mos le'Harmless", null),
        CRANDOR ("Crandor", null);

        private String name;
        private Area area;

        LocationProperty(String name, Tile center){
            this.name = name;
            if (center != null) {
                this.area = new Area(center, 15);
            }
        }

        public boolean valid(Tile tile) {
            return area != null && tile != null && area.contains(tile);
        }

        public static LocationProperty stringToLocation(String name){
            for (LocationProperty locationProperty : values()){
                if (name.equals(locationProperty.name)){
                    return locationProperty;
                }
            }
            return null;
        }

        public static LocationProperty getLocation(Tile tile){
            for (LocationProperty locationProperty : values()){
                if (locationProperty.valid(tile)){
                    return locationProperty;
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return name;
        }
    }


    public static class Location {

        private String name;
        private WidgetChild rsInterface;

        private Location(WidgetChild rsInterface){
            this.name = rsInterface.getText();
            this.rsInterface = rsInterface;
        }

        public String getName() {
            return name;
        }

        public WidgetChild getRsInterface() {
            return rsInterface;
        }

        public boolean click(String... options){
            return rsInterface.interact(options);
        }

        @Override
        public String toString(){
            return name;
        }
    }

}
