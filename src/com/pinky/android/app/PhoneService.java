package com.pinky.android.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneService extends Service {

	ITelephony iTele;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		TelephonyManager tpm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Method getTele;
		try {
			getTele = tpm.getClass().getDeclaredMethod("getITelephony",
					(Class[]) null);
			getTele.setAccessible(true);
			iTele = (ITelephony) getTele.invoke(tpm, (Object[]) null);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tpm.listen(new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				if (state == TelephonyManager.CALL_STATE_RINGING) {
					Log.e("PinkyBlocker", "ringing");
					try {
						iTele.endCall();
						iTele.cancelMissedCallsNotification();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(),
							"收到来电: " + incomingNumber, Toast.LENGTH_SHORT)
							.show();
					SimpleDateFormat sdp = new SimpleDateFormat("MM-dd HH:mm");
					Toast.makeText(getApplicationContext(),
							"来电时间为: " + sdp.format(new Date()),
							Toast.LENGTH_SHORT).show();

				}else if(state == TelephonyManager.CALL_STATE_IDLE) {
					Log.e("PinkyBlocker", "idle");
				}else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
					Log.e("PinkyBlocker", "offhook");
				}
				super.onCallStateChanged(state, incomingNumber);
			}

		}, PhoneStateListener.LISTEN_CALL_STATE);
		super.onStart(intent, startId);
	}

}
