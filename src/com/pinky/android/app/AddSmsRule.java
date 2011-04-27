package com.pinky.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class AddSmsRule extends Activity {

	String type;
	String type_text;
	String detail;
	static final int SELECT_FROM_CONTACT = 1;
	static final int SELECT_FROM_CALLLOG = 2;
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_sms_rule);
		setTitle("PinkyBlocker -- 添加短信拦截规则");
		
		Spinner sp = (Spinner)findViewById(R.id.SmsRuleType);
		et = (EditText)findViewById(R.id.SmsRuleDetail);
		Button okBtn = (Button)findViewById(R.id.SmsRuleOK);
		Button cancleBtn = (Button)findViewById(R.id.SmsRuleCancel);
		
		final LinearLayout ll= (LinearLayout)findViewById(R.id.SmsSelect);
		Button conBtn = (Button)findViewById(R.id.SmsFromContact);
		Button logBtn = (Button)findViewById(R.id.SmsFromLog);
		
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			String[] types = getResources().getStringArray(R.array.sms_rule_type);
			String[] type_des = getResources().getStringArray(R.array.sms_rule_type_sign);
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub			
				type_text = types[position];
				type = type_des[position];
				et.setText("");
				if(position == 2){
					et.setInputType(InputType.TYPE_CLASS_TEXT);
				}
				else{
					et.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				if(position == 1){
					ll.setVisibility(View.VISIBLE);
				}else{
					ll.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				type_text = types[0];
				type = type_des[0];
			}
		});
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et.getText().length() == 0){
					Toast.makeText(AddSmsRule.this, 
							"规则不能为空", Toast.LENGTH_LONG).show();
				}else{
					detail = et.getText().toString();
					showAddDialog(type,type_text,detail);
				}
			}
		});
		
		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(AddSmsRule.this, 
						"添加操作取消，将返回主界面", Toast.LENGTH_SHORT).show();
				AddSmsRule.this.finish();
				returnHome();
			}
		});
		
		conBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AddSmsRule.this,SelectPhone.class);
				i.putExtra("SelectSource", SELECT_FROM_CONTACT);
				AddSmsRule.this.startActivityForResult(i, SELECT_FROM_CONTACT);
			}
		});
		
		logBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AddSmsRule.this,SelectPhone.class);
				i.putExtra("SelectSource", SELECT_FROM_CALLLOG);
				AddSmsRule.this.startActivityForResult(i, SELECT_FROM_CALLLOG);
			}
		});
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		String[] info;
		if(requestCode == SELECT_FROM_CALLLOG || 
				requestCode == SELECT_FROM_CONTACT){
			if(resultCode == RESULT_OK){
				info = data.getStringArrayExtra("PhoneInfo");
				et.setText(info[1]);
			}
		}
		
	}
	
	protected void showAddDialog(final String rule_type, 
			String rule_text, final String rule_detail) {
		// TODO Auto-generated method stub
		String msg;
		if(rule_text.contains("...")){
			msg = rule_text.replace("...", " ["+rule_detail+"] ");
		}else{
			msg = rule_text+" ["+rule_detail+"] ";
		}
		AlertDialog dia = new AlertDialog.Builder(this)
			.setCancelable(false)
			.setTitle("确定添加？")
			.setMessage("将要添加以下规则：\n\n拦截 "+msg)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DBHelper helper = new DBHelper(AddSmsRule.this);
					long res = helper.insertRule(
							new String[]{rule_type,rule_detail});
					if(res != -1){
						Toast.makeText(getApplicationContext(), 
								"规则添加成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						AddSmsRule.this.finish();
						returnHome();
					}else{
						Toast.makeText(getApplicationContext(), 
								"规则添加出错，请重新添加", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
					
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), 
							"添加操作取消", Toast.LENGTH_SHORT).show();
					dialog.cancel();
				}
			}).create();
		dia.show();
	}


	private void returnHome(){
		Intent home = new Intent(this,PinkyBlocker.class);
		startActivity(home);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Toast.makeText(getApplicationContext(), 
					"添加操作取消，将返回主界面", Toast.LENGTH_SHORT).show();
			returnHome();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
