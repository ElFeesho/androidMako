package com.rtg.makovm.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.rtg.makovm.utils.BundleBuilder;

public class ProgressDialogFragment extends DialogFragment
{
	private final static String ARG_TITLE = "title";
	private final static String ARG_MSG = "msg";

	public static DialogFragment newProgressDialog(String title, String msg)
	{
		DialogFragment result = new ProgressDialogFragment();
		result.setArguments(new BundleBuilder().putString(ARG_TITLE, title).putString(ARG_MSG, msg).get());
		return result;
	}

	@Override
	public Dialog onCreateDialog(Bundle aSavedInstanceState)
	{
		ProgressDialog pdlg = new ProgressDialog(getActivity());
		pdlg.setTitle(getArguments().getString(ARG_TITLE, null));
		pdlg.setMessage(getArguments().getString(ARG_MSG, null));
		return pdlg;
	}
}
