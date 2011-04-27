package com.pinky.android.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneService extends Service {

	private static final int PHONE_NOTIFICATION_ID = 222;

	// control call-in state
	private TelephonyManager tpm;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		tpm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		final Context myContext = getApplicationContext();
		final String[] myTypes = myContext.getResources().getStringArray(
				R.array.phone_rule_type_sign);
		final DBHelper myHelper = new DBHelper(myContext);
		tpm.listen(new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				try {
					if (state == TelephonyManager.CALL_STATE_RINGING
							&& PhoneUtil.getITelephony(tpm).isRinging()) {
						String flag = isBlockCall(myContext, myHelper, myTypes,
								incomingNumber);
						if (flag.length() > 0) {
							blockCall();
							myHelper.insertLog(new String[] { flag, incomingNumber,
									String.valueOf(System.currentTimeMillis()),
									null });
							showNotification(myContext, incomingNumber,
									System.currentTimeMillis());
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	protected String isBlockCall(Context context, DBHelper helper,
			String[] types, String call_num) {
		// TODO Auto-generated method stub
		SharedPreferences mySetting = getSharedPreferences(
				"com.pinky.android.app_preferences",
				Context.MODE_WORLD_READABLE);
		boolean phoneState = mySetting.getBoolean("isPhoneOn", false);
		if ((mySetting.getAll().size() > 0 && phoneState)
				|| mySetting.getAll().size() == 0) {
			Cursor cur = helper.customSelectRule(new String[] { "rule_type",
					"rule_detail" }, new String[] { types[1], call_num });
			if (cur.getCount() > 0) {
				cur.close();
				return types[1];
			} else {
				cur = helper.customSelectRule(new String[] { "rule_type" },
						new String[] { types[0] });
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					String log = cur.getString(0);
					if (call_num.startsWith(log)) {
						cur.close();
						return types[0];
					}
				}
				cur.close();
			}
		}		
		return "";
	}

	private void showNotification(Context context, String call_num, long time) {
		// TODO Auto-generated method stub
		SharedPreferences setting = context.getSharedPreferences(
				"com.pinky.android.app_preferences",
				Context.MODE_WORLD_READABLE);
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
			nfc.icon = R.drawable.phone;
			nfc.tickerText = "PinkyBlocker拦截了一通电话";
			nfc.when = time;
			nfc.flags |= Notification.FLAG_AUTO_CANCEL;
			if (ringtone != 0) {
				nfc.defaults |= ringtone;
			}
			Intent goTo = new Intent(context, PinkyBlocker.class);
			goTo.putExtra("FROM_NOTIFICATION", true);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, goTo,
					0);
			nfc.setLatestEventInfo(context, "PinkyBlocker拦截提醒", "来自【"
					+ call_num + "】的电话被拦截", pIntent);
			nm.notify(PHONE_NOTIFICATION_ID, nfc);
		}

	}

	private void blockCall() {
		// TODO Auto-generated method stub
		try {
			PhoneUtil.getITelephony(tpm).endCall();
			PhoneUtil.getITelephony(tpm).cancelMissedCallsNotification();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
