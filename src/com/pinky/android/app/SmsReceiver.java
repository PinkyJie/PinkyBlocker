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
		Log.e("PinkyBlock", "�յ�����");
		Bundle extra = intent.getExtras();
		Object[] pdus = (Object[]) extra.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		for (int i = 0; i < pdus.length; i++) {
			msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			Toast.makeText(context, "��������: " + msgs[i].getOriginatingAddress(),
					Toast.LENGTH_SHORT).show();
			Toast.makeText(context, "����Ϊ: " + msgs[i].getMessageBody(),
					Toast.LENGTH_SHORT).show();
			SimpleDateFormat sdp = new SimpleDateFormat("MM-dd HH:mm");
			Toast.makeText(
					context,
					"����ʱ��Ϊ: "
							+ sdp.format(new Date(msgs[i].getTimestampMillis())),
					Toast.LENGTH_SHORT).show();
			abortBroadcast();
		}
	}
}
