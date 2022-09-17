package de.carstendroesser.obdtripmate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 17.06.17.
 */

public class SelectableCommandsAdapter extends RecyclerView.Adapter<SelectableCommandsAdapter.SelectableCommandViewHolder> {

    // MEMBERS

    private List<OBDCommand> mSelectableCommands;
    private List<OBDCommand> mSelectedCommands;

    // CONSTRUCTORS

    /**
     * Constructs a new adapter to select multiple commands.
     *
     * @param pCommands the commands to select from
     */
    public SelectableCommandsAdapter(List<OBDCommand> pCommands) {
        mSelectableCommands = new ArrayList<>();
        mSelectableCommands.addAll(pCommands);

        mSelectedCommands = new ArrayList<>();
    }

    // ADAPTER

    @Override
    public SelectableCommandViewHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        // inflate view
        LayoutInflater layoutInflater = LayoutInflater.from(pParent.getContext());
        View view = layoutInflater.inflate(R.layout.listitem_selectable_command, pParent, false);

        return new SelectableCommandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectableCommandViewHolder pViewHolder, final int pPosition) {
        pViewHolder.nameView.setText(mSelectableCommands.get(pPosition).getName());
        pViewHolder.checkBox.setOnCheckedChangeListener(null);

        if (mSelectedCommands.contains(mSelectableCommands.get(pPosition))) {
            pViewHolder.checkBox.setChecked(true);
        }

        pViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton pButtonView, boolean pIsChecked) {
                if (pIsChecked) {
                    mSelectedCommands.add(mSelectableCommands.get(pPosition));
                } else {
                    mSelectedCommands.remove(mSelectableCommands.get(pPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectableCommands.size();
    }

    // PUBLIC API

    /**
     * Get the selected commands.
     *
     * @return a list of commands
     */
    public List<OBDCommand> getSelectedCommands() {
        return mSelectedCommands;
    }

    // VIEWHOLDERS

    public class SelectableCommandViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView nameView;

        public SelectableCommandViewHolder(View pItemView) {
            super(pItemView);
            checkBox = (CheckBox) pItemView.findViewById(R.id.selectableCommandListItemCheckBox);
            nameView = (TextView) pItemView.findViewById(R.id.selectableCommandListItemTextView);
        }

    }

}
