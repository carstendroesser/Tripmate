package de.carstendroesser.obdtripmate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;
import de.carstendroesser.obdtripmate.views.SimpleDataView;

/**
 * Created by carstendrosser on 23.06.17.
 */

public class ObdValuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // MEMBERS

    private List<OBDCommand> mOBDCommands;

    // CONSTRUCTORS

    /**
     * Constructs a new adapter with no data.
     */
    public ObdValuesAdapter() {
        mOBDCommands = new ArrayList<>();
    }

    // PUBLIC API

    /**
     * Updates the datasource for this adapter.
     *
     * @param pValues the new values
     */
    public void updateObdCommands(List<OBDCommand> pValues) {
        mOBDCommands.clear();
        mOBDCommands.addAll(pValues);
        notifyDataSetChanged();
    }

    // ADAPTER

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.listitem_value, pParent, false);
        return new ValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder pHolder, int pPosition) {
        OBDCommand command = mOBDCommands.get(pPosition);
        ((ValueViewHolder) pHolder).simpleDataView.setPrimaryText(command.getValue());
        ((ValueViewHolder) pHolder).simpleDataView.setSecondaryText(command.getUnit());
        ((ValueViewHolder) pHolder).simpleDataView.setThirdText(command.getName() + " (" + command.getResponseTime() + ")");
    }

    @Override
    public int getItemCount() {
        return mOBDCommands.size();
    }

    // VIEWHOLDERS

    public class ValueViewHolder extends RecyclerView.ViewHolder {

        SimpleDataView simpleDataView;

        public ValueViewHolder(View pItemView) {
            super(pItemView);
            simpleDataView = (SimpleDataView) pItemView.findViewById(R.id.dataView);
        }
    }

}
