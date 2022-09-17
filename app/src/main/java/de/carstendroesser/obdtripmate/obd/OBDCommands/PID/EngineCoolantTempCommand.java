package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 02.06.17.
 */

public class EngineCoolantTempCommand extends PIDCommand {

    public EngineCoolantTempCommand() {
        super("Coolant temperature", "0105", "°C");
    }

    @Override
    public String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");

        String data = response.substring(response.length() - 2, response.length());
        data = "" + (Integer.parseInt(data.trim(), 16) - 40);
        return data;
    }

}
