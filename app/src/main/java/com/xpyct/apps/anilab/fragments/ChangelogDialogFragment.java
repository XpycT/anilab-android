package com.xpyct.apps.anilab.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.xpyct.apps.anilab.R;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;

/**
 * anilab-android
 * Created by XpycT on 14.07.2015.
 */
public class ChangelogDialogFragment extends DialogFragment {

    public ChangelogDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogRecyclerView chgList = (ChangeLogRecyclerView) layoutInflater.inflate(R.layout.changelog_fragment_dialog, null);

        return new MaterialDialog.Builder(getActivity())
                .title(R.string.changelog)
                .theme(Theme.DARK)
                .customView(chgList, false)
                .positiveText(R.string.close)
                .show();

    }

}