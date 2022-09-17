package de.carstendroesser.obdtripmate.obd.OBDCommands.AT;

/**
 * Created by carstendrosser on 22.06.17.
 */

public class DescribeProtocolCommand extends ATCommand {

    public DescribeProtocolCommand() {
        super("Describe protocol", "AT DP");
    }

    @Override
    protected boolean isValidData() {
        if (mResponse.toLowerCase().contains("auto")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse.replaceAll(">", "").replaceAll("AUTO,", "");
        return response;
    }
}
