package com.pinky.android.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class PinkyBlocker extends TabActivity {

	TabHost mHost;
	
	// database setting
	DBHelper mHelper = new DBHelper(this);
	
	// preference
	BlockerSetting mSetting;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupView();

	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		updateRuleView();
		updateLogView();
				
		//manageServiceByPreference();
		startPhoneService();
			
		Bundle b = getIntent().getExtras();
		if(b != null && b.getBoolean("FROM_NOTIFICATION")){
			mHost.setCurrentTabByTag("log");
		}
		super.onResume();
	}
	
	private void startPhoneService() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, PhoneService.class);
		startService(i);
	}

	private void updateRuleView() {
		// TODO Auto-generated method stub
		ListView lv = (ListView) findViewById(R.id.RuleList);
		ArrayList<ItemContent> items = new ArrayList<ItemContent>();
		Cursor cur = mHelper.selectAllRule();
		for(cur.moveToLast();!cur.isBeforeFirst();cur.moveToPrevious()){
			ItemContent ic = new ItemContent();
			ic.setId(cur.getInt(0));
			String type = cur.getString(1);
			if(type.startsWith("P")){
				ic.setImageUrl(""+R.drawable.phone);
			}else if(type.startsWith("S")){
				ic.setImageUrl(""+R.drawable.sms);
			}
			if(type.contains("BEGIN_WITH")){
				ic.setTitle1(getResources().getString(R.string.begin_with));
			}else if(type.contains("SPECIFIC")){
				ic.setTitle1(getResources().getString(R.string.specific));
			}else if(type.contains("CONTAIN")){
				ic.setTitle1(getResources().getString(R.string.contain));
			}
			ic.setTitle2(cur.getString(2));
			items.add(ic);
		}
		cur.close();
		lv.setAdapter(new BlockerAdapter(items));
		
	}
	
	private void updateLogView() {
		// TODO Auto-generated method stub
		ListView lv = (ListView) findViewById(R.id.LogList);
		ArrayList<ItemContent> items = new ArrayList<ItemContent>();
		Cursor cur = mHelper.selectAllLog();
		for(cur.moveToLast();!cur.isBeforeFirst();cur.moveToPrevious()){
			ItemContent ic = new ItemContent();
			ic.setId(cur.getInt(0));
			String type = cur.getString(1);
			if(type.startsWith("P")){
				ic.setImageUrl(""+R.drawable.phone);
			}else if(type.startsWith("S")){
				ic.setImageUrl(""+R.drawable.sms);
			}
			ic.setTitle1(cur.getString(2));
			ic.setTitle2(cur.getString(3));
			ic.setContent(cur.getString(4));
			items.add(ic);
		}
		cur.close();
		lv.setAdapter(new BlockerAdapter(items));
	}

	private void setupView() {
		mHost = this.getTabHost();
		mHost.addTab(mHost
				.newTabSpec("rule")
				.setIndicator("拦截规则",
						getResources().getDrawable(R.drawable.rule))
				.setContent(R.id.LinearLayout01));

		mHost.addTab(mHost
				.newTabSpec("log")
				.setIndicator("拦截日志",
						getResources().getDrawable(R.drawable.log))
				.setContent(R.id.LinearLayout02));
		
		ListView lvRule = (ListView)findViewById(R.id.RuleList);
		ListView lvLog = (ListView)findViewById(R.id.LogList);
		registerForContextMenu(lvRule);
		registerForContextMenu(lvLog);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.cmenu_add_phone:
			goAddPhone();
			break;
		case R.id.cmenu_add_sms:
			goAddSms();			
			break;
		case R.id.menu_del_rule:
			deleteSingle("rule",info);
			break;
		case R.id.menu_del_all_rule:
			deleteAll("rule");
			break;
		case R.id.menu_del_log:
			deleteSingle("log",info);
			break;
		case R.id.menu_del_all_log:
			deleteAll("log");
			break;
		}
		return super.onContextItemSelected(item);
	}


	private void deleteSingle(final String str, AdapterContextMenuInfo info) {
		// TODO Auto-generated method stub
		final String item_id = ((TextView)info.targetView
				.findViewById(R.id.ItemId)).getText().toString();
		String tip = null;
		String msg = null;
		if(str.equals("log")){
			tip = "日志记录";
			String content = ((TextView)info.targetView.findViewById(R.id.TextView03)).getText().toString();
			String num = ((TextView)info.targetView.findViewById(R.id.TextView01)).getText().toString();
			if(content.length() == 0){
				msg = "来自【"+ num +"】的"+"电话";
			}else{
				msg = "来自【"+ num +"】的"+"短信";
			}
		}else if(str.equals("rule")){
			tip = "拦截规则";
			msg = ""+((TextView)info.targetView.findViewById(R.id.TextView01)).getText().toString()
					+"\n"+((TextView)info.targetView.findViewById(R.id.TextView02)).getText().toString();
		}
		
		AlertDialog dia = new AlertDialog.Builder(this)
		.setTitle("确定删除？")
		.setMessage("确定要删除本条"+tip+"吗？\n\n"+msg+"\n\n警告：本操作不可逆！")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setCancelable(false)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(str.equals("log")){
					mHelper.deleteLog(item_id);
					updateLogView();
				}else if(str.equals("rule")){
					mHelper.deleteRule(item_id);
					updateRuleView();
				}
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		}).create();
		dia.show();
	}

	private void deleteAll(final String str) {
		// TODO Auto-generated method stub
		String tip = null;
		if(str.equals("log")){
			tip = "日志记录";
		}else if(str.equals("rule")){
			tip = "拦截规则";
		}
		
		AlertDialog dia = new AlertDialog.Builder(this)
		.setTitle("确定删除？")
		.setMessage("确定要删除所有"+tip+"吗？\n警告：本操作不可逆！")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setCancelable(false)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mHelper.clearTable(str);
				if(str.equals("log")){
					updateLogView();
				}else if(str.equals("rule")){
					updateRuleView();
				}
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		}).create();
		dia.show();
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		if(v.getId() == R.id.RuleList){
			if(((ListView)v).getChildCount() > 0){
				inflater.inflate(R.menu.rule_menu, menu);
			}
		}else if(v.getId() == R.id.LogList){
			if(((ListView)v).getChildCount() > 0){
				inflater.inflate(R.menu.log_menu, menu);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_about:
			showAboutDialog();
			break;
		case R.id.menu_add_phone:
			goAddPhone();
			break;
		case R.id.menu_add_sms:
			goAddSms();
			break;
		case R.id.menu_exit:
			doExit();
			break;
		case R.id.menu_setting:
			goSetting();
			break;
		}
		return true;
	}

	private void doExit() {
		// TODO Auto-generated method stub
		AlertDialog dia = new AlertDialog.Builder(this)
			.setTitle("退出")
			.setMessage("确定要退出PinkyBlocker？\n\n"+
					"退出后PinkyBlocker将在后台为您拦截，也在可设置中彻底关闭拦截服务。")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					PinkyBlocker.this.finish();
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			}).create();
		dia.show();
	}

	private void goAddSms() {
		// TODO Auto-generated method stub
		Intent add_sms = new Intent(this,AddSmsRule.class);
		startActivity(add_sms);
	}

	private void goAddPhone() {
		// TODO Auto-generated method stub
		Intent add_phone = new Intent(this,AddPhoneRule.class);
		startActivity(add_phone);
	}

	private void goSetting() {
		// TODO Auto-generated method stub
		Intent setting = new Intent(this,BlockerSetting.class);
		startActivity(setting);
	}

	private void showAboutDialog() {
		// TODO Auto-generated method stub
		AlertDialog about_dialog = new AlertDialog.Builder(this)
		.setTitle("PinkyBlocker v1.0")
		.setIcon(R.drawable.icon)
		.setCancelable(false)
		.setMessage(R.string.app_about)
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).create();
		about_dialog.show();
	}

	public class BlockerAdapter extends BaseAdapter {

		private List<ItemContent> items;

		public BlockerAdapter(List<ItemContent> items) {
			super();
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			if (v == null) {
				v = LayoutInflater.from(PinkyBlocker.this).inflate(
						R.layout.list_item, null);
			}

			ItemContent ic = items.get(position);
			if (ic != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.Image01);
				iv.setImageDrawable(PinkyBlocker.this.getResources()
						.getDrawable(Integer.parseInt(ic.getImageUrl())));
				TextView tv1 = (TextView) v.findViewById(R.id.TextView01);
				TextView tv2 = (TextView) v.findViewById(R.id.TextView02);
				TextView tv3 = (TextView) v
						.findViewById(R.id.TextView03);
				TextView tv4 = (TextView) v
				.findViewById(R.id.ItemId);
				if(parent.getId() == R.id.RuleList){
					tv1.setText(Html
							.fromHtml("<font color=\"#6699FF\">拦截类型: </font>"
									+ ic.getTitle1()));
					tv2.setText(Html
							.fromHtml("<font color=\"#6699FF\">规则详情: </font>"
									+ ic.getTitle2()));
					if (ic.getContent() == null) {
						tv3.setVisibility(View.GONE);
					} else {
						tv3.setText(Html
								.fromHtml("<font color=\"#6699FF\">内容: </font>"
										+ ic.getContent()));
					}
				}else if(parent.getId() == R.id.LogList){
					tv1.setText(Html
							.fromHtml("<font color=\"#6699FF\">号码: </font>"
									+ ic.getTitle1()));
					tv2.setText(Html
							.fromHtml("<font color=\"#6699FF\">时间: </font>"
									+ formatTime(ic.getTitle2())));
					if (ic.getContent() == null) {
						tv3.setVisibility(View.GONE);
					} else {
						tv3.setText(Html
								.fromHtml("<font color=\"#6699FF\">内容: </font>"
										+ ic.getContent()));
					}
				}
				tv4.setText(String.valueOf(ic.getId()));
			}
			return v;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public ItemContent getItem(int postion) {
			// TODO Auto-generated method stub
			return items.get(postion);
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}		

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			doExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private String formatTime(String time_str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		Date d = new Date(Long.parseLong(time_str));
		return sdf.format(d);
	}
}