package de.carstendroesser.obdtripmate.obd.OBDCommands.PID;

import java.util.ArrayList;
import java.util.List;

import de.carstendroesser.obdtripmate.obd.OBDCommands.CommandMapper;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class SupportedPidsCommand extends PIDCommand {

    public enum RANGE {
        RANGE_20("0100", 0),
        RANGE_40("0120", 1),
        RANGE_60("0140", 2),
        RANGE_80("0160", 3),
        RANGE_A0("0180", 4),
        RANGE_C0("01A0", 5),
        RANGE_E0("01C0", 6);

        public String command;
        public int level;

        RANGE(String pCommand, int pLevel) {
            command = pCommand;
            level = pLevel;
        }

    }

    private RANGE mRange;

    public SupportedPidsCommand(RANGE pRange) {
        super("Supported PIDS", pRange.command, null);
        mRange = pRange;
    }

    @Override
    protected String getCalculatedResult() {
        return "not supported";
    }

    public List<OBDCommand> getSupportedCommands() {
        if (isValidData()) {
            String response = mResponse.replaceAll("SEARCHING...", "").replaceAll(">", "");
            String[] lines = response.split(FormatUtils.sumOfHex(getCommandCode(), "4000"));

            char[] binarySupportedCommands = "00000000000000000000000000000000".toCharArray();

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].length() == 0) {
                    continue;
                }
                String binaryLine = FormatUtils.hexToBinary(lines[i]);
                for (int j = 0; j < binaryLine.length(); j++) {
                    if (binaryLine.charAt(j) == '1') {
                        binarySupportedCommands[j] = '1';
                    }
                }
            }

            List<OBDCommand> supportedCommands = new ArrayList<>();
            for (int i = 0; i < binarySupportedCommands.length; i++) {
                if (binarySupportedCommands[i] == '1') {
                    OBDCommand command = CommandMapper.getCommandByPidInDec(i + 1 + 32 * mRange.level);
                    if (command != null) {
                        supportedCommands.add(command);
                    }
                }
            }

            return supportedCommands;
        } else {
            return null;
        }
    }
}
