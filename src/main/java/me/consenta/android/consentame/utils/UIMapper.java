package me.consenta.android.consentame.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import me.consenta.android.consentame.R;
import me.consenta.android.consentame.activity.ConsentDetailsActivity;
import me.consenta.android.consentame.model.DataController;
import me.consenta.android.consentame.model.Purpose;
import me.consenta.android.consentame.model.TermsAndConditions;

public class UIMapper {

    private static List<Pair<TextView, TextView>> addresses = new LinkedList<>();
    private static int CONSENTA_GREEN = 0;

    /**
     * Add a {@link View} at the bottom of a {@link ViewGroup}
     * @param v the {@link View} to be added
     * @param list the container {@link ViewGroup}
     */
    public static void append(final View v, final ViewGroup list) {
        list.addView(v, list.getChildCount());
    }

    /**
     * Map a {@link List} of Consent element to the proper layout and
     * appends it to the provided {@link ViewGroup container} in the UI
     * @param sourceList a List of Consent elements, such as {@link DataController}
     * @param container the {@link ViewGroup} the new UI elements will be added to
     */
    public static void map(final List sourceList, final ViewGroup container) {
        if (CONSENTA_GREEN == 0)
            CONSENTA_GREEN = container.getResources().getColor(R.color.consenta_green);
        Object o = sourceList.get(0);

        if (o instanceof DataController) {
            DCMapList(sourceList, container, true);
            return;
        }

        if (o instanceof Purpose) {
            PMapList(sourceList, container);
            return;
        }

        throw new IllegalArgumentException("'" + o.getClass().getCanonicalName() + "' can not be handled by UIMapper.map().");
    }

