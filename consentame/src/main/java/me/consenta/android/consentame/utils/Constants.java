package me.consenta.android.consentame.utils;

public class Constants {

    /**
     * Automatically set to {@code true} when setting to {@code true} the XML
     * attribute "dev" of {@link me.consenta.android.consentame.ConsentaMeCheckButton ConsentaMeCheckButton}
     *
     */
    public static boolean DEV = false;

    /**
     * Set to true {@code true} when testing with sample data (offline)
     */
    public final static boolean DEMO = false;



    /* * * Do not edit below this line * * */

    /**
     * URL of production API
     */
    public static String HOST = "";

    /**
     * URL of test API
     */
    public final static String DEV_HOST = "https://dev.consenta.me";

    public final static String TEST_HOST = "http://eb339f07.ngrok.io";

    // Constant values used to pass values between activities
    public static final class a7f681dac288 {
        public static final String
                c1a85f46bfa4 = "me.consenta.android.id",                        // ID
                b6659757401e = "me.consenta.android.user_consent_id",           // ucID
                cebafe44a0ba = "me.consenta.android.consent_update_token",      // access token
                f5f17645526e = "me.consenta.android.listener",                  // ID of OnUserConsentListener
                dcced0583eb7 = "me.consenta.android.consent-json",              // Consent
                ee2c0c648253 = "me.consenta.android.purposes";                  // list of purposes
    }
}
