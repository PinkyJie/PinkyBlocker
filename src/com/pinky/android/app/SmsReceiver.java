package com.pinky.android.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private static final int SMS_NOTIFICATION_ID = 111;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		SharedPreferences mySetting = context.getSharedPreferences(
				"com.pinky.android.app_preferences",
				Context.MODE_WORLD_READABLE);
		boolean smsState = mySetting.getBoolean("isSmsOn", false);
		if ((mySetting.getAll().size() > 0 && smsState)
				|| mySetting.getAll().size() == 0) {
			Bundle extra = intent.getExtras();
			Object[] pdus = (Object[]) extra.get("pdus");
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
			String msg_num = msg.getOriginatingAddress();
			String msg_content = msg.getMessageBody();
			long msg_time = msg.getTimestampMillis();

			DBHelper helper = new DBHelper(context);
			String[] types = context.getResources().getStringArray(
					R.array.sms_rule_type_sign);
			Cursor cur = helper.customSelectRule(new String[] { "rule_type",
					"rule_detail" }, new String[] { types[1], msg_num });
			boolean isBlock = false;

			if (cur.getCount() > 0) {
				abortBroadcast();
				helper.insertLog(new String[] { types[1], msg_num,
						String.valueOf(msg_time), msg_content });
				showNotification(mySetting, context, msg_num, msg_time);
			} else {
				cur = helper.customSelectRule(new String[] { "rule_type" },
						new String[] { types[0] });
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					String log = cur.getString(0);
					if (msg_num.startsWith(log)) {
						abortBroadcast();
						helper.insertLog(new String[] { types[0], msg_num,
								String.valueOf(msg_time), msg_content });
						showNotification(mySetting, context, msg_num, msg_time);
						isBlock = true;
						cur.close();
						break;
					}
				}
				if (!isBlock) {
					cur = helper.customSelectRule(new String[] { "rule_type" },
							new String[] { types[2] });
					for (cur.moveToFirst(); !cur.isAfterLast(); cur
							.moveToNext()) {
						String log = cur.getString(0);
						if (msg_content.contains(log)) {
							abortBroadcast();
							helper.insertLog(new String[] { types[2], msg_num,
									String.valueOf(msg_time), msg_content });
							showNotification(mySetting, context, msg_num,
									msg_time);
							cur.close();
							break;
						}
					}
				}
			}
		}

	}

	private void showNotification(SharedPreferences setting, Context context,
			String msg_num, long time) {
		// TODO Auto-generated method stub
		boolean alertState = false;
		boolean noticeState = false;
		int ringtone = 0;
		if (setting.getAll().size() > 0) {
			noticeState = setting.getBoolean("isNotificationOn", false);
			if (noticeState) {
				alertState = setting.getBoolean("isAlertOn", false);
				if (alertState) {
					ringtone = Notification.DEFAULT_SOUND;
				}
			}
		}

		if (setting.getAll().size() == 0 || noticeState) {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification nfc = new Notification();
			nfc.icon = R.drawable.sms;
			nfc.tickerText = "PinkyBlocker拦截了一条短信";
			nfc.when = time;
			nfc.flags |= Notification.FLAG_AUTO_CANCEL;
			if(ringtone != 0){
				nfc.defaults |= ringtone;
			}
			Intent goTo = new Intent(context, PinkyBlocker.class);
			goTo.putExtra("FROM_NOTIFICATION", true);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, goTo,
					0);
			nfc.setLatestEventInfo(context, "PinkyBlocker拦截提醒", "来自【" + msg_num
					+ "】的短信被拦截", pIntent);
			nm.notify(SMS_NOTIFICATION_ID, nfc);
		}

	}

}
