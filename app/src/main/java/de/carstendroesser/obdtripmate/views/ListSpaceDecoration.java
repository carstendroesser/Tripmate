package de.carstendroesser.obdtripmate.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by carstendrosser on 30.06.17.
 */

public class ListSpaceDecoration extends ItemDecoration {

    // MEMBERS

    private int mSpace;

    // CONSTRUCTORS

    /**
     * Used to add some space between listitems.
     *
     * @param pSpace the space between the listitem
     */
    public ListSpaceDecoration(int pSpace) {
        mSpace = pSpace;
    }

    // PUBLIC API

    @Override
    public void getItemOffsets(Rect pOutRect, View pView, RecyclerView pParent, State pState) {
        pOutRect.left = mSpace;
        pOutRect.right = mSpace;
        pOutRect.bottom = mSpace;

        if (pParent.getChildPosition(pView) == 0)
            pOutRect.top = mSpace;
    }

}
