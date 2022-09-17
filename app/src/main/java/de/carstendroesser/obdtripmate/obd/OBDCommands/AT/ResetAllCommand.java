package de.carstendroesser.obdtripmate.obd.OBDCommands.AT;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class ResetAllCommand extends ATCommand {

    public ResetAllCommand() {
        super("Reset all", "atz");
    }

    @Override
    protected boolean isValidData() {
        return mResponse.toLowerCase().contains("elm");
    }

}
