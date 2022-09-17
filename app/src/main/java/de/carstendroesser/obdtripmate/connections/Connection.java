package de.carstendroesser.obdtripmate.connections;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.carstendroesser.obdtripmate.devices.Device;

import static de.carstendroesser.obdtripmate.connections.Connection.ConnectionObserver.ConnectionAction;

/**
 * Created by carstendrosser on 01.06.17.
 */

public abstract class Connection {

    // MEMBERS

    protected Context mContext;
    protected Device mConnectedDevice;
    private List<ConnectionObserver> mConnectionObservers;

    // CONSTRUCTORS

    /**
     * Package private Constructor to make sure it cannot be initialised.
     *
     * @param pContext we need that
     */
    Connection(Context pContext) {
        mContext = pContext;
        mConnectionObservers = new ArrayList<>();
    }

    // PUBLIC API

    /**
     * Adds an observer to listen to connection events.
     *
     * @param pObserver the observer to add
     */
    public final void addConnectionObserver(ConnectionObserver pObserver) {
        mConnectionObservers.add(pObserver);
    }

    /**
     * Removes a given observer which shall not be notified about
     * connection events anymore.
     *
     * @param pObserver the observer to remove
     */
    public final void removeConnectionObserver(ConnectionObserver pObserver) {
        mConnectionObservers.remove(pObserver);
    }

    /**
     * Tries to connect to a given device, not blocking the UI-Thread.
     * The connectioncallback's methods will be fired to inform
     * about success or error when trying to connect.
     *
     * @param pDevice the device to connect to
     */
    public void connectToDevice(final Device pDevice) {
        // if we are already connected, disconnect first
        if (isConnected()) {
            mConnectedDevice.disconnect();
            notifyListeners(ConnectionAction.CONNECTION_DEVICE_LOST, mConnectedDevice, false);
            mConnectedDevice = null;
        }

        // try to connect async to the device
        new Thread(new Runnable() {
            @Override
            public void run() {
                // connect() blocks ui
                final boolean connected = pDevice.connect();

                // back to UI-thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (connected) {
                            mConnectedDevice = pDevice;
                        }
                        notifyListeners(ConnectionAction.CONNECT_DEVICE_ATTEMPT, pDevice, connected);
                    }
                });
            }
        }).start();
    }

    /**
     * Checks if a device is connected.
     *
     * @return true if a device is connected, false otherwise
     */
    public final boolean isConnected() {
        return mConnectedDevice != null && mConnectedDevice.isConnected();
    }

    /**
     * Gets the currently connected device.
     *
     * @return the currently connected device or null.
     */
    public final Device getConnectedDevice() {
        return mConnectedDevice;
    }

    /**
     * Blocks the UI-Thread. Tries to read the inputstream of the connected
     * device as long as the last character read was '>' or an exception was
     * thrown. If an exception was thrown, the connectioncallback's
     * onConnectionAbort() method is called to inform about a connection error.
     *
     * @return a Pair of boolean, indicating if success or error, and a String
     * containting the response or errormessage
     */
    public final Pair<Boolean, String> receive() {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while (true) {
            try {
                bytes += mConnectedDevice.getInputStream().read(buffer, bytes, 20);
                final String readMessage = new String(buffer, 0, bytes);
                if (readMessage.endsWith(">")) {
                    return new Pair<>(true, readMessage);
                }
            } catch (Exception pException) {
                pException.printStackTrace();
                shutdown();
                return new Pair<>(false, "exception caught");
            }
        }
    }

    /**
     * Blocks the UI-Thread. Tries to send a given message to the connected
     * device, if connected. Will fire the connectioncallback's
     * onConnectionAbort() to inform about an error when trying to write.
     *
     * @param pMessage the message to send
     */
    public final void send(String pMessage) {
        if (mConnectedDevice != null && mConnectedDevice.isConnected()) {
            try {
                mConnectedDevice.getOutputStream().write((pMessage).getBytes());
                return;
            } catch (IOException pException) {
                pException.printStackTrace();
            }
        }
        shutdown();
    }

    /**
     * Closes the connection to the connected device if existing.
     */
    public final void shutdown() {
        if (mConnectedDevice != null) {
            mConnectedDevice.disconnect();
            mConnectedDevice = null;
        }
        notifyListeners(ConnectionAction.CONNECTION_ABORT, null, false);
    }

    // PROTECTED API

    protected final void notifyListeners(final ConnectionAction pAction, final Device pDevice, final boolean pConnected) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionObserver observer : mConnectionObservers) {
                    switch (pAction) {
                        case CONNECTION_ABORT:
                            observer.onConnectionAbort();
                            break;
                        case CONNECTION_ESTABLISHED:
                            observer.onConnectionEstablished();
                            break;
                        case CONNECTION_ATTEMPT_FAIL:
                            observer.onConnectionEnableAttemptFailed();
                            break;
                        case CONNECT_DEVICE_ATTEMPT:
                            observer.onConnectToDeviceAttempt(pDevice, pConnected);
                            break;
                        case CONNECTION_DEVICE_LOST:
                            observer.onDeviceConnectionLost();
                            break;
                    }
                }
            }
        });
    }

    // ABSTRACT METHODS

    /**
     * Shall enable the connection, e.g. enabling Bluetooth.
     */
    public abstract void enable();

    /**
     * Shall return all available devices which are possible to
     * connect to.
     *
     * @return a list of devices which are available to get connected
     */
    public abstract ArrayList<Device> getAvailableDevices();

    /**
     * Shall check if the connection is enabled, e.g. Bluetooth or WiFi.
     *
     * @return true if enabled, false otherwise
     */
    public abstract boolean isEnabled();

    /**
     * Shall return a name describing the type of this connection.
     *
     * @return a String describing the type, e.g. Bluetooth
     */
    public abstract String getConnectionDescription();

    // INTERFACES

    public interface ConnectionObserver {

        enum ConnectionAction {
            CONNECTION_ABORT,
            CONNECTION_ESTABLISHED,
            CONNECTION_ATTEMPT_FAIL,
            CONNECT_DEVICE_ATTEMPT,
            CONNECTION_DEVICE_LOST
        }

        /**
         * Shall be called whenever the connection has trouble and was aborted.
         */
        void onConnectionAbort();

        /**
         * Shall be called if the connection is established.
         */
        void onConnectionEstablished();

        /**
         * Shall be called whenever enable() was called but enabling failed.
         */
        void onConnectionEnableAttemptFailed();

        /**
         * Shall be called whenever a device was about to be connected.
         *
         * @param pDevice    the device which was about to get connected
         * @param pConnected true if the device is connected now, false otherwise
         */
        void onConnectToDeviceAttempt(Device pDevice, boolean pConnected);

        /**
         * Shall be called whenever a established connection to a device is suddenly lost.
         */
        void onDeviceConnectionLost();
    }

}
