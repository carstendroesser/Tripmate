package de.carstendroesser.obdtripmate.obd.OBDCommands.AT;

import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 06.06.17.
 */

public abstract class ATCommand extends OBDCommand {

    ATCommand(String pName, String pCommand) {
        super(pName, pCommand, null);
    }

    @Override
    protected boolean isValidData() {
        return mResponse.contains("OK");
    }

    @Override
    protected String getCalculatedResult() {
        return mResponse;
    }

}
