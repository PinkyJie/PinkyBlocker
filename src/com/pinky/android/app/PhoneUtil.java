package com.pinky.android.app;

import java.lang.reflect.Method;

import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class PhoneUtil {
	
	static public ITelephony getITelephony(TelephonyManager telMgr) throws Exception {  
        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony");  
        getITelephonyMethod.setAccessible(true);
        return (ITelephony)getITelephonyMethod.invoke(telMgr);  
    }  

}
