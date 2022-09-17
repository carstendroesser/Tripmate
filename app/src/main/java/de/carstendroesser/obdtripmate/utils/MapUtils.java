package de.carstendroesser.obdtripmate.utils;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by carstendrosser on 04.07.17.
 */

public class MapUtils {

    /**
     * Disables all userinteractions for a given map.
     *
     * @param pGoogleMap the map to disable all interactions for
     */
    public static void disableInteraction(GoogleMap pGoogleMap) {
        pGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        pGoogleMap.getUiSettings().setCompassEnabled(false);
        pGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        pGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        pGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        pGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        pGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        pGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        pGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
    }

    /**
     * Adds a resource drawable to a given map.
     *
     * @param pContext    we need that
     * @param pGoogleMap  the map to add the drawable to
     * @param pResource   the resource drawable
     * @param pDimensions the dimensions this marker shall have
     * @param pLatLng     the position this marker shall be added at
     * @return the marker that has been added
     */
    public static Marker addMarkerToMap(Context pContext, GoogleMap pGoogleMap, int pResource, int pDimensions, LatLng pLatLng) {
        BitmapDescriptor icon = BitmapDescriptorFactory
                .fromBitmap(
                        BitmapResizer.resizeBitmap(
                                pContext,
                                pResource,
                                DensityConverter.convertDpToPixels(pContext, pDimensions),
                                DensityConverter.convertDpToPixels(pContext, pDimensions)));

        return pGoogleMap.addMarker(
                new MarkerOptions()
                        .position(pLatLng)
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .icon(icon));
    }

    /**
     * Updates the position of a marker and centers the camera to it.
     *
     * @param pMarker    the marker to update the position of
     * @param pLtdlng    the new position
     * @param pGoogleMap the map
     */
    public static void updateMarkerPosition(Marker pMarker, LatLng pLtdlng, GoogleMap pGoogleMap) {
        pMarker.setPosition(pLtdlng);
        CameraUpdate center = CameraUpdateFactory.newLatLng(pLtdlng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        pGoogleMap.moveCamera(center);
        pGoogleMap.animateCamera(zoom);
    }

}
