package org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling;

import org.lostclient.api.Client;
import org.lostclient.api.accessor.GameObjects;
import org.lostclient.api.accessor.NPCs;
import org.lostclient.api.accessor.PlayerSettings;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.containers.inventory.Inventory;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.input.mouse.Menu;
import org.lostclient.api.wrappers.interactives.GameObject;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.item.Item;
import org.lostclient.api.wrappers.map.Area;
import org.lostclient.api.wrappers.map.Tile;
import org.lostclient.api.wrappers.walking.Walking;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.GameObjectHelper;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.StringHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.Loggable;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WalkerEngine;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.bfs.BFS;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.PathAnalyzer;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.local_pathfinding.Reachable;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.real_time_collision.RealTimeCollisionTile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class PathObjectHandler implements Loggable {

    private static PathObjectHandler instance;

    private final TreeSet<String> sortedOptions, sortedBlackList, sortedBlackListOptions, sortedHighPriorityOptions;

    private PathObjectHandler(){
        sortedOptions = new TreeSet<>(
		        Arrays.asList("Enter", "Cross", "Pass", "Open", "Close", "Walk-through", "Use", "Pass-through", "Exit",
                "Walk-Across", "Go-through", "Walk-across", "Climb", "Climb-up", "Climb-down", "Climb-over", "Climb over", "Climb-into", "Climb-through",
                "Board", "Jump-from", "Jump-across", "Jump-to", "Squeeze-through", "Jump-over", "Pay-toll(10gp)", "Step-over", "Walk-down", "Walk-up","Walk-Up", "Travel", "Get in",
                "Investigate", "Operate", "Climb-under","Jump","Crawl-down","Crawl-through","Activate","Push","Squeeze-past","Walk-Down",
                "Swing-on", "Climb up", "Ascend", "Descend","Channel","Teleport","Pass-Through","Jump-up","Jump-down","Swing across"));

        sortedBlackList = new TreeSet<>(Arrays.asList("Coffin","Drawers","null"));
        sortedBlackListOptions = new TreeSet<>(Arrays.asList("Chop down"));
        sortedHighPriorityOptions = new TreeSet<>(Arrays.asList("Pay-toll(10gp)","Squeeze-past"));
    }

    private static PathObjectHandler getInstance(){
        return instance != null ? instance : (instance = new PathObjectHandler());
    }

    private enum SpecialObject {
        WEB("Web", "Slash", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Web"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Slash"), true)).length > 0;
            }
        }),
        ROCKFALL("Rockfall", "Mine", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Rockfall"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Mine"), true)).length > 0;
            }
        }),
        ROOTS("Roots", "Chop", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Roots"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Chop"), true)).length > 0;
            }
        }),
        ROCK_SLIDE("Rockslide", "Climb-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Rockslide"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Climb-over"), true)).length > 0;
            }
        }),
        ROOT("Root", "Step-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Root"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Step-over"), true)).length > 0;
            }
        }),
        BRIMHAVEN_VINES("Vines", "Chop-down", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return GameObjectHelper.find(15,
                        GameObjectHelper.inAreaPredicate(new Area(destinationDetails.getAssumed(), 1))
                                .and(GameObjectHelper.nameEqualsPredicate("Vines"), true)
                                .and(GameObjectHelper.actionsContainsPredicate("Chop-down"), true)).length > 0;
            }
        }),
        AVA_BOOKCASE ("Bookcase", "Search", new Tile(3097, 3359, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() >= 3097 && destinationDetails.getAssumed().equals(new Tile(3097, 3359, 0));
            }
        }),
        AVA_LEVER ("Lever", "Pull", new Tile(3096, 3357, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() < 3097 && destinationDetails.getAssumed().equals(new Tile(3097, 3359, 0));
            }
        }),
        ARDY_DOOR_LOCK_SIDE("Door", "Pick-lock", new Tile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.localPlayer().getTile().getX() >= 2565 && Players.localPlayer().getTile().distance(new Tile(2565, 3356, 0)) < 3;
            }
        }),
        ARDY_DOOR_UNLOCKED_SIDE("Door", "Open", new Tile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.localPlayer().getTile().getX() < 2565 && Players.localPlayer().getTile().distance(new Tile(2565, 3356, 0)) < 3;
            }
        }),
        YANILLE_DOOR_LOCK_SIDE("Door", "Pick-lock", new Tile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.localPlayer().getTile().getY() <= 9481 && Players.localPlayer().getTile().distance(new Tile(2601, 9482, 0)) < 3;
            }
        }),
        YANILLE_DOOR_UNLOCKED_SIDE("Door", "Open", new Tile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Players.localPlayer().getTile().getY() > 9481 && Players.localPlayer().getTile().distance(new Tile(2601, 9482, 0)) < 3;
            }
        }),
        EDGEVILLE_UNDERWALL_TUNNEL("Underwall tunnel", "Climb-into", new Tile(3138, 3516, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3138, 3516, 0));
            }
        }),
        VARROCK_UNDERWALL_TUNNEL("Underwall tunnel", "Climb-into", new Tile(3141, 3513, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new Tile(3141, 3513, 0 ));
            }
        }),
        GAMES_ROOM_STAIRS("Stairs", "Climb-down", new Tile(2899, 3565, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(2899, 3565, 0)) &&
                    destinationDetails.getAssumed().equals(new Tile(2205, 4934, 1));
            }
        }),
        CANIFIS_BASEMENT_WALL("Wall", "Search", new Tile(3480, 9836, 0),new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(3480, 9836, 0)) ||
                    destinationDetails.getAssumed().equals(new Tile(3480, 9836, 0));
            }
        }),
        BRINE_RAT_CAVE_BOULDER("Cave", "Exit", new Tile(2690, 10125, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getTile().equals(new Tile(2690, 10125, 0))
                    && NPCs.find(NPCHelper.nameEqualsPredicate("Boulder").and(NPCHelper.actionsContainsPredicate("Roll"))).length > 0;
            }
        });

        private String name, action;
        private Tile location;
        private SpecialCondition specialCondition;

        SpecialObject(String name, String action, Tile location, SpecialCondition specialCondition){
            this.name = name;
            this.action = action;
            this.location = location;
            this.specialCondition = specialCondition;
        }

        public String getName() {
            return name;
        }

        public String getAction() {
            return action;
        }

        public Tile getLocation() {
            return location;
        }

        public boolean isSpecialCondition(PathAnalyzer.DestinationDetails destinationDetails){
            return specialCondition.isSpecialLocation(destinationDetails);
        }

        public static SpecialObject getValidSpecialObjects(PathAnalyzer.DestinationDetails destinationDetails){
            for (SpecialObject object : values()){
                if (object.isSpecialCondition(destinationDetails)){
                    return object;
                }
            }
            return null;
        }

    }

    private abstract static class SpecialCondition {
        abstract boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails);
    }

    public static boolean handle(PathAnalyzer.DestinationDetails destinationDetails, List<Tile> path){
        RealTimeCollisionTile start = destinationDetails.getDestination(), end = destinationDetails.getNextTile();

        GameObject[] interactiveObjects = null;

        String action = null;
        SpecialObject specialObject = SpecialObject.getValidSpecialObjects(destinationDetails);
        if (specialObject == null) {
            if ((interactiveObjects = getInteractiveObjects(start.getX(), start.getY(), start.getZ(), destinationDetails)).length < 1 && end != null) {
                interactiveObjects = getInteractiveObjects(end.getX(), end.getY(), end.getZ(), destinationDetails);
            }
        } else {
            action = specialObject.getAction();
            Predicate<GameObject> specialObjectFilter = GameObjectHelper.nameEqualsPredicate(specialObject.getName())
                    .and(GameObjectHelper.actionsContainsPredicate(specialObject.getAction()), true)
                    .and(GameObjectHelper.inAreaPredicate(new Area(specialObject.getLocation() != null ? specialObject.getLocation() : destinationDetails.getAssumed(), 1)), true);
            interactiveObjects = GameObjectHelper.findClosest(15, specialObjectFilter);
        }

        if (interactiveObjects.length == 0) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder("Sort Order: ");
        Arrays.stream(interactiveObjects).forEach(rsObject -> stringBuilder.append(rsObject.getDefinition().getName()).append(" ").append(
		        Arrays.asList(rsObject.getDefinition().getActions())).append(", "));
        getInstance().log(stringBuilder);

        return handle(path, interactiveObjects[0], destinationDetails, action, specialObject);
    }

    private static boolean handle(List<Tile> path, GameObject object, PathAnalyzer.DestinationDetails destinationDetails, String action, SpecialObject specialObject){
        PathAnalyzer.DestinationDetails current = PathAnalyzer.furthestReachableTile(path);

        if (current == null){
            return false;
        }

        RealTimeCollisionTile currentFurthest = current.getDestination();
        if (!Players.localPlayer().isMoving() && (!object.isOnScreen() || !object.isInteractable())){
            if (!WalkerEngine.getInstance().clickMinimap(destinationDetails.getDestination())){
                return false;
            }
        }
        if (WaitFor.condition(Calculations.random(5000, 8000), () -> object.isOnScreen() && object.isInteractable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
            return false;
        }

        boolean successfulClick = false;

        if (specialObject != null) {
            getInstance().log("Detected Special Object: " + specialObject);
            switch (specialObject){
                case WEB:
                    List<GameObject> webs;
                    int iterations = 0;
                    while ((webs = Arrays.stream(GameObjects.getAt(object.getTile()))
                            .filter(object1 -> Arrays.stream(GameObjectHelper.getActions(object1))
                                    .anyMatch(s -> s.equals("Slash"))).collect(Collectors.toList())).size() > 0){
                        GameObject web = webs.get(0);
                        if (canLeftclickWeb()) {
                            InteractionHelper.click(web, "Slash");
                        } else {
                            useBladeOnWeb(web);
                        }
                        if(Game.isUptext("->")){
                            Walking.setWalkFlag(Players.localPlayer().getTile());
                        }
                        if (web.getTile().distance(Players.localPlayer().getTile()) <= 1) {
//                            WaitFor.milliseconds(Calculations.randomSD(50, 800, 250, 150));
                            WaitFor.milliseconds(Calculations.random(50, 800));
                        } else {
                            WaitFor.milliseconds(2000, 4000);
                        }
                        if (Reachable.getMap().getParent(destinationDetails.getAssumedX(), destinationDetails.getAssumedY()) != null &&
                                (webs = Arrays.stream(Objects.getAt(object.getTile())).filter(object1 -> Arrays.stream(GameObjectHelper.getActions(object1))
                                        .anyMatch(s -> s.equals("Slash"))).collect(Collectors.toList())).size() == 0){
                            successfulClick = true;
                            break;
                        }
                        if (iterations++ > 5){
                            break;
                        }
                    }
                    break;
                case ARDY_DOOR_LOCK_SIDE:
                case YANILLE_DOOR_LOCK_SIDE:
                    for (int i = 0; i < Calculations.random(15, 25); i++) {
                        if (!clickOnObject(object, new String[]{specialObject.getAction()})){
                            continue;
                        }
                        if (Players.localPlayer().getTile().distance(specialObject.getLocation()) > 1){
                            WaitFor.condition(Calculations.random(3000, 4000), () -> Players.localPlayer().getTile().distance(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        }
                        if (Players.localPlayer().getTile().equals(new Tile(2564, 3356, 0))){
                            successfulClick = true;
                            break;
                        }
                    }
                    break;
                case VARROCK_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.EDGEVILLE_UNDERWALL_TUNNEL.getLocation().equals(Players.localPlayer().getTile()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case EDGEVILLE_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.VARROCK_UNDERWALL_TUNNEL.getLocation().equals(Players.localPlayer().getTile()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case BRINE_RAT_CAVE_BOULDER:
                    NPC boulder = InteractionHelper.getNPC(NPCHelper.nameEqualsPredicate("Boulder").and(NPCHelper.actionsContainsPredicate("Roll")));
                    if(InteractionHelper.click(boulder, "Roll")){
                        if(WaitFor.condition(12000,
                            () -> NPCs.closest(NPCHelper.nameEqualsPredicate("Boulder").and(NPCHelper.actionsContainsPredicate("Roll"))) == null ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                            WaitFor.milliseconds(3500, 6000);
                        }
                    }
                    break;
            }
        }

        if (!successfulClick){
            String[] validOptions = action != null ? new String[]{action} : getViableOption(
		            Arrays.stream(object.getActions()).filter(getInstance().sortedOptions::contains).collect(
				            Collectors.toList()), destinationDetails);
            if (!clickOnObject(object, validOptions)) {
                return false;
            }
        }

        boolean strongholdDoor = isStrongholdDoor(object);

        if (strongholdDoor){
            if (WaitFor.condition(Calculations.random(6700, 7800), () -> {
                Tile playerPosition = Players.localPlayer().getTile();
                if (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.getZ()), destinationDetails.getNextTile(), 50)) {
                    WaitFor.milliseconds(500, 1000);
                    return WaitFor.Return.SUCCESS;
                }
                if (NPCInteraction.isConversationWindowUp()) {
                    handleStrongholdQuestions();
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            }) != WaitFor.Return.SUCCESS){
                return false;
            }
        }

        WaitFor.condition(Calculations.random(8500, 11000), () -> {
            DoomsToggle.handleToggle();
            PathAnalyzer.DestinationDetails destinationDetails1 = PathAnalyzer.furthestReachableTile(path);
            if (NPCInteraction.isConversationWindowUp()) {
                NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
            }
            if (destinationDetails1 != null) {
                if (!destinationDetails1.getDestination().equals(currentFurthest)){
                    return WaitFor.Return.SUCCESS;
                }
            }
            if (current.getNextTile() != null){
                PathAnalyzer.DestinationDetails hoverDetails = PathAnalyzer.furthestReachableTile(path, current.getNextTile());
                if (hoverDetails != null && hoverDetails.getDestination() != null && hoverDetails.getDestination().getTile().distance(Players.localPlayer().getTile()) > 7 && !strongholdDoor && Players.localPlayer().getTile().distance(object) <= 2){
                    WalkerEngine.getInstance().hoverMinimap(hoverDetails.getDestination());
                }
            }
            return WaitFor.Return.IGNORE;
        });
        if (strongholdDoor){
            MethodProvider.sleep(800, 1200);
        }
        return true;
    }

    public static GameObject[] getInteractiveObjects(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        GameObject[] objects = Objects.getAll(25, interactiveObjectFilter(x, y, z, destinationDetails));
        final Tile base = new Tile(x, y, z);
        Arrays.sort(objects, (o1, o2) -> {
            int c = Integer.compare((int)o1.getTile().distance(base), (int)o2.getTile().distance(base));
            int assumedZ = destinationDetails.getAssumedZ(), destinationZ = destinationDetails.getDestination().getZ();
            List<String> actions1 = Arrays.asList(o1.getActions());
            List<String> actions2 = Arrays.asList(o2.getActions());

            if (assumedZ > destinationZ){
                if (actions1.contains("Climb-up")){
                    return -1;
                }
                if (actions2.contains("Climb-up")){
                    return 1;
                }
            } else if (assumedZ < destinationZ){
                if (actions1.contains("Climb-down")){
                    return -1;
                }
                if (actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(destinationDetails.getAssumed().distance(destinationDetails.getDestination().getTile()) > 20){
                if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
                    return -1;
                } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
                return 1;
            } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
                return -1;
            }
            return c;
        });
        StringBuilder a = new StringBuilder("Detected: ");
        Arrays.stream(objects).forEach(object -> a.append(object.getName()).append(" "));
        getInstance().log(a);



        return objects;
    }

    /**
     * Filter that accepts only interactive objects to progress in path.
     *
     * @param x
     * @param y
     * @param z
     * @param destinationDetails context where destination is at
     * @return
     */
    private static Predicate<GameObject> interactiveObjectFilter(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        final Tile position = new Tile(x, y, z);
        return (o) -> {
            String name = o.getName();
            if (getInstance().sortedBlackList.contains(name)) {
                return false;
            }
            if (GameObjectHelper.getActionsList(o).stream().anyMatch(s -> getInstance().sortedBlackListOptions.contains(s))){
                return false;
            }
            if (o.getTile().distance(destinationDetails.getDestination().getTile()) > 5) {
                return false;
            }
            if (o.getObjectTiles().stream().noneMatch(t -> t.distance(position) <= 2)) {
                return false;
            }
            List<String> options = Arrays.asList(o.getActions());
            return options.stream().anyMatch(getInstance().sortedOptions::contains);
        };
    }

    private static String[] getViableOption(Collection<String> collection, PathAnalyzer.DestinationDetails destinationDetails){
        Set<String> set = new HashSet<>(collection);
        if (set.retainAll(getInstance().sortedHighPriorityOptions) && set.size() > 0){
            return set.toArray(new String[set.size()]);
        }
        if (destinationDetails.getAssumedZ() > destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-up")){
                return new String[]{"Climb-up"};
            }
        }
        if (destinationDetails.getAssumedZ() < destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        if (destinationDetails.getAssumedY() > 5000 && destinationDetails.getDestination().getZ() == 0 && destinationDetails.getAssumedZ() == 0){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        String[] options = new String[collection.size()];
        collection.toArray(options);
        return options;
    }

    private static boolean clickOnObject(GameObject object, String... options){
        boolean result;

        if (isClosedTrapDoor(object, options)){
            result = handleTrapDoor(object);
        } else {
            result = InteractionHelper.click(object, options);
            getInstance().log("Interacting with (" + GameObjectHelper.getName(object) + ") at " + object.getTile() + " with options: " + Arrays.toString(options) + " " + (result ? "SUCCESS" : "FAIL"));
            WaitFor.milliseconds(250,800);
        }

        return result;
    }

    private static boolean isStrongholdDoor(GameObject object){
        List<String> doorNames = Arrays.asList("Gate of War", "Rickety door", "Oozing barrier", "Portal of Death");
        return  doorNames.contains(object.getName());
    }



    private static void handleStrongholdQuestions() {
        NPCInteraction.handleConversation("Use the Account Recovery System.",
            "No, you should never buy an account.",
            "Nobody.",
            "Don't tell them anything and click the 'Report Abuse' button.",
            "Decline the offer and report that player.",
            "Me.",
            "Only on the RuneScape website.",
            "Report the incident and do not click any links.",
            "Authenticator and two-step login on my registered email.",
            "No way! You'll just take my gold for your own! Reported!",
            "No.",
            "Don't give them the information and send an 'Abuse Report'.",
            "Don't give them my password.",
            "The birthday of a famous person or event.",
            "Through account settings on runescape.com.",
            "Secure my device and reset my RuneScape password.",
            "Report the player for phishing.",
            "Don't click any links, forward the email to reportphishing@jagex.com.",
            "Inform Jagex by emailing reportphishing@jagex.com.",
            "Don't give out your password to anyone. Not even close friends.",
            "Politely tell them no and then use the 'Report Abuse' button.",
            "Set up 2 step authentication with my email provider.",
            "No, you should never buy a RuneScape account.",
            "Do not visit the website and report the player who messaged you.",
            "Only on the RuneScape website.",
            "Don't type in my password backwards and report the player.",
            "Virus scan my device then change my password.",
            "No, you should never allow anyone to level your account.",
            "Don't give out your password to anyone. Not even close friends.",
            "Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
            "Report the stream as a scam. Real Jagex streams have a 'verified' mark",
            "Read the text and follow the advice given.",
            "No way! I'm reporting you to Jagex!",
            "Talk to any banker in RuneScape.",
            "Secure my device and reset my RuneScape password.",
            "Secure my device and reset my password.",
            "Delete it - it's a fake!",
            "Use the account management section on the website.",
            "Politely tell them no and then use the 'Report Abuse' button.",
            "Through account setting on oldschool.runescape.com",
            "Through account setting on oldschool.runescape.com.",
            "Nothing, it's a fake.",
            "Only on the Old School RuneScape website.",
            "Don't share your information and report the player.");
    }


    private static boolean isClosedTrapDoor(GameObject object, String[] options){
        return  (object.getName().equals("Trapdoor") && Arrays.asList(options).contains("Open"));
    }

    private static boolean handleTrapDoor(GameObject object){
        if (getActions(object).contains("Open")){
            if (!InteractionHelper.click(object, "Open", () -> {
                GameObject[] objects = GameObjectHelper.find(15, GameObjectHelper.actionsContainsPredicate("Climb-down").and(GameObjectHelper.inAreaPredicate(new Area(object, 2)), true));
                if (objects.length > 0 && getActions(objects[0]).contains("Climb-down")){
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            })){
                return false;
            } else {
                GameObject[] objects = GameObjectHelper.find(15, GameObjectHelper.actionsContainsPredicate("Climb-down").and(GameObjectHelper.inAreaPredicate(new Area(object, 2)), true));
                return objects.length > 0 && handleTrapDoor(objects[0]);
            }
        }
        getInstance().log("Interacting with (" + object.getDefinition().getName() + ") at " + object.getTile() + " with option: Climb-down");
        return InteractionHelper.click(object, "Climb-down");
    }

    public static List<String> getActions(GameObject object){
        List<String> list = new ArrayList<>();
        if (object == null){
            return list;
        }
        String[] actions = object.getActions();
        if (actions == null){
            return list;
        }
        return Arrays.asList(actions);
    }

    @Override
    public String getName() {
        return "Object Handler";
    }

    private static List<Integer> SLASH_WEAPONS = new ArrayList<>(Arrays.asList(1,4,9,10,12,17,20,21));

    private static boolean canLeftclickWeb(){
        return SLASH_WEAPONS.contains(PlayerSettings.getBitValue(357)) || Inventory.contains("Knife");
    }
    private static boolean useBladeOnWeb(GameObject web) {
        String toolTip = Menu.getToolTip();
        if(toolTip == null || !toolTip.contains("->")){
            Item slashable = Inventory.get((i) -> StringHelper.nameContains(i.getName(), "whip", "sword", "dagger", "claws", "scimitar", " axe", "knife", "halberd", "machete", "rapier"));
            if(slashable == null || !slashable.interact("Use"))
                return false;
        }
        return InteractionHelper.click(web, toolTip);
    }

}
