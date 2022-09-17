package de.carstendroesser.obdtripmate.obd;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.carstendroesser.obdtripmate.LocationProvider;
import de.carstendroesser.obdtripmate.connections.Connection;
import de.carstendroesser.obdtripmate.database.DatabaseHelper;
import de.carstendroesser.obdtripmate.database.Trip;
import de.carstendroesser.obdtripmate.obd.OBDCommands.AT.DescribeProtocolCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.AT.EchoOffCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.AT.ProtocolAutoCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.AT.ResetAllCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.AT.SetDefaultsCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.BatteryVoltageCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineCoolantTempCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineLoadCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.MonitorCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.OBDStandardCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.RPMCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.SpeedCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.SupportedPidsCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.VinCommand;
import de.carstendroesser.obdtripmate.obd.OBDEngine.OBDEngineObserver.EngineAction;
import io.nlopez.smartlocation.OnLocationUpdatedListener;

/**
 * Created by carstendrosser on 02.06.17.
 */

public class OBDEngine {

    // ENUMS

    public enum State {
        IDLE,
        SETUP,
        LOOP
    }

    // MEMBERS

    private volatile boolean mLoopStarted = false;
    private volatile Location mLocation;
    private Connection mConnection;
    private volatile State mState;
    private List<OBDCommand> mLoopedCommands;
    private List<OBDEngineObserver> mEngineObservers;
    private List<OBDCommand> mSupportedCommands;
    private String mVin;
    private String mOBDStandard;
    private String mProtocol;
    private String mBatteryVoltage;
    private boolean mSetup = false;
    private LocationProvider mLocationProvider;
    private OnLocationUpdatedListener mLocationUpdatedListener;

    // CONSTRUCTORS

