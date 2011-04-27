package com.pinky.android.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Contacts;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPhone extends Activity {

	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_phone_num);
		lv = (ListView)findViewById(R.id.PhoneNumList);
		int source = getIntent().getExtras().getInt("SelectSource");
		switch (source) {
		case AddPhoneRule.SELECT_FROM_CONTACT:
			setTitle("从通讯录中选取号码");
			new GetContactTask().execute();
			break;
		case AddPhoneRule.SELECT_FROM_CALLLOG:
			setTitle("从通话记录中选取号码");
			new GetCallLogTask().execute();
			break;
		}
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				TextView name = (TextView)view.findViewById(R.id.Name);
				TextView num = (TextView)view.findViewById(R.id.Num);
				Intent i = new Intent();
				i.putExtra("PhoneInfo", new String[]{
						name.getText().toString(),num.getText().toString()});
				setResult(RESULT_OK, i);
				SelectPhone.this.finish();				
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Toast.makeText(getApplicationContext(), 
					"取消选择", Toast.LENGTH_SHORT).show();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class SelectPhoneAdapter extends BaseAdapter {

		private List<PhoneItem> items;

		public SelectPhoneAdapter(List<PhoneItem> items) {
			super();
			this.items = items;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public PhoneItem getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(SelectPhone.this).inflate(
						R.layout.phone_num_item, null);
			}
			TextView tv1 = (TextView) v.findViewById(R.id.Name);
			TextView tv2 = (TextView) v.findViewById(R.id.Num);

			PhoneItem pi = items.get(position);
			tv1.setText(pi.getName());
			tv2.setText(pi.getNum());
			return v;
		}
	}
	
	private class GetContactTask extends AsyncTask<Void, Void, ArrayList<PhoneItem>>{

		
		ProgressDialog dia;
		
		@Override
		protected ArrayList<PhoneItem> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ContentResolver cr = getContentResolver();
			Cursor cur = cr.query(Contacts.People.CONTENT_URI, 
					new String[]{Contacts.People.DISPLAY_NAME, Contacts.People._ID}, null, null, 
					Contacts.People.DEFAULT_SORT_ORDER);
			ArrayList<PhoneItem> al = new ArrayList<PhoneItem>();
			for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
				String name = cur.getString(0);
				int person_id = cur.getInt(1);
				Cursor c1 = cr.query(Contacts.Phones.CONTENT_URI, 
						null, 
						Contacts.Phones.PERSON_ID+" = ?", new String[]{String.valueOf(person_id)}, null);
				for(c1.moveToFirst();!c1.isAfterLast();c1.moveToNext()){
					String num = c1.getString(7);
					num = num.replace("-", "");
					al.add(new PhoneItem(name, num));
				}
				c1.close();
			}
			cur.close();
			return al;
		}
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dia = ProgressDialog.show(SelectPhone.this,
					"", "正在加载，请稍候...", true);
		}


		@Override
		protected void onPostExecute(ArrayList<PhoneItem> result) {
			// TODO Auto-generated method stub
			lv.setAdapter(new SelectPhoneAdapter(result));
			dia.dismiss();
		}
		
	}
	
	private class GetCallLogTask extends AsyncTask<Void, Void, ArrayList<PhoneItem>>{

		ProgressDialog dia;
		@Override
		protected ArrayList<PhoneItem> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ContentResolver cr = getContentResolver();
			Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, 
							new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER}, 
							null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
			ArrayList<PhoneItem> al = new ArrayList<PhoneItem>();
			HashSet<String> numArr = new HashSet<String>();
			for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
				String name = cur.getString(0)==null?"未知":cur.getString(0);
				String num = cur.getString(1);
				if(numArr.add(num)){
					al.add(new PhoneItem(name,num));
				}
			}
			cur.close();
			return al;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dia = ProgressDialog.show(SelectPhone.this, 
					"", "正在加载，请稍候...", true);
		}

		@Override
		protected void onPostExecute(ArrayList<PhoneItem> result) {
			// TODO Auto-generated method stub
			lv.setAdapter(new SelectPhoneAdapter(result));
			dia.dismiss();
		}
		
	}

}
