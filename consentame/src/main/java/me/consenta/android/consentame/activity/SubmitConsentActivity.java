package me.consenta.android.consentame.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.model.UserConsentRequest;
import me.consenta.android.consentame.utils.Constants;

import static me.consenta.android.consentame.utils.Constants.a7f681dac288.c1a85f46bfa4;

public class SubmitConsentActivity extends AppCompatActivity {

    protected static String consentId;

    protected TextView console;
    protected ProgressBar loading;
    protected Button action;

    protected static List<UserChoice> choices;
    protected int ERR_COLOR, MSG_COLOR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        consentId = getIntent().getStringExtra(c1a85f46bfa4);

        console = findViewById(R.id.loading_console);
        console.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(
                        ClipData.newPlainText("consentaError", console.getText())
                );
                Toast.makeText(SubmitConsentActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        loading = findViewById(R.id.progressBar);
        action = findViewById(R.id.action_button);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        
        ERR_COLOR = getResources().getColor(R.color.error_red);
        MSG_COLOR = getResources().getColor(R.color.main_light);

        submit();
    }

    protected void submit() {
        if (choices != null) {
            new SubmitConsentTask(this, consentId, choices).execute();
        } else {
            // class not initialized
            if (Constants.DEV)
                throw new RuntimeException("You have to use method initClass(List<UserChoice>) before starting this activity.");
            else
                setConsoleText("SubmitConsentActivity: Internal app error (wrong initialization). Please contact the developer.", true);
        }
    }

    /**
     * Notify this activity that the submit async task has been finished correctly.
     *
     * @param userConsentId
     */
    void notifySuccess(String userConsentId) {
        ConsentDetailsActivity.getCurrent().notifySuccess(this, userConsentId);
    }

    public static Class<? extends SubmitConsentActivity> initClass(Collection<UserChoice> userChoicesList) {
        choices = new LinkedList<>(userChoicesList);
        return SubmitConsentActivity.class;
    }

    /**
     * Show a message on this activity's console
     *
     * @param text the message to be shown.
     *            If {@code null} or empty, the console will be {@link View#GONE GONE}.
     * @param error if true, the text will be printed in ERR_COLOR instead of the standard color.
     */
    void setConsoleText(String text, boolean error) {
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
     * Return a {@link UserConsentRequest} that can be parsed and sent to Consenta.me with a
     * {@link okhttp3.OkHttpClient OkHttpClient}.
     *
     * @param consentId the ID of the Consent to submit
     * @param choices the list of accepted purposes, mapped as {@link UserChoice}
     *
     * @return a {@link UserConsentRequest} (or a subclass) that can be used to submit the new
     * Consent and receive a User Consent ID
     */
    public UserConsentRequest getRequestWrapper(String consentId, List<UserChoice> choices) {
        return new UserConsentRequest(consentId, choices);
    }
}
