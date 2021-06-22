package org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.impl;

import org.lostclient.api.utilities.math.Calculations;
import org.lostclient.api.wrappers.walking.Walking;
import org.lostclient.api.wrappers.walking.dax_api.walker.handlers.passive_action.PassiveAction;
import org.lostclient.api.wrappers.walking.dax_api.walker.models.enums.ActionResult;

public class RunEnergyPassiveAction implements PassiveAction {

    private int random;

    public RunEnergyPassiveAction() {
//        random = Calculations.randomSD(3, 20, 10, 3);
        random = Calculations.random(3, 20);
    }

    @Override
    public boolean shouldActivate() {
        return !Walking.isRunEnabled() && Walking.getRunEnergy() > random;
    }

    @Override
    public ActionResult activate() {
//        random = Calculations.randomSD(3, 20, 10, 3);
        random = Calculations.random(3, 20);

        return Walking.isRunEnabled() ? ActionResult.CONTINUE : ActionResult.FAILURE;
    }

}
