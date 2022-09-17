package de.carstendroesser.obdtripmate.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class Trip {

    // MEMBERS

    private long mId;
    private long mStartTime;
    private long mEndTime;
    private LatLng mStartPoint;
    private LatLng mEndPoint;
    private ArrayList<Loop> mLoops;

    // CONSTRUCTORS

    /**
     * Creates a trip.
     *
     * @param pId         the id of this trip within the database
     * @param pStartTime  the starttime of this trip
     * @param pEndTime    the endtime of this trip
     * @param pStartPoint the startpoint of this trip
     * @param pEndPoint   the endpoint of this trip
     */
    public Trip(long pId, long pStartTime, long pEndTime, LatLng pStartPoint, LatLng pEndPoint) {
        mId = pId;
        mStartTime = pStartTime;
        mEndTime = pEndTime;
        mStartPoint = pStartPoint;
        mEndPoint = pEndPoint;

        mLoops = new ArrayList<Loop>();
    }

    // PUBLIC API

    public LatLng getStartPoint() {
        return mStartPoint;
    }

    public LatLng getEndPoint() {
        return mEndPoint;
    }

    public long getId() {
        return mId;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setId(long pId) {
        mId = pId;
    }

    public void setEndTime(long pEndTime) {
        mEndTime = pEndTime;
    }

    public void setEndPoint(LatLng pEndPoint) {
        mEndPoint = pEndPoint;
    }

    public void setLoops(List<Loop> pLoops) {
        mLoops.clear();
        mLoops.addAll(pLoops);
    }

    public ArrayList<Loop> getLoops() {
        return mLoops;
    }

    public long getDuration() {
        if (mStartTime > mEndTime) {
            return 0;
        } else {
            return mEndTime - mStartTime;
        }
    }

}
