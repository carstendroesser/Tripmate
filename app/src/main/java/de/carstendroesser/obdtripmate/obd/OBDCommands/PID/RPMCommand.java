package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 02.06.17.
 */

public class RPMCommand extends PIDCommand {

    public RPMCommand() {
        super("RPM", "010C", "rpm");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");

        int a = Integer.parseInt(response.substring(response.length() - 4, response.length() - 2).trim(), 16);
        int b = Integer.parseInt(response.substring(response.length() - 2, response.length()).trim(), 16);

        int rpm = (a * 256 + b) / 4;
        return "" + rpm;
    }

}
