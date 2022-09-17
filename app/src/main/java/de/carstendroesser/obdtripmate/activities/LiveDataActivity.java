package de.carstendroesser.obdtripmate.activities;

import android.content.DialogInterface;
import android.location.Location;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.ObdValuesAdapter;
import de.carstendroesser.obdtripmate.dialogs.CommandsSelectDialog;
import de.carstendroesser.obdtripmate.obd.OBDCommands.BatteryVoltageCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineCoolantTempCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.EngineLoadCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.MonitorCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.RPMCommand;
import de.carstendroesser.obdtripmate.obd.OBDCommands.PID.SpeedCommand;
import de.carstendroesser.obdtripmate.obd.OBDEngine;
import de.carstendroesser.obdtripmate.utils.AnimationUtils;
import de.carstendroesser.obdtripmate.utils.DensityConverter;
import de.carstendroesser.obdtripmate.utils.MapUtils;
import de.carstendroesser.obdtripmate.views.BarDataView;
import de.carstendroesser.obdtripmate.views.CircleDataView;

import static de.carstendroesser.obdtripmate.dialogs.CommandsSelectDialog.OnCommandsSelectedListener;

/**
 * Created by carstendrosser on 22.06.17.
 */

public class LiveDataActivity extends ConnectedActivity implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    // VIEWS

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.rpmDataView)
    protected CircleDataView mRPMCircleDataView;
    @Bind(R.id.speedDataView)
    protected CircleDataView mSpeedCircleDataView;
    @Bind(R.id.voltageDataView)
    protected BarDataView mVoltageDataView;
    @Bind(R.id.coolantTempDataView)
    protected BarDataView mCoolantTempDataView;
    @Bind(R.id.engineLoadDataView)
    protected BarDataView mEngineLoadDataView;
    @Bind(R.id.moreValuesRecyclerView)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.fab)
    protected FloatingActionButton mFab;
    @Bind(R.id.fabTextView)
    protected TextView mFabTextView;
    @Bind(R.id.milImageView)
    protected ImageView mMilImageView;
    @Bind(R.id.chronometer)
    protected Chronometer mChronometer;
    @Bind(R.id.mapCardView)
    protected CardView mMapCardView;

    // MEMBERS

    private ObdValuesAdapter mObdValuesAdapter;
    private CommandsSelectDialog mCommandsSelectDialog;
    private GoogleMap mGoogleMap;
    private Marker mMarker;

    // ACTIVITY CALLBACKS

    @Override
    protected void setup() {
        setContentView(R.layout.activity_livedata);
        ButterKnife.bind(this);

        showProgressDialog("Please wait", "Connecting to service...");

        // setup views
        mSpeedCircleDataView.setMaxValue(240);
        mSpeedCircleDataView.setText("KM/H");
        mRPMCircleDataView.setMaxValue(7000);
        mRPMCircleDataView.setText("RPM");
        mVoltageDataView.setMaxProgress(200);
        mCoolantTempDataView.setMaxProgress(150);
        mEngineLoadDataView.setMaxProgress(100);

        // setup recyclerview (list/grid of data)
        mObdValuesAdapter = new ObdValuesAdapter();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mObdValuesAdapter);

        // setup toolbar and make back button clickable
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                onBackPressed();
            }
        });

        // setup the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        // listen to the device's backkey

        // just go back when idle
        if (mOBDEngine.getState() == OBDEngine.State.IDLE) {
            mOBDEngine.removeEngineObserver(this);
            mConnection.removeConnectionObserver(this);
            super.onBackPressed();
        } else {
            Toast.makeText(this, "You cannot go back when receiving livedata", Toast.LENGTH_SHORT).show();
        }
    }

    // SERVICE CALLBACKS

    @Override
    protected void onServiceConnected() {
        // show dialog to select the commands which shall be queried

        hideAllDialogs();
        mCommandsSelectDialog = new CommandsSelectDialog(
                this,
                mOBDEngine.getSelectableCommands(),
                new OnCommandsSelectedListener() {
                    @Override
                    public void onCommandsSelected(List<OBDCommand> pCommands) {
                        mOBDEngine.setLoopedCommands(pCommands);
                        mFabTextView.setText("GO");
                        hideAllDialogs();
                    }
                });
        mCommandsSelectDialog.show();
    }

    @Override
    protected void onServiceDisconnected() {
        // empty
    }

    // OBD ENGINE CALLBACKS

    @Override
    public void onEngineLoopPass(List<OBDCommand> pCommands, Location pLocation) {

        // fill views with values
        for (OBDCommand command : pCommands) {
            if (command instanceof MonitorCommand) {
                boolean milOn = ((MonitorCommand) command).isMilOn();
                if (milOn) {
                    mMilImageView.setVisibility(View.VISIBLE);
                } else {
                    mMilImageView.setVisibility(View.INVISIBLE);
                }
            } else if (command instanceof RPMCommand) {
                int value = command.getValue() == null ? 0 : Integer.parseInt(command.getValue());
                mRPMCircleDataView.setValueAnimated(value);
            } else if (command instanceof BatteryVoltageCommand) {
                float value = command.getValue() == null ? 0 : Float.parseFloat(command.getValue());
                mVoltageDataView.setProgress((int) (value * 10));
                mVoltageDataView.setText(command.getValue() + command.getUnit());
            } else if (command instanceof EngineCoolantTempCommand) {
                int value = command.getValue() == null ? 0 : Integer.parseInt(command.getValue());
                mCoolantTempDataView.setProgress(value);
                mCoolantTempDataView.setText(command.getValue() + command.getUnit());
            } else if (command instanceof SpeedCommand) {
                int value = command.getValue() == null ? 0 : Integer.parseInt(command.getValue());
                mSpeedCircleDataView.setValueAnimated(value);
            } else if (command instanceof EngineLoadCommand) {
                int value = command.getValue() == null ? 0 : Integer.parseInt(command.getValue());
                mEngineLoadDataView.setProgress(value);
                mEngineLoadDataView.setText(command.getValue() + command.getUnit());
            }
        }

        // update the list's datasource
        mObdValuesAdapter.updateObdCommands(pCommands);

        // update the map
        if (mGoogleMap != null) {
            LatLng ltdlng = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());

            if (mMarker == null) {
                mMarker = MapUtils.addMarkerToMap(this, mGoogleMap, R.drawable.mapmarker, 28, ltdlng);
            } else {
                MapUtils.updateMarkerPosition(mMarker, ltdlng, mGoogleMap);
            }
        }
    }

    @Override
    public void onEngineLoopStopped() {
        hideAllDialogs();
        mFabTextView.setText("GO");
        mChronometer.stop();
    }

    @Override
    public void onEngineLoopStart() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    public void onEngineLoopError() {
        showMessageDialog("Something went wrong", "An error occured while trying to receive livedata.", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                LiveDataActivity.super.onBackPressed();
            }
        }, null);
    }

    // CONNECTION CALLBACKS

    @Override
    public void onDeviceConnectionLost() {
        if (mOBDEngine.getState() == OBDEngine.State.LOOP) {
            mOBDEngine.stopLoopedCommands();
        }

        showMessageDialog("Device lost", "The connection to your adapter was interrupted and there is no active connection.", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                LiveDataActivity.super.onBackPressed();
            }
        }, null);
    }

    @Override
    public void onConnectionAbort() {
        super.onConnectionAbort();

        if (mOBDEngine.getState() == OBDEngine.State.LOOP) {
            mOBDEngine.stopLoopedCommands();
        }

        showMessageDialog("Connection abort", "Your connection was aborted.", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface pDialog, int pWhich) {
                LiveDataActivity.super.onBackPressed();
            }
        }, null);
    }

    // VIEW CALLBACKS

    @OnClick(R.id.fab)
    protected void onFabClick() {
        // toggle livedata on/off
        if (mOBDEngine.getState() == OBDEngine.State.IDLE) {
            mOBDEngine.startLoopedCommands(this);
            mFabTextView.setText("STOP");
        } else {
            showProgressDialog("Please wait", "Stopping the live data...");
            mOBDEngine.stopLoopedCommands();
        }
    }

    // PROTECTED API

    @Override
    protected void hideAllDialogs() {
        super.hideAllDialogs();
        if (mCommandsSelectDialog != null && mCommandsSelectDialog.isShowing()) {
            mCommandsSelectDialog.dismiss();
        }
    }

    // MAP CALLBACKS

    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        mGoogleMap = pGoogleMap;
        mGoogleMap.setOnMapClickListener(this);
        MapUtils.disableInteraction(mGoogleMap);
    }

    @Override
    public void onMapClick(LatLng pLatLng) {
        // enlarges or minimizes the map

        int wantedHeight;

        if (mMapCardView.getMeasuredHeight() == DensityConverter.convertDpToPixels(this, 80)) {
            // we are small
            wantedHeight = mMapCardView.getWidth();
        } else {
            // we are big
            wantedHeight = DensityConverter.convertDpToPixels(this, 80);
        }

        AnimationUtils.animateHeight(mMapCardView, wantedHeight);
    }

}
