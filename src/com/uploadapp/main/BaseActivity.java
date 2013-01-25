package com.uploadapp.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class BaseActivity extends Activity implements OnClickListener {
	private int layoutID;
	private Context gInstance = null;
	public Handler mHandler = null;

	public ProgressDialog baseProgress = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(layoutID);
		if (baseProgress == null) {
			baseProgress = new ProgressDialog(this);
			baseProgress.setMessage("Uploading...");
			baseProgress.setCancelable(false);
		}

	}

	protected void setLayoutId(Context context, final int layoutID) {
		this.layoutID = layoutID;
		this.gInstance = context;
	}

	@Override
	public void onClick(final View view) {
	}

	public void showMessage(String title, String msg, String btntext) {
		Builder builder = null;
		builder = new AlertDialog.Builder(gInstance);

		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton(btntext,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		Dialog dialog = builder.create();
		dialog.show();
	}
}