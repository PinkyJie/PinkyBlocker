package com.pinky.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class AddPhoneRule extends Activity {

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
		setContentView(R.layout.add_phone_rule);
		setTitle("PinkyBlocker -- ��ӵ绰���ع���");
		
		Spinner sp = (Spinner)findViewById(R.id.PhoneRuleType);
		et = (EditText)findViewById(R.id.PhoneRuleDetail);
		final LinearLayout ll= (LinearLayout)findViewById(R.id.PhoneSelect);
		Button okBtn = (Button)findViewById(R.id.PhoneRuleOK);
		Button cancleBtn = (Button)findViewById(R.id.PhoneRuleCancel);
		
		Button conBtn = (Button)findViewById(R.id.PhoneFromContact);
		Button logBtn = (Button)findViewById(R.id.PhoneFromLog);
		
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			String[] types = getResources().getStringArray(R.array.phone_rule_type);
			String[] type_des = getResources().getStringArray(R.array.phone_rule_type_sign);
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub			
				type_text = types[position];
				type = type_des[position];
				et.setText("");
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
					Toast.makeText(AddPhoneRule.this, 
							"������Ϊ��", Toast.LENGTH_LONG).show();
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
				Toast.makeText(AddPhoneRule.this, 
						"��Ӳ���ȡ����������������", Toast.LENGTH_SHORT).show();
				AddPhoneRule.this.finish();
				returnHome();
			}
		});
		
		conBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AddPhoneRule.this,SelectPhone.class);
				i.putExtra("SelectSource", SELECT_FROM_CONTACT);
				AddPhoneRule.this.startActivityForResult(i, SELECT_FROM_CONTACT);
			}
		});
		
		logBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AddPhoneRule.this,SelectPhone.class);
				i.putExtra("SelectSource", SELECT_FROM_CALLLOG);
				AddPhoneRule.this.startActivityForResult(i, SELECT_FROM_CALLLOG);
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
			.setTitle("ȷ����ӣ�")
			.setMessage("��Ҫ������¹���\n\n���� "+msg)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DBHelper helper = new DBHelper(AddPhoneRule.this);
					long res = helper.insertRule(
							new String[]{rule_type,rule_detail});
					if(res != -1){
						Toast.makeText(getApplicationContext(), 
								"������ӳɹ�", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						AddPhoneRule.this.finish();
						returnHome();
					}else{
						Toast.makeText(getApplicationContext(), 
								"������ӳ������������", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
					
				}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), 
							"��Ӳ���ȡ��", Toast.LENGTH_SHORT).show();
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
					"��Ӳ���ȡ����������������", Toast.LENGTH_SHORT).show();
			returnHome();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
