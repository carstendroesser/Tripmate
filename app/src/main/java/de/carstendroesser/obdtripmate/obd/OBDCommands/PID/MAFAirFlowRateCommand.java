package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class MAFAirFlowRateCommand extends PIDCommand {

    public MAFAirFlowRateCommand() {
        super("MAF Air flow rate", "0110", "g/s");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");

        int a = Integer.parseInt(response.substring(response.length() - 4, response.length() - 2).trim(), 16);
        int b = Integer.parseInt(response.substring(response.length() - 2, response.length()).trim(), 16);

        return "" + (a * 256 + b) / 100;
    }
}
