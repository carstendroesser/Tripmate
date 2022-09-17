package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 20.06.17.
 */

public class FuelRailGaugePressureCommand extends PIDCommand {

    public FuelRailGaugePressureCommand() {
        super("Fuel rail gauge pressure", "0123", "kPa");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");

        int a = Integer.parseInt(response.substring(response.length() - 4, response.length() - 2).trim(), 16);
        int b = Integer.parseInt(response.substring(response.length() - 2, response.length()).trim(), 16);

        int pressure = 10 * (256 * a + b);
        return "" + pressure;
    }

}
