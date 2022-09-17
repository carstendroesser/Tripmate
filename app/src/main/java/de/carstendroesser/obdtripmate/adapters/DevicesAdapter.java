package de.carstendroesser.obdtripmate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.devices.BTDevice;
import de.carstendroesser.obdtripmate.devices.Device;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // MEMBERS

    private ArrayList<Device> mDevices;
    private OnDeviceSelectListener mDeviceSelectListener;

    // CONSTRUCTOR

    /**
     * Constructs a new adapter to show a list of devices that can be selected.
     *
     * @param pDevices  the list of devices to show
     * @param pListener notified about selection
     */
    public DevicesAdapter(ArrayList<Device> pDevices, OnDeviceSelectListener pListener) {
        mDevices = new ArrayList<Device>();
        mDevices.addAll(pDevices);
        mDeviceSelectListener = pListener;
    }

    // ADAPTER

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.listitem_device, pParent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder pHolder, final int pPosition) {
        final Device device = mDevices.get(pPosition);
        ((DeviceViewHolder) pHolder).primaryTextView.setText(device.getName());
        ((DeviceViewHolder) pHolder).secondaryTextView.setText(device instanceof BTDevice ? "BT" : "?");

        pHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (mDeviceSelectListener != null) {
                    mDeviceSelectListener.onDeviceSelected(device);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    // VIEWHOLDERS

    private class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView primaryTextView;
        TextView secondaryTextView;

        public DeviceViewHolder(View pItemView) {
            super(pItemView);
            primaryTextView = (TextView) pItemView.findViewById(R.id.listitem_device_textview_primary);
            secondaryTextView = (TextView) pItemView.findViewById(R.id.listitem_device_textview_secondary);
        }

    }

    // INTERFACES

    public interface OnDeviceSelectListener {
        void onDeviceSelected(Device pDevice);
    }

}
