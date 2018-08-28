package me.consenta.android.consentame.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import me.consenta.android.consentame.ConsentaMeCheckButton;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;
import me.consenta.android.consentame.model.Purpose;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.utils.Constants;
import me.consenta.android.consentame.utils.UIMapper;

public class ConsentDetailsActivity extends AppCompatActivity {



    /**
     * Execution mode of this Consent object
     */
    private enum Mode {CREATE, UPDATE}

    /**
     * Default ID of the Terms & Condition switch
     */
    private static final String TERMS_AND_CONDITIONS = "terms_and_conditions";

    private static Consent consent = null;
    private static Mode mode = null;
    private boolean isMapped = false;

    private static HashMap<String, UserChoice> userChoices;

    private static String exitMsg;
    private static boolean unreadErrors;

    private static ConsentDetailsActivity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_details);

        userChoices = new HashMap<>();

        unreadErrors = false;

        if (consent != null)
            return;

        // load consent from json only if 'consent' is not initialized
        Intent caller = getIntent();

        ObjectMapper mapper = new ObjectMapper();
        String consentJson = caller.getStringExtra("me.consenta.android.consent-json");
        LinkedList<String> acceptedPurposes = getPurposes(
                caller.getStringArrayListExtra("me.consenta.android.purposes")
        );

        // set execution mode
        if (acceptedPurposes == null) {
            mode = Mode.CREATE;
        } else {
            mode = Mode.UPDATE;
        }

        if (consentJson == null || consentJson.isEmpty()) {
            setErrorMessage("no Consent fetched (null)");
            return;
        }

        try {
            // try to write data to a Consent object
            consent = mapper.readValue(consentJson, Consent.class);

            // save checked purposes in consent
            if (mode == Mode.UPDATE) {
                if (acceptedPurposes.contains(TERMS_AND_CONDITIONS)) {
                    consent.getTermsAndConditions().setChecked(true);
                }

                for (Purpose p : consent.getPurposes()) {
                    String purposeID = p.getInternalId();
                    if (acceptedPurposes.contains(purposeID)) {
                        p.getAdditionalProperties().put("accepted", true);
                    }
                }
            }
        } catch (IOException e) {
            String err;
            // unknown error
            if (Constants.DEV) {
                err = e.getMessage();
            } else {
                err = "Invalid consent. Please contact the app developer.";
            }

            // don't call 'setErrorMessage()'! 'err' might be null, but in this case an error must
            // always be displayed.
            unreadErrors = true;
            exitMsg = err;
            finish();
        }
    }

    private LinkedList<String> getPurposes(ArrayList<String> purposes) {
        if (purposes == null) {
            return null;
        }
        return new LinkedList<>(
                purposes
        );
    }

    /**
     *  map Consent object to UI elements
     */
    private void mapToUI() {
        if(isMapped) {
            return;
        } else {
            isMapped = true;
        }

        // Data Processors
        LinearLayout dataControllersList = findViewById(R.id.data_controllers_list);
        UIMapper.map(consent.getDataProcessors(), dataControllersList);

        // T&C
        RelativeLayout termsAndConditionsBox = findViewById(R.id.tec);
        UIMapper.map(consent.getTermsAndConditions(), termsAndConditionsBox);

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
        final Button submitBtn = findViewById(R.id.check_and_submit);
        if (mode == Mode.UPDATE) {
            submitBtn.setText(R.string.consent_back_btn_text);
        } else {
            submitBtn.setText(R.string.consent_submit_btn_text);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat unchecked = parseUserChoices();
                if (unchecked != null) {
                    // focus on the non-acceptable choice and show that it's mandatory
                    unchecked.requestFocus();
                    scrollView.scrollTo(unchecked.getScrollX(), unchecked.getScrollY());
                    unchecked.setText(getResources().getString(R.string.mandatory_check_text));
                    unchecked.setTextColor(getResources().getColor(R.color.error_red));
                    scrollView.invalidate();
                    return;
                }

                // all mandatory purposes are checked
                if (mode == Mode.CREATE) {
                    // OK, send consent and call ConsentaMeCheckButton.check()
                    Intent i = new Intent(v.getContext(), SubmitConsentActivity.initClass(userChoices.values()));
                    i.putExtra("me.consenta.android.id", consent.getConsentId());
                    current = thisConsentDetailsActivity;
                    startActivity(i);
                } else if (mode == Mode.UPDATE) {
                    boolean isModified = submitBtn.getText().equals(
                            getString(R.string.consent_update_btn_text)
                    );

                    if (isModified) {
                        Intent i = new Intent(v.getContext(), UpdateConsentActivity.initClass(userChoices.values()));

                        // TODO send update consent request
                        System.out.println("*UPDATE CONSENT*");
                    } else {
                        onBackPressed();
                    }
                } else
                    throw new IllegalStateException("Invalid value for 'mode'");
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

    public static void addChoice(final View v, final int id, final boolean mandatory, final boolean selected) {
        final SwitchCompat sel = v.findViewById(R.id.selector);
        sel.setChecked(selected);


        sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                if (mandatory) {
                    // Hide "MANDATORY" text when clicked. Text is displayed by
                    // setShowText(true), called inside OnClickListener of the submitBtn
                    if (clickedView == sel) {
                        sel.setText("");
                        sel.invalidate();
                    }
                }

                // set confirmation button text to "UPDATE"
                if (mode == Mode.UPDATE) {
                    Button submitBtn = v.getRootView().findViewById(R.id.check_and_submit);
                    submitBtn.setText(R.string.consent_update_btn_text);
                }
            }
        });

        userChoices.put("" + id, new UserChoice(sel, id, mandatory));
    }

    public static SwitchCompat parseUserChoices() {
        for (UserChoice choice : userChoices.values()) {
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
        if (notificator == null) {throw new IllegalArgumentException("Invalid notifier.");}
        notifySuccess();
    }

    private void notifySuccess() {
        consent = null;
        mode = null;
        finish();
    }
}
