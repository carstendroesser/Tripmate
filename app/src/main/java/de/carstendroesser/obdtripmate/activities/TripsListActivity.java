package de.carstendroesser.obdtripmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.TripsAdapter;
import de.carstendroesser.obdtripmate.database.DatabaseHelper;
import de.carstendroesser.obdtripmate.database.Trip;
import de.carstendroesser.obdtripmate.utils.DensityConverter;
import de.carstendroesser.obdtripmate.views.ListSpaceDecoration;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class TripsListActivity extends ConnectedActivity implements TripsAdapter.OnTripClickListener {

    // VIEWS

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.recyclerView)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    protected ProgressBar mProgressBar;

    // MEMBERS

    private TripsAdapter mAdapter;

    // CONNECTED ACTIVITY CALLBACKS

    @Override
    protected void setup() {
        setContentView(R.layout.activity_tripslist);

        ButterKnife.bind(this);

        // setup the toolbar and backbutton
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                onBackPressed();
            }
        });

        // setup the list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new ListSpaceDecoration(DensityConverter.convertDpToPixels(this, 8)));
        mAdapter = new TripsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // load all trips async
        loadTrips();
    }

    @Override
    protected void onServiceConnected() {
        // empty
    }

    @Override
    protected void onServiceDisconnected() {
        // empty
    }


    // PRIVATE API

    /**
     * Loads all the trips by querieng the database async.
     */
    private void loadTrips() {

        // show that we are currently loading something
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        // do it async
        new Thread(new Runnable() {
            @Override
            public void run() {
                // make it last for at least 1sec to prevent the screen from flickering
                // when changing the view visibilities
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException pException) {
                    pException.printStackTrace();
                }

                // load the trips
                final ArrayList<Trip> trips = DatabaseHelper.getInstance(TripsListActivity.this).getTrips();

                // pass the trips to the UI-Thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateList(trips);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    // VIEW CALLBACKS

    @Override
    public void onTripClick(Trip pTrip) {
        // start the details activity with the clicked trip
        Intent intent = new Intent(TripsListActivity.this, TripDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(TripDetailsActivity.TRIP_ID, pTrip.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onTripOptionsClick(View pView, final Trip pTrip) {
        // show a popupmenu to offer options for the clicked trip
        PopupMenu menu = new PopupMenu(TripsListActivity.this, pView);
        menu.setGravity(Gravity.RIGHT);
        menu.inflate(R.menu.trip_menu);
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem pItem) {
                if (pItem.getItemId() == R.id.delete) {
                    showProgressDialog("Please wait", "Deleting this trip...");

                    // delete the trip
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseHelper.getInstance(TripsListActivity.this).deleteTripById(pTrip.getId());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    hideAllDialogs();

                                    // refresh the list of trips
                                    loadTrips();
                                }
                            });
                        }
                    }).start();
                }
                return false;
            }
        });
    }

}
