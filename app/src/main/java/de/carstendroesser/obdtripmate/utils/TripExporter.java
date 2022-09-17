package de.carstendroesser.obdtripmate.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.carstendroesser.obdtripmate.activities.TripSerializer;
import de.carstendroesser.obdtripmate.database.Trip;

/**
 * Created by carstendrosser on 06.07.17.
 */

public class TripExporter {

    public static void tripToJson(final Trip pTrip, final OnTripReadyCallback pCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path =
                            Environment.getExternalStorageDirectory().getPath()
                                    + "/tripmate/trip" + pTrip.getId() + ".json";

                    File file = new File(path);
                    if (!new File(file.getParent()).exists()) {
                        new File(file.getParent()).mkdirs();
                    }

                    FileWriter writer = new FileWriter(file.getAbsolutePath());
                    Gson gson = new GsonBuilder().registerTypeAdapter(Trip.class, new TripSerializer())
                            .create();
                    gson.toJson(pTrip, writer);
                    writer.close();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (pCallback != null) {
                                pCallback.onTripReady(path);
                            }
                        }
                    });

                } catch (IOException pException) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (pCallback != null) {
                                pCallback.onError();
                            }
                        }
                    });
                }

            }
        }).start();
    }

    public interface OnTripReadyCallback {
        void onTripReady(String pPath);

        void onError();
    }

}
