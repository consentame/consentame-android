package me.consenta.android.consentame.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;
import me.consenta.android.consentame.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.consenta.android.consentame.utils.Constants.DEMO;
import static me.consenta.android.consentame.utils.Constants.DEV;

/**
 * Performs calls to Consenta.me to retrieve a Consent object.
 * Automatically switches to {@link ConsentDetailsActivity} once the
 * data have been downloaded.
 *
 */
public final class ConsentaMeActivity extends AppCompatActivity {

    TextView console;
    ProgressBar loading;
    Button retry;

    static int ERR_COLOR, MSG_COLOR;

    private String consentId = null,
                    userConsentId = null;

    public static Class<ConsentaMeActivity> setUpClass() {
        if (ConsentaMeCheckButton.getCurrentInstance() != null) {
            throw new ConcurrentModificationException("You may have only one instance of ConsentaMeActivity running at any time.");
        }
        return ConsentaMeActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consenta_me);


        ERR_COLOR = getResources().getColor(R.color.error_red);
        MSG_COLOR = getResources().getColor(R.color.main_light);

        console = findViewById(R.id.loading_console);
        loading = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry_button);

        consentId = getIntent().getStringExtra("id");
        userConsentId = getIntent().getStringExtra("user_consent_id");

        final ConsentaMeActivity thisConsentaMeActivity = this;
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEMO)
                    new DemoFetchConsentTask(thisConsentaMeActivity).execute();
                else
                    new FetchConsentTask().execute(consentId, userConsentId);
            }
        });

        if (DEMO)
            new DemoFetchConsentTask(this).execute();
        else
            new FetchConsentTask().execute(consentId, userConsentId);

        String error = ConsentDetailsActivity.readErrorMessage();

        if (!error.isEmpty()) {
            setConsoleText(error, true);
            retry.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show a message on this activity's console
     *
     * @param text the message to be shown.
     *            If {@code null} or empty, the console will be {@link View#GONE GONE}.
     * @param error if true, the text will be printed in ERR_COLOR instead of the standard color.
     */
    public void setConsoleText(String text, boolean error) {
        if (text == null || text.isEmpty()) {
            console.setVisibility(View.GONE);
            return;
        }

        if (error) {
            console.setTextColor(ERR_COLOR);
        } else {
            console.setTextColor(MSG_COLOR);
        }

        console.setText(text);
    }

    /**
     * Method used to notify this class that this Activity is not needed
     * anymore. After being notified it will be terminated with {@link #finish()}.
     */
    public void notifySuccess() {
        finish();
    }

    @Override
    public void onBackPressed() {
        ConsentaMeCheckButton.releaseCurrent();
        super.onBackPressed();
    }

    /**
     * {@link AsyncTask} implementation that fetches a consent from
     * <a href="https://consenta.me">Consenta.me</a>
     */
    private final class FetchConsentTask extends AsyncTask<String, Void, HashMap<String, String>> {

        private boolean success;
        private OkHttpClient httpClient;


        FetchConsentTask() {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }

        @Override
        protected void onPreExecute() {
            setConsoleText("Loading...", false);
            retry.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            HashMap<String, String> data = new HashMap<>();

            String consentId = strings[0];
            String ucID = strings[1];

            if (DEMO) {
                return data;
            }

            String fetchConsent = Constants.HOST + "/" + consentId;

            // API call to Consenta.me to fetch consent details
            try {
                Request readConsent = new Request.Builder()
                        .get()
                        .url(fetchConsent)
                        .build();
                Response resp = httpClient.newCall(readConsent).execute();

                // return JSON string
                data.put("consent", resp.body().string());

                if (ucID != null) {
                    // Fetch consent
                    // https://dev.consenta.me/api/userconsent/USER_CONSENT_ID/check/
                    String fetchAcceptedPurposes = Constants.HOST + "/api/userconsent/" + ucID + "/check";

                    Request readPurposes = new Request.Builder()
                            .get()
                            .url(fetchAcceptedPurposes)
                            .build();
                    resp = httpClient.newCall(readPurposes).execute();


                    // return JSON string
                    data.put("purposes", resp.body().string());
                }
            } catch (IOException e) {
                // return client's error message
                if (DEV)
                    e.printStackTrace();
                data.put("error", e.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> returnValues) {
            loading.setVisibility(View.GONE);

            success = !(returnValues.containsKey("error"));

            ObjectMapper mapper = new ObjectMapper();
            try {
                // try to map AsyncTask's result on a 'Consent' object
                mapper.readValue(
                        returnValues.get("consent"),
                        Consent.class
                );
            } catch (IOException e) {
                // failed to map - not a Consent
                success = false;
                returnValues.put("error", e.getMessage());
            }

            String[] purposes = null;
            if (returnValues.containsKey("purposes")) {
                try {
                    purposes = mapper.readValue(
                            returnValues.get("purposes"),
                            TypeFactory.defaultInstance().constructArrayType(String.class)
                    );
                } catch (IOException e) {
                    // failed to map - not a valid JSON array
                    success = false;
                    returnValues.put("error", e.getMessage());
                }
            }

            if (success) {
                // show Consent details
                setConsoleText(null, false);
                Intent intent = new Intent(ConsentaMeActivity.this, ConsentDetailsActivity.class);
                intent.putExtra("consent-json", returnValues.get("consent"));
                intent.putExtra("purposes", purposes);

                notifySuccess(); // finish ConsentaMeActivity
                startActivity(intent); // start ConsentDetailsActivity
            } else {
                // show error message
                String err = returnValues.get("error");
                if (DEV)
                    setConsoleText(err, true);
                else {
                    setConsoleText("There is an error in the application. \nPlease contact the developer.", true);
                    Logger.getLogger(this.getClass().getCanonicalName()).severe(err);
                }

                retry.setVisibility(View.VISIBLE);
            }
        }
    }
}
