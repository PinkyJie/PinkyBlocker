package com.pinky.android.app;

public class PhoneObj {
	
	private String phoneNumber;
	private String callTime;
	
	public PhoneObj(){
		
	}
	
	public PhoneObj(String phoneNumber, String callTime){
		this.phoneNumber = phoneNumber;
		this.callTime = callTime;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getCallTime() {
		return callTime;
	}
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}
	
	

}