    /**
     * Map an instance of {@link TermsAndConditions} to the proper layout and
     * appends it to the provided {@link ViewGroup container}
     * @param source the {@link TermsAndConditions} element to be added to the UI
     * @param container the {@link ViewGroup} the new UI element will be added to
     */
    public static void map(final TermsAndConditions source, final ViewGroup container) {
        int layoutId = (source.isMandatory() ? R.layout.mandatory_terms_and_conditions_box : R.layout.terms_and_conditions_box);

        TextView link = container.findViewById(R.id.tec_complete_link);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openURL = new Intent(Intent.ACTION_VIEW, Uri.parse(source.getUrl()));
                v.getContext().startActivity(openURL);
            }
        });

        LinearLayout tecContainer = container.findViewById(R.id.tec_container);
        RelativeLayout tecBox = (RelativeLayout) tecContainer.inflate(container.getContext(), layoutId, null);
        TextView desc = tecBox.findViewById(R.id.tec_desc);
        desc.setText(source.getTitle());

        ConsentDetailsActivity.addChoice(tecBox, TermsAndConditions.ID, source.isMandatory());
        append(tecBox, container);
    }

    /**
     * Map a {@link List} of {@link DataController DataControllers} to the proper layout and
     * appends it to the provided {@link ViewGroup container}
     * @param sourceList the {@link List} of source {@link DataController} objects that need to be printed on the UI
     * @param container the {@link ViewGroup} that will hold the Data Controllers' data
     */
    private static void DCMapList(final List<DataController> sourceList, final ViewGroup container, boolean dataProcessor) {
        final Context context = container.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        for (final DataController source : sourceList) {
            // map values from the consent to a new data_admin_box, then inject it in the container
            final RelativeLayout dpBox = (RelativeLayout) map(source, inflater.inflate(R.layout.data_controller_box, null), dataProcessor);

            final String name = source.getName();
            final String email = source.getEmail();
            final String addr = source.getAddress();
            dpBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NestedScrollView dpInfoDialog = (NestedScrollView) inflater.inflate(R.layout.data_processor_dialog_box, null);

                    RelativeLayout dpInfo = dpInfoDialog.findViewById(R.id.alert_container);
                    ((TextView) dpInfoDialog.findViewById(R.id.dpdialog_name)).setText(source.getName());
                    ((TextView) dpInfoDialog.findViewById(R.id.dpdialog_address)).setText(source.getAddress());
                    ((TextView) dpInfoDialog.findViewById(R.id.dpdialog_email)).setText(source.getEmail());

                    // show a dialog with Data Processor info when clicked
                    final AlertDialog d = new AlertDialog.Builder(context)
                            .setTitle("Data Processor")
                            .setView(dpInfoDialog)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // close dialog
                                    dialog.cancel();
                                }
                            })
                            .create();
                    d.show();
                    d.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(CONSENTA_GREEN);
                }
            });
            append(dpBox, container);
        }
    }

    /**
     * Map a {@link List} of {@link Purpose Purposes} to the proper layout and
     * appends it to the provided {@link ViewGroup container}
     * @param sourceList the {@link List} of source {@link Purpose} objects that need to be printed on the UI
     * @param container the {@link ViewGroup} that will hold the Purposes' data
     */
    private static void PMapList(final List<Purpose> sourceList, final ViewGroup container) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (Purpose source : sourceList) {
            // choose the correct layout
            int layoutId = (source.isMandatory() ? R.layout.mandatory_purpose_box : R.layout.purpose_box);
            // map values from the consent to a new data_admin_box, then inject it to the bootom of the layout
            RelativeLayout dpBox = (RelativeLayout) map(source, inflater.inflate(layoutId, null));
            append(dpBox, container);
        }
    }

    private static View map(final Purpose source, final View view) {
        // short DESCRIPTION
        TextView sDesc = view.findViewById(R.id.p_short_desc);
        sDesc.setText(source.getDescription());

        // USER DATA DATASET
        TextView userData = view.findViewById(R.id.p_user_data);
        userData.setText(source.getDataset());

        // short DATA CONTROLLERS
        TextView dataCtrl = view.findViewById(R.id.p_data_controllers_short);
        StringBuilder dataControllers = new StringBuilder();
        for (DataController dc : source.getDataControllers()) {
            if(dataControllers.length() > 0)
                dataControllers.append(", ");
            dataControllers.append(dc.getName());
        }
        dataCtrl.setText(dataControllers.toString());

        // set values inside hidden dropdown
        Context context =  view.getContext();
        TextView dropDownToggle = view.findViewById(R.id.p_dropdown_toggle);
        LinearLayout dropDown = view.findViewById(R.id.p_dropdown);
        // short DESCRIPTION (dropdown title)
        TextView sDesc2 = dropDown.findViewById(R.id.p_short_desc_2);
        sDesc2.setText(source.getDescription());
        // long DESCRIPTION (dropdown text)
        TextView lDesc = dropDown.findViewById(R.id.p_long_desc);
        lDesc.setText(source.getLongDescription());
        lDesc.setLinkTextColor(CONSENTA_GREEN);

        dropDown.setVisibility(View.GONE);
        // show/hide dropdown
        dropDownToggle.setOnClickListener(
                new ToggleVisibilityListener(
                        dropDown,
                        context.getString(R.string.text_when_dropdown_toggle_open),
                        context.getString(R.string.text_when_dropdown_toggle_closed)
                )
        );

        ConsentDetailsActivity.addChoice(view, source.getId(), source.isMandatory());

        // long DATA CONTROLLERS
        LinearLayout dcContainer = dropDown.findViewById(R.id.p_data_controllers_full);
        map(source.getDataControllers(), dcContainer);

        view.invalidate();
        return view;
    }

    /**
     * Map the data of a {@link DataController} to a {@link View} that will be added to the UI by
     * {@link #map(DataController, View)}
     * @param source the {@link DataController} object to be mapped
     * @param view the {@link View} that will show the data on the UI
     * @return the View (for further elaboration)
     */
    private static View map(final DataController source, final View view, boolean dataProcessor) {
        // NAME
        ((TextView)view.findViewById(R.id.dp_name)).setText(source.getName() + ", ");

        // ADDRESS
        final TextView addressText = view.findViewById(R.id.dp_address);
        addressText.setText(source.getAddress());

        // EMAIL
        String emailText = source.getEmail();

        ((TextView)view.findViewById(R.id.dp_email)).setText(emailText);
        ((TextView)view.findViewById(R.id.dp_email)).setLinkTextColor(CONSENTA_GREEN);

        view.invalidate();
        return view;
    }
}
