package me.consenta.android.consentame.activity;

import android.os.AsyncTask;
import android.view.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.model.SubmitErrorResponse;
import me.consenta.android.consentame.model.SubmitSuccessResponse;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.model.UserConsentRequest;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static me.consenta.android.consentame.utils.Constants.DEV;

class SubmitConsentTask extends AsyncTask<Void, Void, Boolean> {

    private static final String DEBUG_MSG = "DEBUG: ";

    private UserConsentRequest request;

    private String payload = "no payload";

    private SubmitConsentActivity caller;

    SubmitConsentTask(SubmitConsentActivity caller, String consentId, List<UserChoice> choices) {
        super();
        this.caller = caller;
        request = new UserConsentRequest(consentId, choices);
    }

    @Override
    protected void onPreExecute() {
        caller.setConsoleText("Submitting...", false);
        caller.loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Response res;
        String resContent;
        try {
            // HTTP call to Consenta.me API
            resContent = httpClient.newCall(request.parse()).execute().body().string();
        } catch (NullPointerException npex) {
            payload = DEBUG_MSG + "Null body found inside server response";
            return false;
        } catch (JsonProcessingException e) {
            // failed to parse response to json
            payload = DEBUG_MSG + "" + e.getMessage();
            return false;
        } catch (IOException e) {
            // catches Exceptions from string() (when null) and execute()
            payload = e.getMessage();
            return false;
        }

        // Once received a valid response, extract information from it:
        try {
            try {
                // try to map on a SubmitSuccessResponse
                ObjectMapper mapper = new ObjectMapper();
                SubmitSuccessResponse response = mapper.readValue(resContent, SubmitSuccessResponse.class);
                payload = response.getUserConsentId();

                /* SUCCESS - payload will contain the user consent ID returned by consenta.me */
                return true;

            } catch (JsonMappingException e) {
                // if the response is not a SubmitSuccessResponse, try to map on a SubmitErrorResponse
                ObjectMapper mapper = new ObjectMapper();
                SubmitErrorResponse response = mapper.readValue(resContent, SubmitErrorResponse.class);
                List<Integer> codes = response.getCodes();
                payload = "Please check mandatory items (" +
                        codes.size() +
                        " unchecked)";
                return false;
            }
        } catch (JsonMappingException e) {
            // response is neither SubmitSuccessResponse nor SubmitErrorResponse
            payload = DEBUG_MSG + "weird response from server:\n";
            payload += resContent;
            return false;
        } catch (JsonParseException e) {
            // malformed JSONN
            payload = DEBUG_MSG + "unable to parse response JSON:\n" + e.getMessage();
            return false;
        } catch (IOException e) {
            // low level I/O problem (e.g. connection or end of input) during mapping
            payload = e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        caller.loading.setVisibility(View.GONE);
        caller.notifySuccess();
        if (success) {
            ConsentaMeCheckButton.setCurrentButtonChecked(payload);
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
            if (payload.startsWith(DEBUG_MSG) && !DEV) {
                // TODO subclass exception
                caller.setConsoleText("Error: Something went wrong. Please try again", true);
            } else {
                caller.setConsoleText(payload, true);
            }
        }
    }
}
