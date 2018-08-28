package me.consenta.android.consentame.activity;

import android.os.AsyncTask;

import java.util.List;

import me.consenta.android.consentame.model.UserChoice;

class UpdateConsentTask extends AsyncTask<Void, Void, Boolean> {
    public UpdateConsentTask(UpdateConsentActivity caller, String consentId, List<UserChoice> choices) {
        // TODO check if this can use the same UserConsentRequest object used by SubmitConsentTask;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // TODO
        return null;
    }
}
