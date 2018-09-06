package me.consenta.android.consentame.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;
import me.consenta.android.consentame.model.Purpose;
import me.consenta.android.consentame.model.TermsAndConditions;
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

    private String token = null;

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

        console.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(
                        ClipData.newPlainText("consentaError", console.getText())
                );
                Toast.makeText(ConsentaMeActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        consentId = getIntent().getStringExtra("me.consenta.android.id");

        // parameters for update operation - on creation of a new Consent these params are 'null'
        userConsentId = getIntent().getStringExtra("me.consenta.android.user_consent_id");
        token = getIntent().getStringExtra("me.consenta.android.consent_update_token");

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

        String error = ConsentDetailsActivity.getErrorMessage();

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

        console.setText(text);
        TextView errorInstructions = findViewById(R.id.copy_error_msg_instructions);

        if (error) {
            console.setTextColor(ERR_COLOR);
            if (DEV)
                errorInstructions.setVisibility(View.VISIBLE);
        } else {
            console.setTextColor(MSG_COLOR);
            errorInstructions.setVisibility(View.GONE);
        }
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
    private final class FetchConsentTask extends AsyncTask<String, Void, HashMap<String, Object>> {

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
        protected HashMap<String, Object> doInBackground(String... strings) {
            HashMap<String, Object> data = new HashMap<>();
            if (DEMO) {
                return data;
            }
            String consentId = strings[0];
            String ucID = strings[1];

            // API call to Consenta.me to fetch consent details
            String apiUrl;
            Response resp;
            try {
                if (ucID != null && token == null) {
                    throw new IOException("Access token required");
                }

                // Read Consent
                apiUrl = Constants.HOST + "/api/consent/" + consentId + "/";
                Request readConsent = new Request.Builder()
                        .get()
                        .url(apiUrl)
                        .build();
                // call API
                try {
                    resp = httpClient.newCall(readConsent).execute();
                } catch (SocketTimeoutException ex) {
                    throw new IOException("Server Timeout Error. The server took too long to respond.", ex);
                }
                // return JSON string
                data.put("consent", resp.body().string());

                if (ucID != null) {
                    // Fetch UserConsent
                    apiUrl = Constants.HOST + "/api/userconsent/" + ucID + "/?access_token=" + token;

                    Request readPurposes = new Request.Builder()
                            .get()
                            .url(apiUrl)
                            .build();
                    // call API
                    try {
                        resp = httpClient.newCall(readPurposes).execute();
                    } catch (SocketTimeoutException e) {
                        throw new IOException("Server Timeout Error. The server took too long to respond.", e);
                    }
                    // map response to HashMap
                    HashMap<String, Object> result;
                    result = new ObjectMapper().readValue(
                            resp.body().string(),
                            new TypeReference<HashMap>(){}
                    );
                    // add list of accepted Purpose IDs to returned values
                    ArrayList<Purpose> accepted = parseAcceptedPurposes(result);
                    data.put("purposes", accepted);
                }
            } catch (JsonParseException e) {
                // server returned error page and not a valid response
                if (DEV)
                    e.printStackTrace();
                data.put("error", "500 Internal Server Error");
            } catch (IOException e) {
                // return client's error message
                if (DEV)
                    e.printStackTrace();
                data.put("error", e.getMessage());
            }

            return data;
        }

        private ArrayList<Purpose> parseAcceptedPurposes(HashMap<String, Object> userConsentDetails) throws IOException {
            // verify required keys
            if (! (userConsentDetails.containsKey("accepted") && userConsentDetails.containsKey("terms_and_conditions"))) {
                // Exceptions are caught below and their message will be passed as an error
                if (userConsentDetails.containsKey("detail"))
                    throw new IOException("ERROR: " + userConsentDetails.get("detail"));
                throw new IOException("ERROR: failed to read user's consent");
            }
            // parse Purposes
            ArrayList<Purpose> accepted = new ArrayList<>();
            for (Object element : (List) userConsentDetails.get("accepted")) {
                Purpose p = new ObjectMapper().convertValue(
                        element,
                        Purpose.class
                );
                accepted.add(p);
            }
            // Parse T&C
            if ((Boolean) userConsentDetails.get("terms_and_conditions")) {
                Purpose p = new Purpose(
                        TermsAndConditions.ID,
                        "Terms and Conditions",
                        "terms_and_conditions"
                );
                accepted.add(p);
            }
            return accepted;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> returnValues) {
            loading.setVisibility(View.GONE);

            success = !(returnValues.containsKey("error"));

            ArrayList<Integer> purposes = null;

            if (success) {
                // try to map AsyncTask's result on a 'Consent' object;
                // update 'success' flag
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.readValue(
                            (String) returnValues.get("consent"),
                            Consent.class
                    );
                } catch (IOException e) {
                    // failed to map - not a Consent
                    success = false;
                    returnValues.put("error", e.getMessage());
                }

                purposes = null;
                if (returnValues.containsKey("purposes")) {
                    List<Purpose> acceptedPurposes = (List<Purpose>) returnValues.get("purposes");
                    purposes = new ArrayList<>() ;
                    for (Purpose p : acceptedPurposes) {
                        purposes.add(p.getId());
                    }
                }
            }

            if (success) {
                // show Consent details
                setConsoleText(null, false);
                Intent intent = new Intent(ConsentaMeActivity.this, ConsentDetailsActivity.class);
                intent.putExtra("me.consenta.android.consent-json", (String) returnValues.get("consent"));
                intent.putExtra("me.consenta.android.purposes", purposes);

                // parameters for Update operation - on creation, these values are null
                intent.putExtra("me.consenta.android.user-consent-id", userConsentId);
                intent.putExtra("me.consenta.android.token", token);

                notifySuccess(); // finish ConsentaMeActivity
                startActivity(intent); // start ConsentDetailsActivity
            } else {
                // show error message
                String err = (String) returnValues.get("error");
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
