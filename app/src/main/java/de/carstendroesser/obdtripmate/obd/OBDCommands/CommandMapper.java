package de.carstendroesser.obdtripmate.obd.OBDCommands;

import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineCoolantTempCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineLoadCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.FuelRailGaugePressureCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.IntakeAirTempCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.IntakeManifoldPressureCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.MAFAirFlowRateCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.MonitorCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.OBDStandardCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.RPMCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.SpeedCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.SupportedPidsCommand;
import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 12.06.17.
 */

public class CommandMapper {

    public static OBDCommand getCommandByPidInDec(int pNumber) {
        switch (pNumber) {
            case 4:
                return new EngineLoadCommand();
            case 5:
                return new EngineCoolantTempCommand();
            case 11:
                return new IntakeManifoldPressureCommand();
            case 12:
                return new RPMCommand();
            case 13:
                return new SpeedCommand();
            case 15:
                return new IntakeAirTempCommand();
            case 16:
                return new MAFAirFlowRateCommand();
            case 28:
                return new OBDStandardCommand();
            case 32:
                return new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_40);
            case 35:
                return new FuelRailGaugePressureCommand();
        }
        return null;
    }

    public static OBDCommand getCommandByCommandCode(String pCode) {
        switch (FormatUtils.removeAllSpace(pCode).toLowerCase()) {
            case "0101":
                return new MonitorCommand();
            case "atrv":
                return new BatteryVoltageCommand();
            case "0104":
                return new EngineLoadCommand();
            case "0105":
                return new EngineCoolantTempCommand();
            case "010B":
                return new IntakeManifoldPressureCommand();
            case "010c":
                return new RPMCommand();
            case "010d":
                return new SpeedCommand();
            case "010f":
                return new IntakeAirTempCommand();
            case "0110":
                return new MAFAirFlowRateCommand();
            case "011c":
                return new OBDStandardCommand();
            case "0140":
                return new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_40);
            case "0123":
                return new FuelRailGaugePressureCommand();
        }
        return null;
    }

}
