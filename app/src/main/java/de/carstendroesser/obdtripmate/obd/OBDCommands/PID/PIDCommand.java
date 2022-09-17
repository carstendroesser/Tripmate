package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 06.06.17.
 */

public abstract class PIDCommand extends OBDCommand {

    protected PIDCommand(String pName, String pCommand, String pUnit) {
        super(pName, pCommand, pUnit);
    }

    @Override
    protected boolean isValidData() {
        if (mResponse.toLowerCase().contains(FormatUtils.sumOfHex(getCommandCode(), "4000").toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

}
