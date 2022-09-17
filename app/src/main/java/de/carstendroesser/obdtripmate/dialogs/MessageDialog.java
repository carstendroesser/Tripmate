package de.carstendroesser.obdtripmate.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import de.carstendroesser.obdtripmate.R;

/**
 * Created by carstendrosser on 21.06.17.
 */

public class MessageDialog extends AlertDialog {

    /**
     * Creates a dialog to show simple message with confirm button and/or cancel button.
     *
     * @param pContext              we need that
     * @param pTitle                the title for this dialog
     * @param pMessage              the bodymessage for this dialog
     * @param pConfirmButtonText    the text for the confirmbutton
     * @param pCancelClickListener  notified about clicks on the cancel button
     * @param pConfirmClickListener notified about clicks on the confirm button
     */
    public MessageDialog(Context pContext, String pTitle, String pMessage, String pConfirmButtonText, final OnClickListener pCancelClickListener, final OnClickListener pConfirmClickListener) {
        super(pContext);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // setup the content view
        View content = LayoutInflater.from(pContext).inflate(R.layout.dialog_message, null);
        setView(content);

        // make it persistent and not cancelable
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // setup title and message
        TextView titleView = (TextView) content.findViewById(R.id.titleTextView);
        titleView.setText(pTitle);
        TextView messageView = (TextView) content.findViewById(R.id.messageTextView);
        messageView.setText(pMessage);

        // setup cancel button
        content.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                pCancelClickListener.onClick(MessageDialog.this, 0);
            }
        });

        // pass through the click event of the confirm button
        Button actionButton = (Button) content.findViewById(R.id.buttonAction);
        actionButton.setText(pConfirmButtonText);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                pConfirmClickListener.onClick(MessageDialog.this, 0);
            }
        });

        if (pConfirmClickListener == null) {
            actionButton.setVisibility(View.GONE);
        }
    }

}
