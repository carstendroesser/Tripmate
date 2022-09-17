package de.carstendroesser.obdtripmate.obd.OBDCommands;

import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 31.05.17.
 */

public abstract class OBDCommand {

    private String mCommand;
    private String mUnit;
    private String mName;
    private long mResponseTime;

    protected String mResponse;

    protected OBDCommand(String pName, String pCommand, String pUnit) {
        mName = pName;
        mCommand = pCommand;
        mUnit = pUnit;
    }

    public final String getCommandCode() {
        return mCommand;
    }

    public String getResponse() {
        return mResponse;
    }

    public final void setResponse(String pResponse, long pResponseTime) {
        mResponse = FormatUtils.removeAllSpace(pResponse);
        mResponseTime = pResponseTime;
    }

    public final long getResponseTime() {
        return mResponseTime;
    }

    public final String getValue() {
        if (isValidData()) {
            return getCalculatedResult();
        } else {
            return null;
        }
    }

    public final String getUnit() {
        return mUnit;
    }

    public final String getName() {
        return mName;
    }

    protected abstract boolean isValidData();

    protected abstract String getCalculatedResult();

}
