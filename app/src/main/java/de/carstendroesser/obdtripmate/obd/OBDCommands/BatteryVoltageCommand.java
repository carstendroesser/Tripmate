package de.carstendroesser.obdtripmate.obd.OBDCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class BatteryVoltageCommand extends OBDCommand {

    public BatteryVoltageCommand() {
        super("Battery voltage", "at rv", "V");
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replace(">", "");
        response = response.replace("V", "");
        return response;
    }

    @Override
    protected boolean isValidData() {
        Pattern pattern = Pattern.compile("\\d\\.\\d");
        Matcher matcher = pattern.matcher(mResponse);
        return matcher.find();
    }

}
