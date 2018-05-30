package me.consenta.android.consentame.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;

import static me.consenta.android.consentame.utils.Constants.DEV;

public final class DemoFetchConsentTask extends AsyncTask<Void, Void, String>{

    private boolean success;

    private ConsentaMeActivity caller;

    public DemoFetchConsentTask(ConsentaMeActivity caller) {
        this.caller = caller;
    }

    @Override
    protected void onPreExecute() {
        caller.loading.setVisibility(View.VISIBLE);
        caller.setConsoleText("Loading...", false);
    }

    @Override
    protected String doInBackground(Void... v) {
        try {
            StringBuilder json = new StringBuilder();

            InputStream file = caller.getResources().openRawResource(R.raw.consent);
            Scanner consent = new Scanner(file);

            while(consent.hasNextLine()) {
                json.append(consent.nextLine()).append("\n");
            }

            success = true;
            return json.toString();

        } catch (Exception e) {
            if (DEV) {
                e.printStackTrace();
            }
            return "ERROR: could not read demo data: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String jsonResponse) {
        caller.loading.setVisibility(View.GONE);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readValue(
                    jsonResponse,
                    Consent.class
            );
            success = true;
        } catch (IOException e) {
            success = false;
        }

        if (success) {
            caller.setConsoleText(null, false);
            Intent intent = new Intent(caller.getApplicationContext(), ConsentDetailsActivity.class);
            intent.putExtra("consent-json", jsonResponse);

            caller.startActivity(intent);
        } else {
            ConsentDetailsActivity.setErrorMessage(jsonResponse);
//            caller.setConsoleText(jsonResponse, true);
        }
    }
}
