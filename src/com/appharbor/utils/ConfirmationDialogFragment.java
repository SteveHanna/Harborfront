package com.appharbor.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmationDialogFragment extends DialogFragment {

	public static ConfirmationDialogFragment newInstance(String title, String message) {
		ConfirmationDialogFragment frag = new ConfirmationDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
		.setTitle(getArguments().getString("title"))
		.setMessage(getArguments().getString("message"))
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				((IConfirmationDialogHandler)getActivity()).doPositiveClick();
			}
		}).setNegativeButton("No", null)
		.create();
	}
}
