package me.consenta.android.consentame.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.consenta.android.consentame.utils.Constants;

public final class UpdateConsentActivity extends SubmitConsentActivity {

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
                setConsoleText("SubmitConsentActivity: Internal app error. Please contact the developer.", true);
        }
    }
}
