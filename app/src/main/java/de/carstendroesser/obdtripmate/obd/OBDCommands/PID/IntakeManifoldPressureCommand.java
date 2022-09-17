package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class IntakeManifoldPressureCommand extends PIDCommand {

    public IntakeManifoldPressureCommand() {
        super("Intake manifold pressure", "010B", "kPa");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");
        String data = response.substring(response.length() - 2, response.length());
        return "" + (Integer.parseInt(data.trim(), 16));
    }

}
