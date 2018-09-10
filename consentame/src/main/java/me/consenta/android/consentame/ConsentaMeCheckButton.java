package me.consenta.android.consentame;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.Keep;
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
@Keep
public final class ConsentaMeCheckButton extends LinearLayout {

    // base attributes
    private static ConsentaMeCheckButton currentButton = null;
    private String consentId;
    private boolean checked;
    private OnUserConsentListener listener = null;

    // update operation
    private String userConsentId;
    private String updateAccessToken = null;


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

            setupOnClick(context);

            Logger.getLogger(getClass().getSimpleName()).info("setup complete");
        } finally {
            a.recycle();
        }

        LayoutInflater l = LayoutInflater.from(context);
        l.inflate(R.layout.consenta_me_check_btn, this);
        ImageSwitcher checkboxes = findViewById(R.id.checkboxes);
        checkboxes.setImageResource(R.drawable.ic_square);
        checked = false;
    }

    private void setupOnClick(Context context) {
        super.setOnClickListener(new ConsentaMeOnClickListener(context, this));
    }

    void setListener(OnUserConsentListener listener) {
        this.listener = listener;
    }

    /**
     * Change the state of the currently running instance of the Button to 'checked'
     */
    public static void setCurrentButtonChecked(String userConsentId) {
        ImageSwitcher checkBoxImg = currentButton.findViewById(R.id.checkboxes);
        checkBoxImg.setImageResource(R.drawable.ic_check_square);
        currentButton.userConsentId = userConsentId;
        currentButton.checked = true;
    }

    /**
     * Change the Button's state to 'checked'
     */
    void setButtonChecked(String userConsentId) {
        ImageSwitcher checkBoxImg = this.findViewById(R.id.checkboxes);
        checkBoxImg.setImageResource(R.drawable.ic_check_square);
        this.userConsentId = userConsentId;
        this.checked = true;
    }

    /**
     * Change the Button's state to 'unchecked'
     */
    void setButtonUnchecked() {
        ImageSwitcher checkBoxImg = this.findViewById(R.id.checkboxes);
        checkBoxImg.setImageResource(R.drawable.ic_square);
        this.userConsentId = null;
        this.checked = false;
    }

    /**
     * Store the access token that will be used for updating the Consent.
     *
     * @param token the access token that is needed to update a Consent. Tokens are provided to
     *              the application's backend by Consenta.me API and will then be sent
     *              to the app by the backend.
     */
    void setAccessToken(String token) {
        updateAccessToken = token;
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


    /**
     * {@link android.view.View.OnClickListener} implementation that handles the click actions on
     * the {@link ConsentaMeCheckButton}.
     */
    public final class ConsentaMeOnClickListener implements View.OnClickListener {

        private Context context;
        private ConsentaMeCheckButton thisBtn;
        private ConsentaMe btnHandler;

        public ConsentaMeOnClickListener(final Context appContext, final ConsentaMeCheckButton button) {
            context = appContext;
            thisBtn = button;
            btnHandler = new ConsentaMe(thisBtn);
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, ConsentaMeActivity.setUpClass());
            intent.putExtra("me.consenta.android.id", btnHandler.getConsentId());
            // if 'null', the ConsentaMeActivity will create a new consent,
            // otherwise the old one will be fetched and updated.
            intent.putExtra("me.consenta.android.user_consent_id", btnHandler.getUserConsentId());
            intent.putExtra("me.consenta.android.consent_update_token", updateAccessToken);
            intent.putExtra("me.consenta.android.listener",
                    ConsentaMeActivity.registerListener(listener)
            );
            currentButton = thisBtn;
            context.startActivity(intent);
        }
    }
}
