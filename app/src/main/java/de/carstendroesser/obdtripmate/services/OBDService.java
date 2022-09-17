package de.carstendroesser.obdtripmate.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

import de.carstendroesser.obdtripmate.LocationProvider;
import de.carstendroesser.obdtripmate.connections.BluetoothConnection;
import de.carstendroesser.obdtripmate.connections.Connection;
import de.carstendroesser.obdtripmate.devices.Device;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.obd.OBDEngine;
import de.carstendroesser.obdtripmate.utils.NotificationFactory;

import static de.carstendroesser.obdtripmate.obd.OBDEngine.OBDEngineObserver;

/**
 * Created by carstendrosser on 20.06.17.
 */

public class OBDService extends Service implements Connection.ConnectionObserver, OBDEngineObserver {

    // MEMBERS

    private OBDEngine mOBDEngine;

    // SERVICE

    @Override
    public void onCreate() {
        // make it persistent and try to get not killed by the android system
        startForeground(NotificationFactory.NOTIFICATION_ID, NotificationFactory.getServiceNotification(getApplicationContext(), "running"));

        // setup a connection for the OBDEngine
        BluetoothConnection connection = new BluetoothConnection(this);
        connection.addConnectionObserver(this);

        // init an OBDEngine and listen to events
        mOBDEngine = new OBDEngine();
        mOBDEngine.setConnection(connection);
        mOBDEngine.setLocationProvider(LocationProvider.getInstance(this));
        mOBDEngine.addEngineObserver(this);

        // start the locationprovider
        LocationProvider.getInstance(this).start();
    }

    @Override
    public IBinder onBind(Intent pIntent) {
        return new ServiceBinder();
    }

    // PRIVATE API

    /**
     * Updates the notification text in the notification tray.
     *
     * @param pText the message to show in the notification
     */
    private void notify(String pText) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NotificationFactory.NOTIFICATION_ID, NotificationFactory.getServiceNotification(this, pText));
    }

    // PUBLIC API

    /**
     * Gets the set OBDEngine
     *
     * @return
     */
    public OBDEngine getObdEngine() {
        return mOBDEngine;
    }

    // CONNECTION CALLBACKS

    @Override
    public void onConnectionAbort() {
        notify("Connection abort");
    }

    @Override
    public void onConnectionEstablished() {
        notify("Connection enabled");
    }

    @Override
    public void onConnectionEnableAttemptFailed() {
        notify("Enabling connection failed");
    }

    @Override
    public void onConnectToDeviceAttempt(Device pDevice, boolean pConnected) {
        if (pConnected) {
            notify("Connected to device " + pDevice.getName());
        } else {
            notify("Connecting to device failed");
        }
    }

    @Override
    public void onDeviceConnectionLost() {
        notify("Connection to device lost");
    }

    // OBDENGINE CALLBACKS

    @Override
    public void onEngineLoopStart() {
        notify("Livedata started");
    }

    @Override
    public void onEngineLoopPass(List<OBDCommand> pCommands, Location pLocation) {
        // empty
    }

    @Override
    public void onEngineLoopError() {
        notify("Livedata error");
    }

    @Override
    public void onEngineLoopStopped() {
        notify("Livedata stopped");
    }

    @Override
    public void onEngineSetupComplete(String pVin, String pProtocol, String pOBDStandard, String pBatteryVoltage) {
        notify("OBD setup completed");
    }

    @Override
    public void onEngineSetupError() {
        notify("Error when trying to setup");
    }

    @Override
    public void onEngineSetupStart() {
        notify("OBD setup started");
    }

    // BINDERS

    public class ServiceBinder extends Binder {
        public OBDService getService() {
            return OBDService.this;
        }
    }

}
