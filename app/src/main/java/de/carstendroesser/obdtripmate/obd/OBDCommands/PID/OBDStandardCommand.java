package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class OBDStandardCommand extends PIDCommand {

    public OBDStandardCommand() {
        super("OBD Standards", "011c", null);
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse;
        response = response.replaceAll(">", "");
        String data = response.substring(response.length() - 2, response.length());
        int obdCode = Integer.parseInt(FormatUtils.hexToDecimal(data));

        switch (obdCode) {
            case 6:
                return "EOBD (6)";
            // just implemented the neccessary things
        }
        return "" + obdCode;
    }

}
