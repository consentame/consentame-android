package me.consenta.android.consentame.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.utils.Constants;
import me.consenta.android.consentame.utils.UIMapper;

public class ConsentDetailsActivity extends AppCompatActivity {

    private static Consent consent = null;

    private static LinkedList<UserChoice> userChoices;

    private static String exitMsg;
    private static boolean unreadErrors;

    private static ConsentDetailsActivity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_details);

        userChoices = new LinkedList<>();

        unreadErrors = false;

        if (consent != null)
            return;

        // load consent from json only if 'consent' is not initialized
        Intent caller = getIntent();

        ObjectMapper mapper = new ObjectMapper();
        String consentJson = caller.getStringExtra("consent-json");

        if (consentJson == null || consentJson.isEmpty()) {
            setErrorMessage("no Consent fetched (null)");
            return;
        }

        try {
            // try to write data to a Consent object
            consent = mapper.readValue(consentJson, Consent.class);
        } catch (IOException e) {
            String err;
            // unknown error
            if (Constants.DEV) {
                err = e.getMessage();
            } else {
                err = "Consent not found. Please contact the app developer.";
            }

            exitMsg = err;
            unreadErrors = true;
            finish();
        }
    }

    /**
     *  map Consent object to UI
     */
    private void mapToUI() {
        // Data Processors
        LinearLayout dataControllersList = findViewById(R.id.data_controllers_list);
        UIMapper.map(consent.getDataProcessors(), dataControllersList);

        // T&C
        RelativeLayout termsAndConditionsBox = findViewById(R.id.tec);
        UIMapper.map(consent.termsAndConditions(), termsAndConditionsBox);

        // Privacy Policy
        TextView privacyPolicyCompleteLink = findViewById(R.id.policy_complete_link);
        privacyPolicyCompleteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open policy URL
                Intent openURL = new Intent(Intent.ACTION_VIEW, Uri.parse(consent.getPolicyUrl()));
                v.getContext().startActivity(openURL);
            }
        });

        // Purposes
        LinearLayout purposesList = findViewById(R.id.purposes_list);
        UIMapper.map(consent.getPurposes(), purposesList);


        /* * * add listener to Submit button * * */
        final ScrollView scrollView = findViewById(R.id.scroll_consent);

        final ConsentDetailsActivity thisConsentDetailsActivity = this;
        Button submitBtn = findViewById(R.id.check_and_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat unchecked = parseUserChoices();
                if (unchecked == null) {
                    // OK, send consent and call ConsentaMeCheckButton.check()
                    Intent i = new Intent(v.getContext(), SubmitConsentActivity.initClass(userChoices));
                    i.putExtra("id", consent.getConsentId());
                    current = thisConsentDetailsActivity;
                    startActivity(i);
                } else {
                    unchecked.requestFocus();
                    scrollView.scrollTo(unchecked.getScrollX(), unchecked.getScrollY());
                    unchecked.setText(getResources().getString(R.string.mandatory_check_text));
                    scrollView.invalidate();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mapToUI();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        ConsentaMeCheckButton.releaseCurrent();
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public static String readErrorMessage() {
        if (unreadErrors)
            return exitMsg;
        else
            return "";
    }

    static void setErrorMessage(String msg) {
        if (msg == null || msg.isEmpty()) {
            unreadErrors = false;
            exitMsg = null;
        } else {
            unreadErrors = true;
            exitMsg = msg;
        }
    }

    public static void addChoice(View v, int id, boolean mandatory) {
        final SwitchCompat sel = v.findViewById(R.id.selector);
        if (mandatory) {
            sel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    // Hide "MANDATORY" text when clicked. Text is displayed by
                    // setShowText(true), called inside OnClickListener of the "submit" button
                    if (clickedView == sel) {
                        sel.setText("");
                        sel.invalidate();
                    }
                }
            });
        }

        userChoices.add(new UserChoice(sel, id, mandatory));
    }

    public static SwitchCompat parseUserChoices() {
        for (UserChoice choice : userChoices) {
            if (! choice.isAcceptable()) {
                return choice.getSwitch();
            }
        }

        return null;
    }

    /**
     * @return the current running instance of {@link ConsentDetailsActivity}
     */
    public static ConsentDetailsActivity getCurrent() {
        return current;
    }

    /**
     * Notify this Activity of successful submit.
     * It is invoked by {@link SubmitConsentActivity} after a successful Consent submission.
     *
     * @param notificator the {@link SubmitConsentActivity} which ended successfully.
     */
    public void notifySuccess(final SubmitConsentActivity notificator) {
        if (notificator == null) {throw new IllegalArgumentException("Not a valid submission.");}
        consent = null;
        finish();
    }
}
