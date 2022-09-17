package de.carstendroesser.obdtripmate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.database.Trip;
import de.carstendroesser.obdtripmate.utils.FormatUtils;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class TripsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // MEMBERS

    private ArrayList<Trip> mTrips;
    private OnTripClickListener mOnTripClickListener;

    // CONSTRUCTORS

    /**
     * Construcs a new adapter to show trips.
     *
     * @param pOnTripClickListener notified about trip clicks
     */
    public TripsAdapter(OnTripClickListener pOnTripClickListener) {
        mTrips = new ArrayList<Trip>();
        mOnTripClickListener = pOnTripClickListener;
    }

    // PUBLIC API

    /**
     * Updates the datasource for this adapter.
     *
     * @param pTrips trips to show
     */
    public void updateList(ArrayList<Trip> pTrips) {
        mTrips.clear();
        mTrips.addAll(pTrips);
        notifyDataSetChanged();
    }

    // ADAPTER

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.listitem_trip, pParent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder pHolder, final int pPosition) {

        // set starttime and duration
        ((TripViewHolder) pHolder).secondaryTextView.setText(FormatUtils.toReadableDate(mTrips.get(pPosition).getStartTime()));
        ((TripViewHolder) pHolder).thirdTextView.setText(FormatUtils.toReadableDuration(mTrips.get(pPosition).getDuration()));

        // set title for this listitem
        ((TripViewHolder) pHolder).primaryTextView.setText("#" + mTrips.get(pPosition).getId());

        // get map-images for start and endpoint
        LatLng startPoint = mTrips.get(pPosition).getStartPoint();
        LatLng endPoint = mTrips.get(pPosition).getEndPoint();

        String src = "http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=300x200&markers=color:red|label:";

        Glide.with(pHolder.itemView.getContext()).load(src + "A|" + startPoint.latitude + "," + startPoint.longitude + "&sensor=false").fitCenter().into(((TripViewHolder) pHolder).imageViewPrimary);
        Glide.with(pHolder.itemView.getContext()).load(src + "B|" + endPoint.latitude + "," + endPoint.longitude + "&sensor=false").fitCenter().into(((TripViewHolder) pHolder).imageViewSecondary);

        // listen to clickevents for this listitem
        pHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTripClickListener != null) {
                    mOnTripClickListener.onTripClick(mTrips.get(pPosition));
                }
            }
        });

        // listen to optionsclick for this listitem
        ((TripViewHolder) pHolder).imageViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (mOnTripClickListener != null) {
                    mOnTripClickListener.onTripOptionsClick(pView, mTrips.get(pPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    // INTERFACES

    public interface OnTripClickListener {
        void onTripClick(Trip pTrip);

        void onTripOptionsClick(View pView, Trip pTrip);
    }

    // VIEWHOLDERS

    private class TripViewHolder extends RecyclerView.ViewHolder {

        TextView primaryTextView;
        TextView secondaryTextView;
        TextView thirdTextView;
        ImageView imageViewPrimary;
        ImageView imageViewSecondary;
        ImageView imageViewOptions;

        TripViewHolder(View pItemView) {
            super(pItemView);
            primaryTextView = (TextView) pItemView.findViewById(R.id.listitem_trip_textview_primary);
            secondaryTextView = (TextView) pItemView.findViewById(R.id.listitem_trip_textview_secondary);
            thirdTextView = (TextView) pItemView.findViewById(R.id.listitem_trip_textview_third);
            imageViewPrimary = (ImageView) pItemView.findViewById(R.id.listitem_trip_imageview_primary);
            imageViewSecondary = (ImageView) pItemView.findViewById(R.id.listitem_trip_imageview_secondary);
            imageViewOptions = (ImageView) pItemView.findViewById(R.id.listitem_trip_imageview_options);
        }
    }

}
