package de.carstendroesser.obdtripmate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.carstendroesser.obdtripmate.LocationProvider;
import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.DevicesAdapter;
import de.carstendroesser.obdtripmate.devices.Device;
import de.carstendroesser.obdtripmate.dialogs.DevicesSelectDialog;
import de.carstendroesser.obdtripmate.obd.OBDEngine;
import de.carstendroesser.obdtripmate.services.OBDService;
import de.carstendroesser.obdtripmate.utils.MapUtils;
import de.carstendroesser.obdtripmate.utils.WaveHelper;
import io.nlopez.smartlocation.OnLocationUpdatedListener;

/**
 * Created by carstendrosser on 21.06.17.
 */

public class MainActivity extends ConnectedActivity implements OnMapReadyCallback {

    // VIEWS

    @Bind(R.id.waveView)
    protected WaveView mWaveView;
    @Bind(R.id.vinTextView)
    protected TextView mVinTextView;
    @Bind(R.id.obdStandardTextView)
    protected TextView mOBDStandardTextView;
    @Bind(R.id.protocolTextView)
    protected TextView mProtocolTextView;
    @Bind(R.id.connectionTypeTextView)
    protected TextView mConnectionTypeTextView;
    @Bind(R.id.deviceNameTextView)
    protected TextView mDeviceNameTextView;
    @Bind(R.id.batteryTextView)
    protected TextView mBatteryTextView;

    // MEMBERS

    private WaveHelper mWaveHelper;
    private DevicesSelectDialog mDeviceSelectDialog;

    // ACTIVITY CALLBACKS

    @Override
    protected void setup() {
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        showProgressDialog("Please wait", "Setting up...");

        // setting up WaveView
        mWaveView.setWaveColor(R.color.colorPrimary, R.color.colorPrimaryDark);
        mWaveView.setShapeType(WaveView.ShapeType.SQUARE);
        mWaveHelper = new WaveHelper(mWaveView);

        // setup mapfragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    // SERVICE CALLBACKS

    @Override
    protected void onServiceConnected() {
        if (!mConnection.isEnabled()) {
            showProgressDialog("Please wait", "Trying to enable connection...");
            mConnection.enable();
        } else {
            // connection is enabled
            if (!mConnection.isConnected()) {
                hideAllDialogs();
                if (mDeviceSelectDialog != null) {
                    mDeviceSelectDialog.dismiss();
                }
                showDeviceSelectDialog();
            } else {
                if (mOBDEngine.getState() == OBDEngine.State.IDLE) {
                    showProgressDialog("Please wait", "Setting up OBD Adapter...");
                    mOBDEngine.startObdSetup();
                }
            }
        }
    }

    @Override
    protected void onServiceDisconnected() {
        // empty
    }

    // CONNECTION CALLBACKS

    @Override
    public void onConnectionEstablished() {
        showDeviceSelectDialog();
    }

    @Override
    public void onConnectToDeviceAttempt(final Device pDevice, boolean pConnected) {
        if (pConnected) {
            showProgressDialog("Please wait", "Setting up OBD Connection...");
            mOBDEngine.startObdSetup();
        } else {
            // something went wrong!
            showMessageDialog("Something went wrong", "Couldn't connect to the given device. Do you want to retry?", "Yes, retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface pDialog, int pWhich) {
                    hideAllDialogs();
                    onShutdownButtonClick();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface pDialog, int pWhich) {
                    showProgressDialog("Please wait", "Connecting to device " + pDevice.getName());
                    mConnection.connectToDevice(pDevice);
                }
            });
        }
    }

    @Override
    public void onConnectionAbort() {
        showMessageDialog("Something went wrong", "The connection was aborted. Do you want to try to establish the connection again?", "Yes, reconnect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                pDialog.dismiss();
                onShutdownButtonClick();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                showProgressDialog("Please wait...", "Trying to enable connection...");
                mConnection.enable();
                pDialog.dismiss();
            }
        });
    }

    @Override
    public void onConnectionEnableAttemptFailed() {
        showMessageDialog("Connection not enabled", "Trying to enable the connection failed. Do you want to retry to establish the connection?", "Yes, retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                hideAllDialogs();
                onShutdownButtonClick();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                showProgressDialog("Please wait", "Trying to establish the connection...");
                mConnection.enable();
            }
        });
    }

    @Override
    public void onDeviceConnectionLost() {
        showMessageDialog("Device lost", "The connection to your adapter was interrupted and there is no active connection. Do you want to select another device?", "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                hideAllDialogs();
                onShutdownButtonClick();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                showDeviceSelectDialog();
            }
        });
    }

    // OBD ENGINE CALLBACKS

    @Override
    public void onEngineSetupComplete(String pVin, String pProtocol, String pOBDStandard, String pBatteryVoltage) {
        mVinTextView.setText(pVin);
        mProtocolTextView.setText(pProtocol);
        mOBDStandardTextView.setText(pOBDStandard);
        mConnectionTypeTextView.setText(mConnection.getConnectionDescription());
        mDeviceNameTextView.setText(mConnection.getConnectedDevice().getName());
        mBatteryTextView.setText(pBatteryVoltage + "V");
        mWaveHelper.start();
        hideAllDialogs();
    }

    @Override
    public void onEngineSetupError() {
        showMessageDialog("OBD not setup correctly", "The setup was not completed successfuly. Do you want to retry the setup?", "Yes, retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                hideAllDialogs();
                onShutdownButtonClick();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                showProgressDialog("Please wait", "Setting up OBD Adapter...");
                mOBDEngine.startObdSetup();
            }
        });
    }

    // MAP CALLBACKS

    @Override
    public void onMapReady(final GoogleMap pGoogleMap) {
        MapUtils.disableInteraction(pGoogleMap);
        final Marker marker = MapUtils.addMarkerToMap(this, pGoogleMap, R.drawable.mapmarker, 28, new LatLng(0, 0));

        // listen to location updates
        LocationProvider.getInstance(this).addLocationUpdatedListener(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location pLocation) {
                LatLng ltdlng = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
                MapUtils.updateMarkerPosition(marker, ltdlng, pGoogleMap);
            }
        });
    }

    // VIEW CALLBACKS

    @OnClick(R.id.liveDataButton)
    protected void onLiveDataButtonClick() {
        startActivity(new Intent(this, LiveDataActivity.class));
    }

    @OnClick(R.id.myTripsButton)
    protected void onMyTripsButtonClick() {
        startActivity(new Intent(this, TripsListActivity.class));
    }

    @OnClick(R.id.shutdownImageView)
    protected void onShutdownButtonClick() {
        // disable everything
        mConnection.removeConnectionObserver(this);
        mOBDEngine.removeEngineObserver(this);
        mConnection.shutdown();
        stopService(new Intent(this, OBDService.class));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // PRIVATE API

    private void showDeviceSelectDialog() {
        hideAllDialogs();
        mDeviceSelectDialog = new DevicesSelectDialog(this, mConnection.getAvailableDevices(), new DevicesAdapter.OnDeviceSelectListener() {
            @Override
            public void onDeviceSelected(Device pDevice) {
                showProgressDialog("Please wait", "Connecting to device " + pDevice.getName());
                mConnection.connectToDevice(pDevice);
            }
        });
        mDeviceSelectDialog.show();
    }

    // PROTECTED API

    @Override
    protected void hideAllDialogs() {
        super.hideAllDialogs();
        if (mDeviceSelectDialog != null && mDeviceSelectDialog.isShowing()) {
            mDeviceSelectDialog.hide();
        }
    }

}
