package de.carstendroesser.obdtripmate.devices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class BTDevice implements Device {

    // CONSTANTS

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MEMBERS

    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mSocket;

    // CONSTRUCTORS

    /**
     * Creates a new BTDevice with the given BluetoothDevice.
     *
     * @param pBluetoothDevice the specific device to wrap
     */
    public BTDevice(BluetoothDevice pBluetoothDevice) {
        mBluetoothDevice = pBluetoothDevice;
    }

    // PUBLIC API

    @Override
    public boolean connect() {
        try {
            mSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            return true;
        } catch (IOException pException) {
            pException.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                mSocket.getInputStream().close();
                mSocket.getOutputStream().close();
                mSocket.close();
            } catch (IOException pException) {
                pException.printStackTrace();
            }
            mSocket = null;
        }
    }

    @Override
    public InputStream getInputStream() {
        if (!isConnected()) {
            return null;
        }

        try {
            return mSocket.getInputStream();
        } catch (IOException pException) {
            return null;
        }
    }

    @Override
    public OutputStream getOutputStream() {
        if (!isConnected()) {
            return null;
        }

        try {
            return mSocket.getOutputStream();
        } catch (IOException pException) {
            return null;
        }
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    @Override
    public String getName() {
        return mBluetoothDevice.getName();
    }

}
