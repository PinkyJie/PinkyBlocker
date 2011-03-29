package com.pinky.android.app;

public class SmsObj {
	
	private String smsNumuber;
	private String smsContent;
	private String smsTime;
	
	public SmsObj(){
		
	}
	
	public SmsObj(String smsNumuber, String smsContent, String smsTime) {
		super();
		this.smsNumuber = smsNumuber;
		this.smsContent = smsContent;
		this.smsTime = smsTime;
	}
	public String getSmsNumuber() {
		return smsNumuber;
	}
	public void setSmsNumuber(String smsNumuber) {
		this.smsNumuber = smsNumuber;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getSmsTime() {
		return smsTime;
	}
	public void setSmsTime(String smsTime) {
		this.smsTime = smsTime;
	}
	
	
}
