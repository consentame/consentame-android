package me.consenta.android.consentame.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import me.consenta.android.consentame.R;
import me.consenta.android.consentame.model.UserChoice;
import me.consenta.android.consentame.utils.Constants;

public class SubmitConsentActivity extends AppCompatActivity {

    private static String consentId;

    TextView console;
    ProgressBar loading;
    Button action;

    private static List<UserChoice> choices;
    private int ERR_COLOR, MSG_COLOR;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        consentId = getIntent().getStringExtra("id");

        console = findViewById(R.id.loading_console);
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

    private void submit() {
        if (choices != null) {
            new SubmitConsentTask(this, consentId, choices).execute();
        } else {
            // class not initialized
            if (Constants.DEV)
                throw new RuntimeException("You have to use method initClass(List<UserChoice>) before starting this activity.");
            else
                setConsoleText("SubmitConsentActivity: Internal app error. Please contact the developer.", true);
        }
    }

    /**
     * Notify this activity that the submit async task has been finished correctly.
     *
     */
    void notifySuccess() {
        ConsentDetailsActivity.getCurrent().notifySuccess(this);
    }

    public static Class<SubmitConsentActivity> initClass(List<UserChoice> userChoicesList) {
        choices = userChoicesList;
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
}
