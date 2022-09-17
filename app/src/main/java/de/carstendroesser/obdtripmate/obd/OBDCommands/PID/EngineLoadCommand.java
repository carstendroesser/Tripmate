package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class EngineLoadCommand extends PIDCommand {

    public EngineLoadCommand() {
        super("Calculated Engine Load", "0104", "%");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");
        String data = response.substring(response.length() - 2, response.length());
        data = "" + ((int) (Integer.parseInt(data.trim(), 16) / 2.55));
        return data;
    }
}
