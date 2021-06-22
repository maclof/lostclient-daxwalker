package org.lostclient.api.wrappers.walking.dax_api.walker_engine.interaction_handling;

import org.lostclient.api.accessor.NPCs;
import org.lostclient.api.accessor.Players;
import org.lostclient.api.utilities.MethodProvider;
import org.lostclient.api.utilities.internal.RSPlayer;
import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.input.Keyboard;
import org.lostclient.api.wrappers.interactives.Character;
import org.lostclient.api.wrappers.interactives.NPC;
import org.lostclient.api.wrappers.interactives.Player;
import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.Loggable;
import org.lostclient.api.wrappers.walking.dax_api.walker_engine.WaitFor;
import org.lostclient.api.wrappers.widgets.WidgetChild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class NPCInteraction implements Loggable {

    public static String[] GENERAL_RESPONSES = {"Sorry, I'm a bit busy.", "OK then.", "Yes.", "Okay..."};

    private static final int
            ITEM_ACTION_INTERFACE_WINDOW = 193,
            NPC_TALKING_INTERFACE_WINDOW = 231,
            PLAYER_TALKING_INTERFACE_WINDOW = 217,
            SELECT_AN_OPTION_INTERFACE_WINDOW = 219,
            SINGLE_OPTION_DIALOGUE_WINDOW = 229;

    private static final int[] ALL_WINDOWS = new int[]{ITEM_ACTION_INTERFACE_WINDOW, NPC_TALKING_INTERFACE_WINDOW, PLAYER_TALKING_INTERFACE_WINDOW, SELECT_AN_OPTION_INTERFACE_WINDOW, SINGLE_OPTION_DIALOGUE_WINDOW};


    private static NPCInteraction instance;

    private NPCInteraction(){

    }

    private static NPCInteraction getInstance(){
        return instance != null ? instance : (instance = new NPCInteraction());
    }

    /**
     *
     * @param predicate
     * @param talkOptions
     * @param replyAnswers
     * @return
     */
    public static boolean talkTo(Predicate<NPC> predicate, String[] talkOptions, String[] replyAnswers) {
        if (!clickNpcAndWaitChat(predicate, talkOptions)){
            return false;
        }
        handleConversation(replyAnswers);
        return true;
    }

    /**
     *
     * @param predicate
     * @param options
     * @return
     */
    public static boolean clickNpcAndWaitChat(Predicate<NPC> predicate, String... options) {
        return clickNpc(predicate, options) && waitForConversationWindow();
    }

    public static boolean clickNpc(Predicate<NPC> predicate, String... options) {
        NPC npc = NPCs.closest(predicate);
        if (npc == null) {
            getInstance().log("Cannot find NPC.");
            return false;
        }
        return InteractionHelper.click(npc, options);
    }

    public static boolean waitForConversationWindow(){
        Player player = Players.localPlayer();
        Character rsCharacter = null;
        if (player != null){
            rsCharacter = player.getTarget();
        }
        return WaitFor.condition(rsCharacter != null ? WaitFor.getMovementRandomSleep(rsCharacter) : 10000, () -> {
            if (isConversationWindowUp()) {
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

    public static boolean isConversationWindowUp(){
        return Arrays.stream(ALL_WINDOWS).anyMatch(InterfaceHelper::isInterfaceSubstantiated);
    };

    public static void handleConversationRegex(String regex){
        while (true){
            if (WaitFor.condition(Calculations.random(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                continue;
            }

            List<WidgetChild> selectableOptions = getAllOptions(regex);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(100);
                continue;
            }

//            MethodProvider.sleep(Calculations.randomSD(350, 2250, 775, 350));
            MethodProvider.sleep(Calculations.random(350, 2250));
            getInstance().log("Replying with option: " + selectableOptions.get(0).getText());
            Keyboard.type(selectableOptions.get(0).getIndex() + "", true);
            waitForNextOption();
        }
    }

    public static void handleConversation(String... options){
        getInstance().log("Handling... " + Arrays.asList(options));
        List<String> blackList = new ArrayList<>();
        int limit = 0;
        while (limit++ < 50){
            if (WaitFor.condition(Calculations.random(650, 800), () -> isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                getInstance().log("Conversation window not up.");
                break;
            }

            if (getClickHereToContinue() != null){
                clickHereToContinue();
                limit = 0;
                continue;
            }

            List<WidgetChild> selectableOptions = getAllOptions(options);
            if (selectableOptions == null || selectableOptions.size() == 0){
                WaitFor.milliseconds(150);
                continue;
            }

            for (WidgetChild selected : selectableOptions){
                if(blackList.contains(selected.getText())){
                    continue;
                }
//                MethodProvider.sleep(Calculations.randomSD(350, 2250, 775, 350));
                MethodProvider.sleep(Calculations.random(350, 2250));
                getInstance().log("Replying with option: " + selected.getText());
                blackList.add(selected.getText());
                Keyboard.type(selected.getIndex() + "", true);
                waitForNextOption();
                limit = 0;
                break;
            }
            MethodProvider.sleep(20,40);
        }
        if(limit > 50){
            getInstance().log("Reached conversation limit.");
        }
    }

    /**
     *
     * @return Click here to continue conversation interface
     */
    private static WidgetChild getClickHereToContinue(){
        List<WidgetChild> list = getConversationDetails();
        if (list == null){
            return null;
        }
        Optional<WidgetChild> optional = list.stream().filter(rsInterface -> rsInterface.getText().equals("Click here to continue")).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * Presses space bar
     */
    private static void clickHereToContinue(){
        getInstance().log("Clicking continue.");
        Keyboard.type(" ", false);
        waitForNextOption();
    }

    /**
     * Waits for chat conversation text change.
     */
    private static void waitForNextOption(){
        List<String> interfaces = getAllInterfaces().stream().map(WidgetChild::getText).collect(Collectors.toList());
        WaitFor.condition(5000, () -> {
            if (!interfaces.equals(getAllInterfaces().stream().map(WidgetChild::getText).collect(Collectors.toList()))){
               return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        });
    }

    /**
     *
     * @return List of all reply-able interfaces that has valid text.
     */
    private static List<WidgetChild> getConversationDetails(){
        for (int window : ALL_WINDOWS){
            List<WidgetChild> details = InterfaceHelper.getAllInterfaces(window).stream().filter(rsInterfaceChild -> {
                if (rsInterfaceChild.getTextureId() != -1) {
                    return false;
                }
                String text = rsInterfaceChild.getText();
                return text != null && text.length() > 0;
            }).collect(Collectors.toList());
            if (details.size() > 0) {
                getInstance().log("Conversation Options: [" + details.stream().map(WidgetChild::getText).collect(
		                Collectors.joining(", ")) + "]");
                return details;
            }
        }
        return null;
    }

    /**
     *
     * @return List of all Chat interfaces
     */
    private static List<WidgetChild> getAllInterfaces(){
        ArrayList<WidgetChild> interfaces = new ArrayList<>();
        for (int window : ALL_WINDOWS) {
            interfaces.addAll(InterfaceHelper.getAllInterfaces(window));
        }
        return interfaces;
    }

    /**
     *
     * @param regex
     * @return list of conversation clickable options that matches {@code regex}
     */
    private static List<WidgetChild> getAllOptions(String regex){
        List<WidgetChild> list = getConversationDetails();
        return list != null ? list.stream().filter(rsInterface -> rsInterface.getText().matches(regex)).collect(
		        Collectors.toList()) : null;
    }

    /**
     *
     * @param options
     * @return list of conversation clickable options that is contained in options.
     */
    private static List<WidgetChild> getAllOptions(String... options){
        final List<String> optionList = Arrays.stream(options).map(String::toLowerCase).collect(Collectors.toList());
        List<WidgetChild> list = getConversationDetails();
        return list != null ? list.stream().filter(rsInterface -> optionList.contains(rsInterface.getText().trim().toLowerCase())).collect(
		        Collectors.toList()) : null;
    }

    @Override
    public String getName() {
        return "NPC Interaction";
    }

}
