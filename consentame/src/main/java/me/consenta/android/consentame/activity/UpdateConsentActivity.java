package me.consenta.android.consentame.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import me.consenta.android.consentame.model.UpdateConsentRequest;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.model.UserConsentRequest;
import me.consenta.android.consentame.utils.Constants;

public final class UpdateConsentActivity extends SubmitConsentActivity {

    private static String accessToken;

    public static Class<UpdateConsentActivity> initClass(String temporaryToken, Collection<UserChoice> userChoicesList) {
        choices = new LinkedList<>(userChoicesList);
        // user consent ID is read in super.onCreate() and saved in 'consentId' variable
        accessToken = temporaryToken;
        return UpdateConsentActivity.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void submit() {
        if (choices != null) {
            new UpdateConsentTask(this, consentId, choices).execute();
        } else {
            // class not initialized
            if (Constants.DEV)
                throw new RuntimeException("You have to use method initClass(List<UserChoice>) before starting this activity.");
            else
                setConsoleText("SubmitConsentActivity: Internal app error (wrong initialization). Please contact the developer.", true);
        }
    }

    @Override
    public UserConsentRequest getRequestWrapper(String consentId, List<UserChoice> choices) {
        return new UpdateConsentRequest(consentId, accessToken, choices);
    }
}
