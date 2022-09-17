package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 26.06.17.
 */

public class MonitorCommand extends PIDCommand {

    public MonitorCommand() {
        super("Monitoring MIL", "0101", null);
    }

    @Override
    protected String getCalculatedResult() {
        return (isMilOn() ? "MIL on" : "MIL off");
    }

    public boolean isMilOn() {
        if (getBinaryResponse() != null) {
            if (getBinaryResponse()[0] == '1') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private char[] getBinaryResponse() {
        if (isValidData()) {
            String response = mResponse.replaceAll("SEARCHING...", "").replaceAll(">", "");
            String[] lines = response.split(FormatUtils.sumOfHex(getCommandCode(), "4000"));

            // response byte count = 4 = 32bit

            char[] binaryresponse = "00000000000000000000000000000000".toCharArray();

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].length() == 0) {
                    continue;
                }
                String binaryLine = FormatUtils.hexToBinary(lines[i]);
                for (int j = 0; j < binaryLine.length(); j++) {
                    if (binaryLine.charAt(j) == '1') {
                        binaryresponse[j] = '1';
                    }
                }
            }

            return binaryresponse;
        } else {
            return null;
        }
    }

}
