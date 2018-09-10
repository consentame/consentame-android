package me.consenta.android.consentame.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import me.consenta.android.consentame.OnUserConsentListener;
import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.Consent;
import me.consenta.android.consentame.model.Purpose;
import me.consenta.android.consentame.model.TermsAndConditions;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.utils.Constants;
import me.consenta.android.consentame.utils.UIMapper;

import static me.consenta.android.consentame.utils.Constants.a7f681dac288.b6659757401e;
import static me.consenta.android.consentame.utils.Constants.a7f681dac288.c1a85f46bfa4;
import static me.consenta.android.consentame.utils.Constants.a7f681dac288.cebafe44a0ba;
import static me.consenta.android.consentame.utils.Constants.a7f681dac288.dcced0583eb7;
import static me.consenta.android.consentame.utils.Constants.a7f681dac288.ee2c0c648253;
import static me.consenta.android.consentame.utils.Constants.a7f681dac288.f5f17645526e;

public class ConsentDetailsActivity extends AppCompatActivity {

    /**
     * Execution mode of this {@link ConsentDetailsActivity}
     */
    private enum Mode {CREATE, UPDATE;
    }

    /**
     * Default ID of the Terms & Condition in the accepted purposes list
     */
    private static final String TERMS_AND_CONDITIONS = "terms_and_conditions";

    /**
     * Holds the currently running instance of {@link ConsentDetailsActivity} while
     * a {@link SubmitConsentTask} is running. No more than 1 instance can be running at any time.
     */
    private static ConsentDetailsActivity current;

    private static Consent consent = null;
    private static Mode mode = null;
    private boolean isMapped = false;
    private OnUserConsentListener afterListener = null;

    // params for UPDATE
    private String userConsentId = null;
    private String temporaryAccessToken = null;

    private static HashMap<String, UserChoice> userChoices;

    private static String exitMsg;
    private static boolean unreadErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_details);

        userChoices = new HashMap<>();

        // params for UPDATE
        Intent intent = getIntent();
        userConsentId = intent.getStringExtra(b6659757401e);
        temporaryAccessToken = intent.getStringExtra(cebafe44a0ba);

        // get OnUserConsentListener to be executed after
        afterListener = ConsentaMeActivity.getListener(
                intent.getStringExtra(f5f17645526e)
        );

        unreadErrors = false;

        // load consent from json only if 'consent' is not initialized
        if (consent != null) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        String consentJson = intent.getStringExtra(dcced0583eb7);
        LinkedList<Integer> acceptedPurposes = getPurposes(
                intent.getIntegerArrayListExtra(ee2c0c648253)
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
            if (consent.getAdditionalProperties().containsKey("detail")) {
                ConsentaMeCheckButton.releaseCurrent();
                throw new IOException((String) consent.getAdditionalProperties().get("detail"));
            }

            // save checked purposes in consent
            if (mode == Mode.UPDATE) {
//                if (acceptedPurposes.contains(TERMS_AND_CONDITIONS)) {
                if (acceptedPurposes.contains(TermsAndConditions.ID)) {
                    consent.getTermsAndConditions().setChecked(true);
                }

                for (Purpose p : consent.getPurposes()) {
                    int purposeID = p.getId();
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

    private LinkedList<Integer> getPurposes(ArrayList<Integer> purposes) {
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
                    i.putExtra(c1a85f46bfa4, consent.getConsentId());
                    current = thisConsentDetailsActivity;
                    startActivity(i);
                } else if (mode == Mode.UPDATE) {
                    boolean isModified = submitBtn.getText().equals(
                            getString(R.string.consent_update_btn_text)
                    );

                    if (isModified) {
                        Intent i = new Intent(v.getContext(), UpdateConsentActivity.initClass(temporaryAccessToken, userChoices.values()));

                        if(userConsentId == null) {
                            throw new IllegalArgumentException("No user consent ID provided");
                        }
                        if(temporaryAccessToken == null) {
                            throw new IllegalArgumentException("No access token provided");
                        }
                        i.putExtra(c1a85f46bfa4, userConsentId);

                        current = thisConsentDetailsActivity;
                        startActivity(i);

                        System.out.println("*UPDATE CONSENT*"); // TODO remove
                    } else {
                        // act like the back button
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

    public static String getErrorMessage() {
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

    public static void addChoice(final View v, final int id, String name, final boolean mandatory, final boolean selected) {
        final SwitchCompat sel = v.findViewById(R.id.selector);
        sel.setChecked(selected);

        sel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton changedView, boolean isChecked) {
                if (mandatory) {
                    // Hide "MANDATORY" text when clicked. Text is displayed by
                    // setShowText(true), called inside OnClickListener of the submitBtn
                    if (changedView == sel) {
                        sel.setText("");
                        sel.invalidate();
                    }
                }

                // set confirmation button text to "UPDATE" and activate restrictive clauses switch
                if (mode == Mode.UPDATE) {
                    Button submitBtn = v.getRootView().findViewById(R.id.check_and_submit);
                    submitBtn.setText(R.string.consent_update_btn_text);
                }
            }
        });

        userChoices.put(name, new UserChoice(sel, id, name, mandatory));
    }

    public static void addRestrictive(final RelativeLayout v) {
        final SwitchCompat restrictive = v.findViewById(R.id.restrictive_selector);
        // if the consent was accepted, the clauses MUST have been checked
        restrictive.setChecked(mode == Mode.UPDATE);

        restrictive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton changedView, boolean isChecked) {
                // Hide "MANDATORY" text when clicked. Text is displayed by
                // setShowText(true), called inside OnClickListener of the submitBtn
                if (changedView == restrictive) {
                    restrictive.setText("");
                    restrictive.invalidate();
                }
                // set confirmation button text to "UPDATE" and activate restrictive clauses switch
                if (mode == Mode.UPDATE) {
                    Button submitBtn = v.getRootView().findViewById(R.id.check_and_submit);
                    submitBtn.setText(R.string.consent_update_btn_text);
                }
            }
        });

        userChoices.put("restrictive", new UserChoice(restrictive, TermsAndConditions.RESTRICTIVE_ID, "restrictive", true));
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
     * This method will execute the associated {@link OnUserConsentListener
     * It is invoked by {@link SubmitConsentActivity} after a successful Consent submission.
     *
     * @param notificator the {@link SubmitConsentActivity} which ended successfully.
     */
    public void notifySuccess(final SubmitConsentActivity notificator, String userConsentId) {
        if (notificator == null) {throw new IllegalArgumentException("Invalid notifier.");}
        notifySuccess(userConsentId);
    }

    private void notifySuccess(String userConsentId) {
        consent = null;
        mode = null;

        if (afterListener != null)
            afterListener.handle(userConsentId);

        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapToUI();
    }
}
