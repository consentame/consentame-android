package me.consenta.android.consentame.model;

import android.support.v7.widget.SwitchCompat;

import java.util.List;

/**
 * This class represents a user's choice about a {@link Purpose} of a Privacy Policy
 *
 * @author Andrea Arighi andrea@chino.io
 */
public class UserChoice {

    private SwitchCompat choice;
    private int purposeId;
    private boolean mandatory;

    /**
     * Create a new {@link UserChoice}
     *
     * @param choice the {@link SwitchCompat} that is used to express the user's choice
     * @param id the id of the {@link Purpose} which the user is asked to choose about
     * @param mandatoryChoice if {@code true}, the user must accept this Purpose
     */
    public UserChoice(SwitchCompat choice, int id, boolean mandatoryChoice) {
        this.choice = choice;
        this.purposeId = id;
        this.mandatory = mandatoryChoice;
    }

    /**
     * Append this choice's ID to the list if the User selected the relative {@link SwitchCompat switch}
     *
     * @param idList a list of {@link Purpose} IDs
     */
    public void ifSelectedAppendTo(List<Integer> idList) {
        if (choice.isChecked()) {
            idList.add(new Integer(purposeId));
        }
    }

    /**
     * A {@link UserChoice} is not acceptable if <b>it is mandatory AND it has NOT been checked</b>.
     * @return {@code false} if this {@link UserChoice} is mandatory but has not been checked
     */
    public boolean isAcceptable() {
        // The acceptable condition can be written like this:
        return choice.isChecked() || !mandatory;
    }

    /**
     * @return the {@link SwitchCompat switch} that represents this {@link UserChoice}
     */
    public SwitchCompat getSwitch() {
        return choice;
    }

    public int getId() {
        return purposeId;
    }
}
