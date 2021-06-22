package org.lostclient.api.wrappers.walking.dax_api.shared.helpers.questing;

import org.lostclient.api.wrappers.walking.dax_api.shared.helpers.InterfaceHelper;
import org.lostclient.api.wrappers.widgets.WidgetChild;

import java.util.ArrayList;
import java.util.Arrays;


public class QuestHelper {

    private static final int QUEST_MASTER_INTERFACE = 399;

    public static ArrayList<Quest> getAllQuests(){
        ArrayList<Quest> quests = new ArrayList<>();
        for (WidgetChild rsInterface : InterfaceHelper.getAllInterfaces(QUEST_MASTER_INTERFACE)){
            String[] actions = rsInterface.getActions();
            if (actions == null){
                continue;
            }
            System.out.println(rsInterface.getText());
            if (Arrays.asList(actions).contains("Read Journal:")){
                quests.add(new Quest(rsInterface.getText(), Quest.State.getState(rsInterface.getWidget().getColor())));
            }
        }
        return quests;
    }

}
