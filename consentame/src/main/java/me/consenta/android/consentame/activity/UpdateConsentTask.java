package me.consenta.android.consentame.activity;

import android.view.View;
import android.widget.Toast;

import java.util.List;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.UserChoice;

class UpdateConsentTask extends SubmitConsentTask {

    public UpdateConsentTask(UpdateConsentActivity caller, String consentId, List<UserChoice> choices) {
        super(caller, consentId, choices);
    }

    @Override
    protected void onPreExecute() {
        caller.setConsoleText("Updating...", false);
        caller.loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return super.doInBackground(voids);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        caller.loading.setVisibility(View.GONE);
        Toast.makeText(caller.getApplicationContext(), R.string.success_toast_msg, Toast.LENGTH_SHORT)
                .show();
        caller.notifySuccess(payload);
        if (success) {
            ConsentaMeCheckButton.releaseCurrent();
            caller.setConsoleText("Success!", false);
            caller.action.setText("Continue");
            caller.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // terminate activity
                    caller.finish();
                }
            });
            caller.action.setVisibility(View.VISIBLE);
        } else {
            super.showError();
        }
    }
}
