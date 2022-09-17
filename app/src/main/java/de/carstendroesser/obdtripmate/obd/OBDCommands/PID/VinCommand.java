package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

import de.carstendroesser.obdtripmate.utils.FormatUtils;

import static de.carstendroesser.obdtripmate.utils.FormatUtils.hexToString;

/**
 * Created by carstendrosser on 11.06.17.
 */

public class VinCommand extends PIDCommand {


    public VinCommand() {
        super("VIN", "0902", null);
    }

    @Override
    protected String getCalculatedResult() {
        String response = mResponse.replaceAll("SEARCHING...", "").replaceAll(">", "");

        if (response.contains(":")) {
            // first three characters contain the count of the following bytes
            int countOfBytes = Integer.parseInt(FormatUtils.hexToDecimal(response.substring(0, 3)));

            // remove all line identifiers as 0:, 1: and 2:
            response = response.replaceAll("0:", "").replaceAll("1:", "").replaceAll("2:", "");

            // only take the count of bytes and remove the rest such as count of bytes
            response = response.substring(response.length() - countOfBytes * 2 + 2);
        } else {
            response = response.replaceAll("49020.", "");
        }
        return FormatUtils.removeAllSpace(hexToString(response));
    }

}
