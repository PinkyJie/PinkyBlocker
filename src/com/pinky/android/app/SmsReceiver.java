package com.pinky.android.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("PinkyBlock", "收到短信");
		Bundle extra = intent.getExtras();
		Object[] pdus = (Object[]) extra.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		for (int i = 0; i < pdus.length; i++) {
			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			Toast.makeText(context, "短信来自: " + msgs[i].getOriginatingAddress(),
					Toast.LENGTH_SHORT).show();
			Toast.makeText(context, "内容为: " + msgs[i].getMessageBody(),
					Toast.LENGTH_SHORT).show();
			SimpleDateFormat sdp = new SimpleDateFormat("MM-dd HH:mm");
			Toast.makeText(
					context,
					"发送时间为: "
							+ sdp.format(new Date(msgs[i].getTimestampMillis())),
					Toast.LENGTH_SHORT).show();
			abortBroadcast();
		}
	}
}
