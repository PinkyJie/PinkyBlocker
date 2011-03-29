package com.pinky.android.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IoUtil {

	public enum RuleType {
		PHO_BEGIN_WITH, PHO_SPECIFIC, SMS_BEGIN_WITH, SMS_SPECIFIC, SMS_CONTAIN;
	}

	public enum LogType {
		PHONE, SMS;
	}

	@SuppressWarnings("null")
	public static Map<RuleType, ArrayList<String>> readRule(File f)
			throws IOException {
		HashMap<RuleType, ArrayList<String>> ruleMap = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(f)));
		String line;
		ArrayList<String> phone_b = null;
		ArrayList<String> phone_s = null;
		ArrayList<String> sms_b = null;
		ArrayList<String> sms_c = null;
		ArrayList<String> sms_s = null;
		while ((line = br.readLine()) != null) {
			String[] line_array = line.split("\t");
			RuleType type = RuleType.valueOf(line_array[0]);
			switch (type) {
			case PHO_BEGIN_WITH:
				phone_b.add(line_array[1]);
				break;
			case PHO_SPECIFIC:
				phone_s.add(line_array[1]);
				break;
			case SMS_BEGIN_WITH:
				sms_b.add(line_array[1]);
				break;
			case SMS_CONTAIN:
				sms_c.add(line_array[1]);
				break;
			case SMS_SPECIFIC:
				sms_s.add(line_array[1]);
				break;
			}
		}
		ruleMap.put(RuleType.PHO_BEGIN_WITH, phone_b);
		ruleMap.put(RuleType.PHO_SPECIFIC, phone_s);
		ruleMap.put(RuleType.SMS_BEGIN_WITH, sms_b);
		ruleMap.put(RuleType.SMS_CONTAIN, sms_c);
		ruleMap.put(RuleType.SMS_SPECIFIC, sms_s);
		return ruleMap;
	}

	public static void writeRule(File f, RuleType type, String rule)
			throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(f)), true);
		String[] ruleArray;
		pw.print(type.toString());
		pw.print("\t");
		if (rule.contains(",")) {
			ruleArray = rule.split(",");
			for (int i = 0; i < ruleArray.length; i++) {
				pw.println(ruleArray[i]);
			}
		} else {
			pw.println(rule);
		}
		pw.close();
	}

	@SuppressWarnings("null")
	public static Map<LogType, ArrayList<Object>> readLog(File f)
			throws IOException {
		HashMap<LogType, ArrayList<Object>> logMap = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(f)));
		String line;
		ArrayList<Object> phone_list = null;
		ArrayList<Object> sms_list = null;
		while ((line = br.readLine()) != null) {
			String[] line_array = line.split("\t");
			RuleType type = RuleType.valueOf(line_array[0]);
			String[] myObj = line_array[1].split(",");
			switch (type) {
			case PHO_BEGIN_WITH:
				PhoneObj phone = new PhoneObj(myObj[0], myObj[1]);
				phone_list.add(phone);
				break;
			case PHO_SPECIFIC:
				SmsObj sms = new SmsObj(myObj[0], myObj[1], myObj[2]);
				sms_list.add(sms);
				break;
			}
		}
		logMap.put(LogType.PHONE, phone_list);
		logMap.put(LogType.SMS, sms_list);
		return logMap;
	}

	public static void writeLog(File f, LogType type, Object obj)
			throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(f)), true);
		pw.print(type.toString());
		pw.print("\t");
		if (obj instanceof PhoneObj) {
			PhoneObj phone = (PhoneObj) obj;
			pw.println(phone.getPhoneNumber() + "," + phone.getCallTime());
		} else if (obj instanceof SmsObj) {
			SmsObj sms = (SmsObj) obj;
			pw.println(sms.getSmsNumuber() + "," + sms.getSmsContent() + ","
					+ sms.getSmsTime());
		}
		pw.close();

	}
	
	public static void addRule(){
		
	}
	
	public static void removeRule(){
		
	}
	
	public static void removeLog(){
		
	}

}
