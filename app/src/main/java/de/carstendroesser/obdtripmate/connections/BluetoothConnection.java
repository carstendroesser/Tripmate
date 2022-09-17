package de.carstendroesser.obdtripmate.connections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Set;

import de.carstendroesser.obdtripmate.devices.BTDevice;
import de.carstendroesser.obdtripmate.devices.Device;

import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTING;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;

/**
 * Created by carstendrosser on 01.06.17.
 */

public class BluetoothConnection extends Connection {

    // MEMBERS

    private BroadcastReceiver mBroadcastReceiver;

    // CONSTRUCTORS

    /**
     * Constructs a new BluetoothConnection.
     *
     * @param pContext we need that
     */
    public BluetoothConnection(Context pContext) {
        super(pContext);

        // listen to system events like enabling/disabling bluetooth and device lost
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context pContext, Intent pIntent) {
                String action = pIntent.getAction();
                int state = pIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (state == STATE_ON) {
                            notifyListeners(ConnectionObserver.ConnectionAction.CONNECTION_ESTABLISHED, null, false);
                        } else if (state == STATE_DISCONNECTING || state == STATE_OFF || state == STATE_TURNING_OFF || state == STATE_DISCONNECTED) {
                            shutdown();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        shutdown();
                        notifyListeners(ConnectionObserver.ConnectionAction.CONNECTION_DEVICE_LOST, mConnectedDevice, false);
                        break;
                }

            }
        };

        // register the broadcastreceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        pContext.registerReceiver(mBroadcastReceiver, filter);
    }

    // PUBLIC API

    @Override
    public void enable() {
        // if already enabled, notify again
        if (isEnabled()) {
            notifyListeners(ConnectionObserver.ConnectionAction.CONNECTION_ESTABLISHED, null, false);
            return;
        }

        // just enable the connection if it is not turning on yet
        int adapterState = BluetoothAdapter.getDefaultAdapter().getState();
        if (adapterState != STATE_TURNING_ON) {
            boolean enablingInitiated = BluetoothAdapter.getDefaultAdapter().enable();
            if (!enablingInitiated) {
                notifyListeners(ConnectionObserver.ConnectionAction.CONNECTION_ATTEMPT_FAIL, null, false);
            }
        }
    }

    @Override
    public ArrayList<Device> getAvailableDevices() {
        if (!isEnabled()) {
            return null;
        }

        // get all bonded devices and wrap them in a Device
        ArrayList<Device> listOfDevices = new ArrayList<Device>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            pairedDevice.getBondState();
            listOfDevices.add(new BTDevice(pairedDevice));
        }

        return listOfDevices;
    }

    @Override
    public boolean isEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void connectToDevice(Device pDevice) {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        super.connectToDevice(pDevice);
    }

    @Override
    public String getConnectionDescription() {
        return "Bluetooth";
    }

}
