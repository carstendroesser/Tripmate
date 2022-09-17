package de.carstendroesser.obdtripmate.devices;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by carstendrosser on 31.05.17.
 */

/**
 * Interface used to communicate on a specified norm with all
 * types of devices.
 */
public interface Device {

    /**
     * Tries to connect. Blocking.
     *
     * @return true if connected, false otherwise
     */
    boolean connect();

    /**
     * Disconnects the device.
     */
    void disconnect();

    /**
     * Checks if this device is connected.
     *
     * @return true if it is connected, false otherwise
     */
    boolean isConnected();

    /**
     * Gets an inputstream to receive messages from this device.
     *
     * @return an inputstream to receive messages
     */
    InputStream getInputStream();

    /**
     * Gets an outputstream to send messages to this device.
     *
     * @return an outputstream to send messages to
     */
    OutputStream getOutputStream();

    /**
     * A name specifying this device.
     *
     * @return the name of this device
     */
    String getName();

}
