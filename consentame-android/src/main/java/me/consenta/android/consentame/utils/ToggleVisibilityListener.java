package me.consenta.android.consentame.utils;

import android.view.View;
import android.widget.TextView;

public class ToggleVisibilityListener implements View.OnClickListener {

    private final View targetView;
    private final String shownLabel, hiddenLabel;

    /**
     * Implementation of {@link android.view.View.OnClickListener} that
     * allows to change visibility of a View between {@link View#GONE GONE}
     * and {@link View#VISIBLE}
     * @param target the {@link View} that will be shown/hidden by this listener
     */
    public ToggleVisibilityListener(View target, String textIfShown, String textIfHidden) {
        targetView = target;
        shownLabel = textIfShown;
        hiddenLabel = textIfHidden;
    }

    @Override
    public void onClick(View v) {
        if (targetView.getVisibility() == View.GONE) {
            targetView.setVisibility(View.VISIBLE);
            if (v instanceof TextView) {
                ((TextView) v).setText(shownLabel);
            }
        } else if (targetView.getVisibility() == View.VISIBLE) {
            targetView.setVisibility(View.GONE);
            if (v instanceof TextView) {
                ((TextView) v).setText(hiddenLabel);
            }
        }
    }
}
