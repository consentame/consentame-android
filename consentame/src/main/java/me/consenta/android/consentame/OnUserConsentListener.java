package me.consenta.android.consentame;

import android.support.annotation.Nullable;

public interface OnUserConsentListener {

    /**
     * This method is executed after the user successfully confirmed a Consent.
     * It can be used to store the UserConsent ID or to confirm the User Consent with the backend.
     *
     * @param ucID the User Consent ID returned by Consenta.me. MAY BE NULL.
     */
    public void handle(@Nullable String ucID);
}
