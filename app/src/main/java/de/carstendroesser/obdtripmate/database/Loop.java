package de.carstendroesser.obdtripmate.database;

import android.location.Location;

import java.util.ArrayList;

import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class Loop {

    // MEMBERS

    private Location mLocation;
    private long mTimestamp;
    private long mId;
    private ArrayList<OBDCommand> mCommands;

    // CONSTRUCTORS

    /**
     * Creates a loop.
     *
     * @param pId        the loops id within the database
     * @param pLocation  the location this loop was received
     * @param pTimeStamp the timestamp this loop was received
     * @param pCommands  the commands that belong to this loop
     */
    public Loop(int pId, Location pLocation, long pTimeStamp, ArrayList<OBDCommand> pCommands) {
        mLocation = pLocation;
        mTimestamp = pTimeStamp;
        mCommands = new ArrayList<>();
        mCommands.addAll(pCommands);
        mId = pId;
    }

    // PUBLIC API

    public Location getLocation() {
        return mLocation;
    }

    public long getId() {
        return mId;
    }

    public long getTimeStamp() {
        return mTimestamp;
    }

    public ArrayList<OBDCommand> getCommands() {
        return mCommands;
    }

}
