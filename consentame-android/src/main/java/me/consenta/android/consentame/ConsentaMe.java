package me.consenta.android.consentame;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * This class provide some utility methods to interact with a {@link ConsentaMeCheckButton}
 */
public final class ConsentaMe {

    private final ConsentaMeCheckButton button;

    /**
     * Create a new instance of {@link ConsentaMe} linked to the specified {@link ConsentaMeCheckButton}
     * @param checkButton the button which will be linked to the new object
     *
     * @see ConsentaMe
     */
    public ConsentaMe(ConsentaMeCheckButton checkButton) {
        this.button = checkButton;
    }

    /**
     * Create a new instance of {@link ConsentaMe} linked to the specified {@link ConsentaMeCheckButton}
     * @param view a {@link View} that contains the target {@link ConsentaMeCheckButton}
     * @param checkButtonResId the resource id of a {@link ConsentaMeCheckButton}
     *
     * @see ConsentaMe
     */
    public ConsentaMe(View view, int checkButtonResId) {
        this.button = view.findViewById(checkButtonResId);
    }

    /**
     * Create a new instance of {@link ConsentaMe} linked to the specified {@link ConsentaMeCheckButton}
     * @param activity an {@link Activity} that contains the target {@link ConsentaMeCheckButton}
     * @param checkButtonResId the resource id of a {@link ConsentaMeCheckButton}
     *
     * @see ConsentaMe
     */
    public ConsentaMe(Activity activity, int checkButtonResId) {
        this.button = activity.findViewById(checkButtonResId);
    }

    /**
     * Get the Consent ID of the linked {@link ConsentaMeCheckButton}
     * @return a {@link String} containing the Consent ID
     */
    public String getConsentId() {
        return button.getConsentId();
    }

    /**
     * Get the User Consent ID of the linked {@link ConsentaMeCheckButton}
     * @return a {@link String} containing the User Consent ID. If the button was not checked,
     *      this will return {@code null}
     *
     * @see #isChecked()
     */
    @Nullable
    public String getUserConsentId() {
        return button.getUserConsentId();
    }

    /**
     * Check if the linked {@link ConsentaMeCheckButton} has already been checked by the user.<br>
     * <br>
     * The "checked" status of a {@link ConsentaMeCheckButton} is {@code false} by default
     * and will become {@code true} once the user has successfully submitted the consent.
     *
     * @return the checked status of the linked {@link ConsentaMeCheckButton}.
     */
    public boolean isChecked() {
        return button.isChecked();
    }

    /**
     * Get the Consent ID of the currently running instance of {@link ConsentaMeCheckButton}
     * @return a {@link String} containing the Consent ID or {@code null} if no instance is running.
     */
    @Nullable
    public static String getCurrentConsentId() {
        if (!isRunning())
            return null;

        return ConsentaMeCheckButton.getCurrentInstance().getConsentId();
    }

    /**
     * Get the User Consent ID of the currently running instance of {@link ConsentaMeCheckButton}
     * @return a {@link String} containing the User Consent ID or {@code null} if no instance is running.
     */
    public static String getCurrentUserConsentId() {
        if (!isRunning())
            return null;

        return ConsentaMeCheckButton.getCurrentInstance().getUserConsentId();
    }

    /**
     * Check whether the currently running instance of {@link ConsentaMeCheckButton}
     * has already been checked by the user.
     * @return {@code true} if the button has been checked, {@code false} if not, or if
     * there is no currently running instance.
     *
     * @see #isRunning()
     */
    public static boolean isCurrentChecked() {
        return isRunning() && ConsentaMeCheckButton.getCurrentInstance().isChecked();
    }

    /**
     * Check whether there is a currently running instance of {@link ConsentaMeCheckButton},
     * which prevents other buttons to be clicked on.
     * @return {@code true} if there is an instance of {@link ConsentaMeCheckButton running},
     *      {@code false} otherwise.
     */
    public static boolean isRunning() {
        return ConsentaMeCheckButton.getCurrentInstance() != null;
    }

}
