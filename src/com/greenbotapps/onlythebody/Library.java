package com.greenbotapps.onlythebody;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Library {

	public static boolean isOnline(Activity act) {
		ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public static void ShowMessage(Activity act, int Title, int Msg, int Ok) {
		AlertDialog ad = new AlertDialog.Builder(act).create();
		ad.setTitle(Title);
		String message = act.getString(Msg);
		ad.setMessage(message.trim());
		ad.getWindow().getAttributes().windowAnimations = R.style.views_animation;
		ad.setButton(DialogInterface.BUTTON_POSITIVE, act.getString(Ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});

		ad.show();
	}
	
	public static void ShowMessageStr(Activity act, int Title, String Msg, int Ok) {
		AlertDialog ad = new AlertDialog.Builder(act).create();
		ad.setTitle(Title);
		ad.setMessage(Msg.trim());
		ad.getWindow().getAttributes().windowAnimations = R.style.views_animation;
		ad.setButton(DialogInterface.BUTTON_POSITIVE, act.getString(Ok), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});

		ad.show();
	}
}
