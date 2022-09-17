package de.carstendroesser.obdtripmate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.ObdValuesAdapter;
import de.carstendroesser.obdtripmate.database.DatabaseHelper;
import de.carstendroesser.obdtripmate.database.Loop;
import de.carstendroesser.obdtripmate.database.Trip;
import de.carstendroesser.obdtripmate.utils.DensityConverter;
import de.carstendroesser.obdtripmate.utils.FormatUtils;
import de.carstendroesser.obdtripmate.utils.IntentFactory;
import de.carstendroesser.obdtripmate.utils.MapUtils;
import de.carstendroesser.obdtripmate.utils.TripExporter;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class TripDetailsActivity extends ConnectedActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    // CONSTANTS

    public static final String TRIP_ID = "TRIP_ID";

    // VIEWS

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.dateTextView)
    protected TextView mDateTextView;
    @Bind(R.id.durationTextView)
    protected TextView mDurationTextView;
    @Bind(R.id.distanceTextView)
    protected TextView mDistanceTextView;
    @Bind(R.id.leftTextView)
    protected TextView mLeftTextView;
    @Bind(R.id.rightTextView)
    protected TextView mRightTextView;
    @Bind(R.id.seekBar)
    protected SeekBar mSeekbar;
    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    // MEMBERS

    private GoogleMap mGoogleMap;
    private LatLngBounds mBounds;
    private PolylineOptions mPolylineOptions;
    private Marker mMarker;
    private ObdValuesAdapter mAdapter;
    private Trip mTrip;
    private volatile long mDistance;
    private long mTripId;

    // CONNECTED ACTIVITY CALLBACKS

    @Override
    protected void setup() {
        setContentView(R.layout.activity_tripdetails);

        ButterKnife.bind(this);

        // setup recyclerview (list/grid)
        mAdapter = new ObdValuesAdapter();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mAdapter);

        // get the trip-id to show
        Bundle bundle = getIntent().getExtras();
        mTripId = bundle.getLong(TRIP_ID);

        // setup toolbar and back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                onBackPressed();
            }
        });

        // setup mapview
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        showProgressDialog("Please wait", "Trip is getting processed...");

        // load the trip
        loadTrip();
    }

    @Override
    protected void onServiceConnected() {
        // empty
    }

    @Override
    protected void onServiceDisconnected() {
        // empty
    }

    // ACTIVITY CALLBACKS

    @Override
    public boolean dispatchKeyEvent(KeyEvent pEvent) {
        // listen to volume down/up to make it easier to step trough the loops
        if (pEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pEvent.getAction() == KeyEvent.ACTION_DOWN && mSeekbar.getProgress() > 0) {
                showLoop(mSeekbar.getProgress() - 1);
            }
            return true;
        } else if (pEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pEvent.getAction() == KeyEvent.ACTION_DOWN
                    && mSeekbar.getProgress() < mTrip.getLoops().size() - 1) {
                showLoop(mSeekbar.getProgress() + 1);
            }
            return true;
        }

        return super.dispatchKeyEvent(pEvent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_details_menu, pMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pItem) {
        switch (pItem.getItemId()) {
            case R.id.delete:
                showProgressDialog("Please wait", "Deleting this trip...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseHelper.getInstance(TripDetailsActivity.this).deleteTripById(mTrip.getId());
                        finish();
                    }
                }).start();
                return true;
            case R.id.share:
                showProgressDialog("Please wait", "Trip is written to file...");

                TripExporter.tripToJson(mTrip, new TripExporter.OnTripReadyCallback() {
                    @Override
                    public void onTripReady(String pPath) {
                        hideAllDialogs();
                        startActivity(Intent.createChooser(IntentFactory.getShareIntentFor(pPath), "Share to"));
                    }

                    @Override
                    public void onError() {
                        showMessageDialog(
                                "Error",
                                "Something went wrong when trying to write this trip to a file.",
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface pDialog, int pWhich) {
                                        pDialog.dismiss();
                                    }
                                }, null);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(pItem);
        }
    }

    // MAP CALLBACKS

    @Override
    public void onMapReady(GoogleMap pGoogleMap) {
        mGoogleMap = pGoogleMap;
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mGoogleMap.animateCamera(zoom);

        MapUtils.disableInteraction(pGoogleMap);
    }

    // PRIVATE API

    /**
     * Loads data async for the given tripid and finally shows all data.
     */
    private void loadTrip() {
        // load async
        new Thread(new Runnable() {
            @Override
            public void run() {

                // get trip by id out of the database
                final Trip trip = DatabaseHelper.getInstance(TripDetailsActivity.this).getTripById(mTripId);

                // get all loops for this trip
                final ArrayList<Loop> loops =
                        DatabaseHelper.getInstance(TripDetailsActivity.this).getLoopsForTrip(mTripId);

                // create a path by the given loops
                final PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(getResources().getColor(R.color.colorAccent));
                polylineOptions.endCap(new RoundCap());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.width(DensityConverter.convertDpToPixels(TripDetailsActivity.this, 4));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                // calculate boundaries, distance and path
                for (int i = 0; i < loops.size(); i++) {
                    if (i > 0) {
                        mDistance += loops.get(i).getLocation().distanceTo(loops.get(i - 1).getLocation());
                    }

                    LatLng latLng = new LatLng(
                            loops.get(i).getLocation().getLatitude(),
                            loops.get(i).getLocation().getLongitude());
                    polylineOptions.add(latLng);
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                // do in mainthread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mTrip = trip;
                        mPolylineOptions = polylineOptions;
                        mTrip.setLoops(loops);
                        mBounds = bounds;
                        onTripProcessed();
                    }
                });
            }
        }).start();
    }

    /**
     * Called when the trip is processed and available to show.
     * Updates all views with data etc.
     */
    private void onTripProcessed() {
        hideAllDialogs();

        // set the activity's title
        mToolbar.setTitle("Details for trip " + mTrip.getId());

        // update the seekbar
        mSeekbar.setMax(mTrip.getLoops().size() - 1);
        mSeekbar.setOnSeekBarChangeListener(this);

        // update the mapview
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(
                mBounds,
                DensityConverter.convertDpToPixels(this, 16));
        mGoogleMap.moveCamera(update);
        mGoogleMap.addPolyline(mPolylineOptions);

        // update trip-info views
        mDateTextView.setText(FormatUtils.toReadableDate(mTrip.getStartTime()));
        mDurationTextView.setText(FormatUtils.toReadableDuration(mTrip.getEndTime() - mTrip.getStartTime()));
        mDistanceTextView.setText(FormatUtils.metersToKm(mDistance) + " KM");

        // add marker for start and end of the trip
        MapUtils.addMarkerToMap(this, mGoogleMap, R.drawable.a, 20, new LatLng(mTrip.getStartPoint().latitude, mTrip.getStartPoint().longitude));
        MapUtils.addMarkerToMap(this, mGoogleMap, R.drawable.b, 20, new LatLng(mTrip.getEndPoint().latitude, mTrip.getEndPoint().longitude));

        // show first loop
        showLoop(0);
    }

    /**
     * Updates all dataviews with data of the given loop.
     *
     * @param pLoop the loop's number to show
     */
    private void showLoop(int pLoop) {

        // update the seekbar's position
        mSeekbar.setProgress(pLoop);

        // set datasource for the list's adapter
        mAdapter.updateObdCommands(mTrip.getLoops().get(pLoop).getCommands());

        // calculate times for the currently shown loop
        Loop firstLoop = mTrip.getLoops().get(0);
        mLeftTextView.setText(FormatUtils.toReadableDuration(mTrip.getLoops().get(pLoop).getTimeStamp() - firstLoop.getTimeStamp()));
        mRightTextView.setText(FormatUtils.toReadableTime(mTrip.getLoops().get(pLoop).getTimeStamp()));

        // set/update the marker's position
        if (mMarker == null) {
            mMarker = MapUtils.addMarkerToMap(this, mGoogleMap, R.drawable.mapmarker, 28, new LatLng(mTrip.getLoops().get(pLoop).getLocation().getLatitude(), mTrip.getLoops().get(pLoop).getLocation().getLongitude()));
        } else {
            mMarker.setPosition(new LatLng(mTrip.getLoops().get(pLoop).getLocation().getLatitude(), mTrip.getLoops().get(pLoop).getLocation().getLongitude()));
        }
    }

    // VIEW CALLBACKS

    @Override
    public void onProgressChanged(SeekBar pSeekBar, int pProgress, boolean pFromUser) {
        // the seekbar has changed
        if (pFromUser) {
            // the user changed the seekbar
            showLoop(pProgress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar pSeekBar) {
        // empty
    }

    @Override
    public void onStopTrackingTouch(SeekBar pSeekBar) {
        // empty
    }

}
