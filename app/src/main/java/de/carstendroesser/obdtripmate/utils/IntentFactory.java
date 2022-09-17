package de.carstendroesser.obdtripmate.utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by carstendrosser on 06.07.17.
 */

public class IntentFactory {

    public static Intent getShareIntentFor(String pPath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(pPath)));
        return shareIntent;
    }

}
