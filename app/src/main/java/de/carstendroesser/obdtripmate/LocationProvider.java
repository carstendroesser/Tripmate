package de.carstendroesser.obdtripmate;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

/**
 * Created by carstendrosser on 24.06.17.
 */

public class LocationProvider {

    // MEMBERS

    private Context mContext;
    private LocationParams.Builder mBuilder;
    private List<OnLocationUpdatedListener> mLocationUpdatedListener;
    private static LocationProvider mInstance;

    // CONSTRUCTORS

    /**
     * Make the constructor private to prevent initialistions as this is a singleton.
     *
     * @param pContext we need that
     */
    private LocationProvider(Context pContext) {
        mContext = pContext;
        mLocationUpdatedListener = new ArrayList<>();

        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;
        long trackingInterval = 200;
        float trackingDistance = 0;

        mBuilder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(trackingInterval);
    }

    // PUBLIC API

    /**
     * Gets the single instance of this class.
     *
     * @param pContext we need that
     * @return the singleton
     */
    public static LocationProvider getInstance(Context pContext) {
        if (mInstance == null) {
            mInstance = new LocationProvider(pContext);
        }
        return mInstance;
    }

    /**
     * Adds a listener to get notified about location updates.
     *
     * @param pListener the listener to notify
     */
    public void addLocationUpdatedListener(OnLocationUpdatedListener pListener) {
        if (!mLocationUpdatedListener.contains(pListener)) {
            mLocationUpdatedListener.add(pListener);
        }
    }

    /**
     * Removes a listener that shall not be notified anymore.
     *
     * @param pListener the listener to remove
     */
    public void removeLocationUpdatedListener(OnLocationUpdatedListener pListener) {
        mLocationUpdatedListener.remove(pListener);
    }

    /**
     * Starts the location checker.
     */
    public void start() {
        SmartLocation.with(mContext).location().continuous().config(mBuilder.build()).start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location pLocation) {
                for (OnLocationUpdatedListener listener : mLocationUpdatedListener) {
                    if (listener != null) {
                        listener.onLocationUpdated(pLocation);
                    }
                }
            }
        });
    }

    /**
     * Stops the location checker.
     */
    public void stop() {
        SmartLocation.with(mContext).location().stop();
    }

}
