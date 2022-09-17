package de.carstendroesser.obdtripmate.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.DevicesAdapter;
import de.carstendroesser.obdtripmate.devices.Device;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class DevicesSelectDialog extends AlertDialog {

    // MEMBERS

    private RecyclerView mRecyclerViewDevices;

    // CONSTRUCTORS

    /**
     * Creates a new dialog to select one of the given devices.
     *
     * @param pContext  we need that
     * @param pDevices  a list of devices to select one from
     * @param pListener notified about selection
     */
    public DevicesSelectDialog(Context pContext, ArrayList<Device> pDevices, final DevicesAdapter.OnDeviceSelectListener pListener) {
        super(pContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // setup content views
        View content = LayoutInflater.from(pContext).inflate(R.layout.dialog_devices_select, null);
        setView(content);

        // make this dialog persistent and not cancelable by e.g. backclicks
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // setup recyclerview (list)
        mRecyclerViewDevices = (RecyclerView) content.findViewById(R.id.dialog_devices_select_recyclerview);

        mRecyclerViewDevices.setLayoutManager(new LinearLayoutManager(pContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewDevices.setAdapter(new DevicesAdapter(pDevices, new DevicesAdapter.OnDeviceSelectListener() {
            @Override
            public void onDeviceSelected(Device pDevice) {
                dismiss();
                if (pListener != null) {
                    pListener.onDeviceSelected(pDevice);
                }
            }
        }));
    }

}
