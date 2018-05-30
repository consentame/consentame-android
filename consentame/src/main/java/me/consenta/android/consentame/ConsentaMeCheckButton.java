package me.consenta.android.consentame;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;

import java.util.logging.Logger;

import me.consenta.android.consentame.activity.ConsentaMeActivity;

import static me.consenta.android.consentame.utils.Constants.DEV;
import static me.consenta.android.consentame.utils.Constants.DEV_HOST;
import static me.consenta.android.consentame.utils.Constants.HOST;

/**
 * Default Consenta.me checkbox
 */
public final class ConsentaMeCheckButton extends LinearLayout {

    private String consentId;
    private boolean checked;
    private String userConsentId;

    private static ConsentaMeCheckButton currentButton = null;

    public ConsentaMeCheckButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ConsentaMeCheckButton,
                0, 0);
        try {
            this.consentId = a.getString(R.styleable.ConsentaMeCheckButton_consentId);
            DEV = a.getBoolean(R.styleable.ConsentaMeCheckButton_dev, false);
            if (DEV)
                HOST = DEV_HOST;

            Logger.getLogger(getClass().getSimpleName()).info("correctly set consentId");

            final ConsentaMeCheckButton thisBtn = this;

            super.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent;
                    if (!checked) {
                        intent = new Intent(context, ConsentaMeActivity.setUpClass());
                        intent.putExtra("id", consentId);

                        currentButton = thisBtn;

                        context.startActivity(intent);
                    }
//                    TODO (backlog): update consent and move context.startActivity() to the bottom
//                    else {
//                       // update consent
//                    }
//                    context.startActivity(intent);
                }
            });
        } finally {
            a.recycle();
        }

        LayoutInflater l = LayoutInflater.from(context);
        l.inflate(R.layout.consenta_me_check_btn, this);
        ImageSwitcher checkboxes = findViewById(R.id.checkboxes);
        checkboxes.setImageResource(R.drawable.ic_square);
        checked = false;
    }

    /**
     * Change the Button's current state to 'checked'
     */
    public static void setCurrentButtonChecked(String userConsentId) {
        ImageSwitcher checkBoxImg = currentButton.findViewById(R.id.checkboxes);
        checkBoxImg.setImageResource(R.drawable.ic_check_square);
        currentButton.userConsentId = userConsentId;
        currentButton.checked = true;
    }

    /**
     * Get the current instance of {@link ConsentaMeCheckButton}, that was clicked by a user.
     * As soon as the user successfully submits the Consent this method will return {@code null}.
     * @return the instance of {@link ConsentaMeCheckButton} that was selected by the user,
     * or {@code null} if the user has already submitted the Consent.
     */
    @Nullable
    public static ConsentaMeCheckButton getCurrentInstance() {
        return currentButton;
    }

    /**
     *  Reset the static current instance of this class
     */
    public static void releaseCurrent() {
        currentButton = null;
    }

    /**
     * <b>WARNING: the default {@link android.view.View.OnClickListener} on this object cannot be changed.</b>
     * Invoking this method on {@link ConsentaMeCheckButton} will trigger an exception.
     *
     * @param l will be ignored.
     * @throws UnsupportedOperationException The default OnClickListener for this element cannot be overridden.
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        throw new UnsupportedOperationException("The default OnClickListener for this element cannot be overridden.");
    }

    /* * * Class interface (exposed by class ConsentaMe) * * */

    /**
     * Get the id of the Consent that was set for this Button.
     *
     * @return a {@link String} containing the Consent ID
     */
    String getConsentId() {
        return consentId;
    }

    /**
     * Verify the 'checked' status of this Button
     * @return {@code true} if the Consent was correctly approved by the user
     */
    boolean isChecked() {
        return checked;
    }

    /**
     * Get the current ID of an accepted Consent.
     * If the user has not agreed, this method will return {@code null}.
     *
     * @return a user consent ID, or {@code null} if the user has not accepted the Consent yet
     */
    @Nullable
    String getUserConsentId() {
        return userConsentId;
    }
}
