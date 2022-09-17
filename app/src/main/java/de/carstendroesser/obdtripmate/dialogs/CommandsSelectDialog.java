package de.carstendroesser.obdtripmate.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import java.util.List;

import de.carstendroesser.obdtripmate.R;
import de.carstendroesser.obdtripmate.adapters.SelectableCommandsAdapter;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 31.05.17.
 */

public class CommandsSelectDialog extends AlertDialog {

    // MEMBERS

    private RecyclerView mRecyclerViewCommands;

    // CONSTRUCTORS

    /**
     * Creates a new dialog to select multiple commands.
     *
     * @param pContext  we need that
     * @param pCommands a list of commands to selecto from
     * @param pListener notified about selection
     */
    public CommandsSelectDialog(Context pContext, List<OBDCommand> pCommands, final OnCommandsSelectedListener pListener) {
        super(pContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // set the layout
        View content = LayoutInflater.from(pContext).inflate(R.layout.dialog_select_commands, null);
        setView(content);

        // make it persistent and not cancelable
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // setup the recyclerview (list)
        RecyclerView recyclerViewCommands = (RecyclerView) content.findViewById(R.id.dialog_select_commands_recyclerview);
        recyclerViewCommands.setLayoutManager(new LinearLayoutManager(pContext, LinearLayoutManager.VERTICAL, false));

        final SelectableCommandsAdapter adapter = new SelectableCommandsAdapter(pCommands);
        recyclerViewCommands.setAdapter(adapter);

        // setup the button to confirm the selection
        content.findViewById(R.id.buttonSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                dismiss();
                pListener.onCommandsSelected(adapter.getSelectedCommands());
            }
        });
    }

    // INTERFACES

    /**
     * Used to get notified about selections.
     */
    public interface OnCommandsSelectedListener {
        /**
         * The select button was clicked.
         *
         * @param pCommands the selected commands
         */
        void onCommandsSelected(List<OBDCommand> pCommands);
    }

}
