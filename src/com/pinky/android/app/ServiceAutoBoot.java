package com.pinky.android.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceAutoBoot extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent phone = new Intent(context,PhoneService.class);
		context.startService(phone);
	}

}
