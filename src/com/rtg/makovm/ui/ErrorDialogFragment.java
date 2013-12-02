package com.rtg.makovm.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.rtg.makovm.utils.BundleBuilder;

public class ErrorDialogFragment extends DialogFragment
{
	private final static String ARG_TITLE = "title";
	private final static String ARG_MESSAGE = "msg";

	public static DialogFragment createErrorDialogFragment(String title, String msg)
	{
		ErrorDialogFragment edf = new ErrorDialogFragment();
		edf.setArguments(new BundleBuilder().putString(ARG_TITLE, title).putString(ARG_MESSAGE, msg).get());
		return edf;
	}

	@Override
	public Dialog onCreateDialog(Bundle aSavedInstanceState)
	{
		return new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString(ARG_TITLE)).setMessage(getArguments().getString(ARG_MESSAGE)).setNegativeButton("OK", null).create();
	}
}
