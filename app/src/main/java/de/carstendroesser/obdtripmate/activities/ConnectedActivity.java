package de.carstendroesser.obdtripmate.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import de.carstendroesser.obdtripmate.connections.Connection;
import de.carstendroesser.obdtripmate.devices.Device;
import de.carstendroesser.obdtripmate.dialogs.MessageDialog;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.obd.OBDEngine;
import de.carstendroesser.obdtripmate.services.OBDService;
import de.carstendroesser.obdtripmate.services.OBDService.ServiceBinder;

import static de.carstendroesser.obdtripmate.obd.OBDEngine.OBDEngineObserver;

public abstract class ConnectedActivity extends AppCompatActivity implements ServiceConnection, Connection.ConnectionObserver, OBDEngineObserver {

    // MEMBERS

    protected boolean mBound = false;
    protected OBDService mOBDService;
    protected OBDEngine mOBDEngine;
    protected Connection mConnection;

    private ProgressDialog mProgressDialog;
    private MessageDialog mMessageDialog;

    // ACTIVITY CALLBACKS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if the service is already running...

        boolean serviceAlreadyRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (OBDService.class.getName().equals(service.service.getClassName())) {
                serviceAlreadyRunning = true;
            }
        }

        // ... and start it
        if (!serviceAlreadyRunning) {
            startService(new Intent(this, OBDService.class));
        }

        // setup progressdialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        setup();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // bind the background service
        Intent intent = new Intent(this, OBDService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    @Override
    public void onBackPressed() {
        // listen to the devices' backkey
        hideAllDialogs();
        super.onBackPressed();
    }

    // SERVICE CALLBACKS

    @Override
    public void onServiceConnected(ComponentName pName, IBinder pService) {
        mBound = true;

        // get the service
        ServiceBinder binder = (ServiceBinder) pService;
        mOBDService = binder.getService();

        // get the engine and listen to engine related events
        mOBDEngine = mOBDService.getObdEngine();
        mOBDEngine.addEngineObserver(this);

        // get the connection and listen to connection related events
        mConnection = mOBDService.getObdEngine().getConnection();
        mConnection.addConnectionObserver(this);

        // event pass-trough to derived activities
        onServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName pName) {
        mBound = false;
        mOBDService = null;

        // event pass-trough to derived activities
        onServiceDisconnected();
    }

    // CONNECTION CALLBACKS

    @Override
    public void onConnectionAbort() {
        // empty
    }

    @Override
    public void onConnectionEstablished() {
        // empty
    }

    @Override
    public void onConnectionEnableAttemptFailed() {
        // empty
    }

    @Override
    public void onConnectToDeviceAttempt(Device pDevice, boolean pConnected) {
        // empty
    }

    @Override
    public void onDeviceConnectionLost() {
        // empty
    }

    // OBDENGINE CALLBACKS

    @Override
    public void onEngineLoopPass(List<OBDCommand> pCommands, Location pLocation) {
        // empty
    }

    @Override
    public void onEngineLoopError() {
        // empty
    }

    @Override
    public void onEngineLoopStopped() {
        // empty
    }

    @Override
    public void onEngineSetupComplete(String pVin, String pProtocol, String pOBDStandard, String pBatteryVoltage) {
        // empty
    }

    @Override
    public void onEngineSetupError() {
        // empty
    }

    @Override
    public void onEngineLoopStart() {
        // empty
    }

    @Override
    public void onEngineSetupStart() {
        // empty
    }

    // PRIVATE API

    /**
     * Loose connection to the service.
     */
    private void unbindService() {
        if (mBound) {
            unbindService(this);
            mBound = false;
        }
    }

    // PROTECTED API

    /**
     * Shows a dialog with a progress-indicator.
     *
     * @param pTitle   the header title
     * @param pMessage the body message
     */
    protected void showProgressDialog(String pTitle, String pMessage) {
        hideAllDialogs();
        mProgressDialog.setTitle(pTitle);
        mProgressDialog.setMessage(pMessage);
        mProgressDialog.show();
    }

    /**
     * Shows a dialog with a given message.
     *
     * @param pTitle           the dialog header
     * @param pMessage         the dialog body message
     * @param pConfirmText     the confirmbutton's text
     * @param pCancelListener  notified about cancelbutton clicks
     * @param pConfirmListener notified about confirmbutton clicks
     */
    protected void showMessageDialog(String pTitle, String pMessage, String pConfirmText, DialogInterface.OnClickListener pCancelListener, DialogInterface.OnClickListener pConfirmListener) {
        hideAllDialogs();
        mMessageDialog = new MessageDialog(this, pTitle, pMessage, pConfirmText, pCancelListener, pConfirmListener);
        mMessageDialog.show();
    }

    /**
     * Hides all dialogs to make sure that there are never multiple
     * dialogs shown at the same time.
     */
    protected void hideAllDialogs() {
        try {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (mMessageDialog != null && mMessageDialog.isShowing()) {
                mMessageDialog.dismiss();
            }

        } catch (Exception pException) {
            // empty
        }
    }

    // ABSTRACT METHODS

    /**
     * All derived activities shall setup the content view here
     * and do initialisations.
     */
    protected abstract void setup();

    /**
     * Called whenever the OBDService is connected and ready to use.
     */
    protected abstract void onServiceConnected();

    /**
     * Called whenever the OBDService is disconnected.
     */
    protected abstract void onServiceDisconnected();

}