    /**
     * Constructs a new OBDEngine used to receive obd information
     * by using a set connection.
     */
    public OBDEngine() {
        mState = State.IDLE;
        mLoopedCommands = new ArrayList<>();
        mEngineObservers = new ArrayList<>();
        mSupportedCommands = new ArrayList<>();

        // set default location to have one if no location updates are available
        mLocation = new Location("OBDEngine");
        mLocation.setLatitude(0);
        mLocation.setLongitude(0);

        // listen to location updates
        mLocationUpdatedListener = new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location pLocation) {
                mLocation = pLocation;
            }
        };
    }

    // PUBLIC API

    /**
     * Sets a LocationProvider to which this object is attached to as listener to get
     * notified about location updates.
     *
     * @param pLocationProvider the LocationProvider to get the current location from
     */
    public void setLocationProvider(LocationProvider pLocationProvider) {
        if (mLocationProvider != null) {
            mLocationProvider.removeLocationUpdatedListener(mLocationUpdatedListener);
        }
        mLocationProvider = pLocationProvider;
        mLocationProvider.addLocationUpdatedListener(mLocationUpdatedListener);
    }

    /**
     * Adds a listener to get notified about engine actions.
     *
     * @param pObserver the listener to notify
     */
    public void addEngineObserver(OBDEngineObserver pObserver) {
        mEngineObservers.add(pObserver);
    }

    /**
     * Removes an engine observer.
     *
     * @param pObserver the observer to remove
     */
    public void removeEngineObserver(OBDEngineObserver pObserver) {
        mEngineObservers.remove(pObserver);
    }

    /**
     * Sets a connection the communication to the vehicle shall be done with.
     *
     * @param pConnection the connection to use
     */
    public void setConnection(Connection pConnection) {
        mConnection = pConnection;
    }

    /**
     * Gets the currently used connection.
     *
     * @return the currently used connection
     */
    public Connection getConnection() {
        return mConnection;
    }

    /**
     * Gets the state of the engine. Can be one of the following:
     * IDLE: the engine is ready to do something
     * LOOP: currently looping commands
     * SETUP: currently trying to setup everything
     *
     * @return the current state
     */
    public State getState() {
        return mState;
    }

    /**
     * Checks if everything was setup once before.
     *
     * @return true if setup was done already, otherwise false
     */
    public boolean isSetup() {
        return mSetup;
    }

    /**
     * If possible, starts a setup. The adapter will be reset to the
     * needs and some values are queried.
     */
    public void startObdSetup() {

        // to prevent errors
        if (mState != State.IDLE) {
            return;
        }

        // if we don't need to setup again
        if (mSetup) {
            notifyInMainThread(EngineAction.SETUP_COMPLETE);
            return;
        }

        // we need to setup, set the state accordingly
        mState = State.SETUP;
        notifyInMainThread(EngineAction.SETUP_START);

        // start async setup
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mConnection.isConnected()) {
                    notifyInMainThread(EngineAction.SETUP_ERROR);
                    mState = State.IDLE;
                    return;
                }

                // do some setup commands for the adapter
                List<OBDCommand> setupCommands = new ArrayList<OBDCommand>();
                setupCommands.add(new ResetAllCommand());
                setupCommands.add(new SetDefaultsCommand());
                setupCommands.add(new EchoOffCommand());
                setupCommands.add(new ProtocolAutoCommand());

                for (OBDCommand command : setupCommands) {
                    mConnection.send(command.getCommandCode() + "\r");
                    Pair<Boolean, String> response = mConnection.receive();

                    if (!response.first) {
                        notifyInMainThread(EngineAction.SETUP_ERROR);
                        mState = State.IDLE;
                        return;
                    }
                }

                // get all the supported commands
                final List<OBDCommand> supportedCommands = new ArrayList<>();

                List<SupportedPidsCommand> checkCommands = new ArrayList<>();
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_20));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_40));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_60));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_80));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_A0));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_C0));
                checkCommands.add(new SupportedPidsCommand(SupportedPidsCommand.RANGE.RANGE_E0));

                // query for supported commands
                for (SupportedPidsCommand checkCommand : checkCommands) {
                    mConnection.send(checkCommand.getCommandCode() + "\r");
                    Pair<Boolean, String> response = mConnection.receive();

                    if (!response.first) {
                        notifyInMainThread(EngineAction.SETUP_ERROR);
                        mState = State.IDLE;
                        return;
                    }

                    checkCommand.setResponse(response.second, 0);

                    if (checkCommand.getSupportedCommands() == null) {
                        notifyInMainThread(EngineAction.SETUP_ERROR);
                        mState = State.IDLE;
                        return;
                    }

                    for (OBDCommand supportedCommand : checkCommand.getSupportedCommands()) {
                        if (!(supportedCommand instanceof SupportedPidsCommand)) {
                            supportedCommands.add(supportedCommand);
                        }
                    }

                    if (checkCommand.getSupportedCommands().isEmpty() ||
                            !(checkCommand.getSupportedCommands().get(checkCommand.getSupportedCommands().size() - 1)
                                    instanceof SupportedPidsCommand)) {
                        break;
                    }
                }

                // query the VIN
                final VinCommand vinCommand = new VinCommand();

                mConnection.send(vinCommand.getCommandCode() + "\r");
                Pair<Boolean, String> response = mConnection.receive();

                if (!response.first) {
                    notifyInMainThread(EngineAction.SETUP_ERROR);
                    mState = State.IDLE;
                    return;
                }

                vinCommand.setResponse(response.second, 0);

                // query OBDStandard
                final OBDStandardCommand obdStandardCommand = new OBDStandardCommand();

                mConnection.send(obdStandardCommand.getCommandCode() + "\r");
                Pair<Boolean, String> obdStandardCommandResponse = mConnection.receive();

                if (!obdStandardCommandResponse.first) {
                    notifyInMainThread(EngineAction.SETUP_ERROR);
                    mState = State.IDLE;
                    return;
                }

                obdStandardCommand.setResponse(obdStandardCommandResponse.second, 0);

                // query car's protocol
                final DescribeProtocolCommand protocolCommand = new DescribeProtocolCommand();

                mConnection.send(protocolCommand.getCommandCode() + "\r");
                Pair<Boolean, String> protocolCommandResponse = mConnection.receive();

                if (!protocolCommandResponse.first) {
                    notifyInMainThread(EngineAction.SETUP_ERROR);
                    mState = State.IDLE;
                    return;
                }

                protocolCommand.setResponse(protocolCommandResponse.second, 0);

                // query batteryvoltage
                final BatteryVoltageCommand batteryCommand = new BatteryVoltageCommand();

                mConnection.send(batteryCommand.getCommandCode() + "\r");
                Pair<Boolean, String> batteryCommandResponse = mConnection.receive();

                if (!batteryCommandResponse.first) {
                    notifyInMainThread(EngineAction.SETUP_ERROR);
                    mState = State.IDLE;
                    return;
                }

                batteryCommand.setResponse(batteryCommandResponse.second, 0);

                // do in UI-Thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mSupportedCommands.clear();
                        mSupportedCommands.add(new BatteryVoltageCommand());
                        mSupportedCommands.addAll(supportedCommands);
                        mVin = vinCommand.getValue();
                        mOBDStandard = obdStandardCommand.getValue();
                        mProtocol = protocolCommand.getValue();
                        mBatteryVoltage = batteryCommand.getValue();
                        mSetup = true;
                    }
                });

                mState = State.IDLE;
                notifyInMainThread(EngineAction.SETUP_COMPLETE);
            }
        }).start();
    }

    /**
     * Gets a list of all commands that can be selected to
     * query. The always supported commands are not selectable
     * and not included in the returned list.
     *
     * @return a list of commands that are supported
     */
    public List<OBDCommand> getSelectableCommands() {
        List<OBDCommand> supportedCommands = mSupportedCommands;
        List<OBDCommand> selectableCommands = new ArrayList<>();

        for (OBDCommand supportedCommand : supportedCommands) {
            if (supportedCommand instanceof BatteryVoltageCommand
                    || supportedCommand instanceof EngineCoolantTempCommand
                    || supportedCommand instanceof RPMCommand
                    || supportedCommand instanceof SpeedCommand
                    || supportedCommand instanceof EngineLoadCommand) {
            } else {
                selectableCommands.add(supportedCommand);
            }
        }

        return selectableCommands;
    }

    /**
     * Sets list of commands that shall be queried in a loop. The
     * always supported commands will be added automatically.
     *
     * @param pCommands a list of commands that shall be queried repeatly
     */
    public void setLoopedCommands(List<OBDCommand> pCommands) {
        mLoopedCommands.clear();
        mLoopedCommands.addAll(pCommands);

        // the always supported commands shall be looped always
        mLoopedCommands.add(new MonitorCommand());
        mLoopedCommands.add(new BatteryVoltageCommand());
        mLoopedCommands.add(new RPMCommand());
        mLoopedCommands.add(new SpeedCommand());
        mLoopedCommands.add(new EngineLoadCommand());
        mLoopedCommands.add(new EngineCoolantTempCommand());
    }

    /**
     * Starts the looper query of commands.
     *
     * @param pContext we need that
     */
    public void startLoopedCommands(final Context pContext) {
        // if we are not idle, we cannot do anything
        if (mState != State.IDLE) {
            return;
        }

        // we can start, set the state accordingly
        mLoopStarted = true;
        mState = State.LOOP;
        notifyInMainThread(EngineAction.LOOP_START);

        // loop async
        new Thread(new Runnable() {
            @Override
            public void run() {
                // the trip has started, insert a new one
                Trip trip = new Trip(-1, System.currentTimeMillis(), 0, new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), null);
                long tripId = DatabaseHelper.getInstance(pContext).insertTrip(trip);
                trip.setId(tripId);

                // do it looped
                while (true) {

                    // check if we shall stop
                    if (!mLoopStarted) {
                        trip.setEndTime(System.currentTimeMillis());
                        trip.setEndPoint(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                        DatabaseHelper.getInstance(pContext).updateTrip(trip);
                        notifyInMainThread(EngineAction.LOOP_STOPPED);
                        mState = State.IDLE;
                        mLoopStarted = false;
                        return;
                    }

                    // create a new loop
                    long loopId = DatabaseHelper.getInstance(pContext).insertLoop(tripId, mLocation, System.currentTimeMillis());

                    // query all the commands!
                    for (OBDCommand command : mLoopedCommands) {

                        // update the trips endpoint although we are not finished yet
                        trip.setEndTime(System.currentTimeMillis());
                        trip.setEndPoint(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                        DatabaseHelper.getInstance(pContext).updateTrip(trip);

                        long start = System.currentTimeMillis();

                        // check if we are stopped
                        if (!mLoopStarted) {
                            notifyInMainThread(EngineAction.LOOP_STOPPED);
                            mState = State.IDLE;
                            mLoopStarted = false;
                            return;
                        }

                        // check the connection
                        if (!mConnection.isConnected()) {
                            // error
                            notifyInMainThread(EngineAction.LOOP_ERROR);
                            mState = State.IDLE;
                            mLoopStarted = false;
                            return;
                        }

                        // send a request
                        mConnection.send(command.getCommandCode() + "\r");

                        // and receive a response
                        Pair<Boolean, String> response = mConnection.receive();

                        // check the response
                        if (response.first == false) {
                            // error
                            notifyInMainThread(EngineAction.LOOP_ERROR);
                            mState = State.IDLE;
                            mLoopStarted = false;
                            return;
                        }

                        // correct response, save it!
                        command.setResponse(response.second, System.currentTimeMillis() - start);

                        // save this value
                        DatabaseHelper.getInstance(pContext).insertValue(loopId, command);
                    }
                    notifyInMainThread(EngineAction.LOOP_PASS);
                }

            }
        }).start();
    }

    /**
     * Stops the looped commands query.
     */
    public void stopLoopedCommands() {
        mLoopStarted = false;
    }

    // PRIVATE API

    private void notifyInMainThread(final EngineAction pAction) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (OBDEngineObserver observer : mEngineObservers) {
                    switch (pAction) {
                        case LOOP_PASS:
                            observer.onEngineLoopPass(mLoopedCommands, mLocation);
                            break;
                        case LOOP_ERROR:
                            observer.onEngineLoopError();
                            break;
                        case LOOP_STOPPED:
                            observer.onEngineLoopStopped();
                            break;
                        case SETUP_COMPLETE:
                            observer.onEngineSetupComplete(mVin, mProtocol, mOBDStandard, mBatteryVoltage);
                            break;
                        case SETUP_ERROR:
                            observer.onEngineSetupError();
                            break;
                        case SETUP_START:
                            observer.onEngineSetupStart();
                            break;
                        case LOOP_START:
                            observer.onEngineLoopStart();
                            break;
                    }
                }
            }
        });
    }

    // INTERFACES

    public interface OBDEngineObserver {

        enum EngineAction {
            LOOP_PASS,
            LOOP_ERROR,
            LOOP_STOPPED,
            SETUP_COMPLETE,
            SETUP_ERROR,
            SETUP_START,
            LOOP_START
        }

        /**
         * The engine looped query has started.
         */
        void onEngineLoopStart();

        /**
         * All looped commands have been queried.
         *
         * @param pCommands a list of commands that have been queried
         * @param pLocation the location this query started
         */
        void onEngineLoopPass(List<OBDCommand> pCommands, Location pLocation);

        /**
         * Something went wrong with the connection or something else.
         */
        void onEngineLoopError();

        /**
         * The loop has stopped by a non-error cause.
         */
        void onEngineLoopStopped();

        /**
         * The setup has succeeded
         *
         * @param pVin            the car's vin
         * @param pProtocol       the car's protocol
         * @param pOBDStandard    the car's obd standard
         * @param pBatteryVoltage the car's current batteryvoltage
         */
        void onEngineSetupComplete(String pVin, String pProtocol, String pOBDStandard, String pBatteryVoltage);

        /**
         * Something went wrong when trying to setup the engine.
         */
        void onEngineSetupError();

        /**
         * The setup has started.
         */
        void onEngineSetupStart();

    }

}
