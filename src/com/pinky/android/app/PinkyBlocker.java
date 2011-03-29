package com.pinky.android.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;

public class PinkyBlocker extends TabActivity {

	TabHost th;
	ListView lv_rule;
	ListView lv_log;
	private static final String ruleFileName = "/sdcard/" + R.string.app_name
			+ "/rule.block";
	private static final String LogFileName = "/sdcard/" + R.string.app_name
			+ "/log.block";
	File ruleFile;
	File logFile;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		th = this.getTabHost();
		th.addTab(th
				.newTabSpec("rule")
				.setIndicator("拦截规则",
						getResources().getDrawable(R.drawable.rule))
				.setContent(new TabContentFactory() {

					List<ItemContent> items;

					{
						items = new ArrayList<ItemContent>();

						ItemContent ic = new ItemContent();
						ic.setImageUrl("" + R.drawable.phone);
						ic.setTitle_type("" + R.string.begin_with);
						ic.setTitle_num("1111");
						ic.setContent("");
						items.add(ic);

						ic = new ItemContent();
						ic.setImageUrl("" + R.drawable.sms);
						ic.setTitle_type("" + R.string.begin_with);
						ic.setTitle_num("2222");
						ic.setContent("");
						items.add(ic);

					}

					@Override
					public View createTabContent(String tag) {
						// TODO Auto-generated method stub
						ListView lv = new ListView(PinkyBlocker.this);
						lv.setAdapter(new BlockerAdapter(PinkyBlocker.this, 0,
								items));
						return lv;
					}
				}));

		th.addTab(th
				.newTabSpec("log")
				.setIndicator("拦截日志",
						getResources().getDrawable(R.drawable.log))
				.setContent(new TabContentFactory() {

					List<ItemContent> items;

					{
						items = new ArrayList<ItemContent>();

						ItemContent ic = new ItemContent();
						ic.setImageUrl("" + R.drawable.phone);
						ic.setTitle_type("" + R.string.begin_with);
						ic.setTitle_num("33333");
						ic.setContent("");
						items.add(ic);

						ic = new ItemContent();
						ic.setImageUrl("" + R.drawable.sms);
						ic.setTitle_type("" + R.string.begin_with);
						ic.setTitle_num("3333");
						ic.setContent("我发来sdk加法lsd将发送登陆；卡交电费炼金术打发打发阿斯顿发生大幅");
						items.add(ic);

					}

					@Override
					public View createTabContent(String tag) {
						// TODO Auto-generated method stub
						ListView lv = new ListView(PinkyBlocker.this);
						lv.setAdapter(new BlockerAdapter(PinkyBlocker.this, 0,
								items));
						return lv;
					}
				}));

		Intent i = new Intent(this, PhoneService.class);
		startService(i);

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
			break;
		case R.id.menu_add_phone:
			break;
		case R.id.menu_add_sms:
			break;
		case R.id.menu_exit:
			finish();
			break;
		case R.id.menu_setting:
			Toast.makeText(this, "设置功能未开放", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_view_log:
			th.setCurrentTabByTag("log");
			break;
		}
		return true;
	}

	public class BlockerAdapter extends ArrayAdapter<ItemContent> {

		private List<ItemContent> items;

		public BlockerAdapter(Context context, int textViewResourceId,
				List<ItemContent> items) {
			super(context, textViewResourceId, items);
			// TODO Auto-generated constructor stub
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
				TextView tv_type = (TextView) v.findViewById(R.id.TextView01);
				TextView tv_num = (TextView) v.findViewById(R.id.TextView02);
				TextView tv_content = (TextView) v
						.findViewById(R.id.TextView03);
				tv_type.setText(ic.getTitle_type());
				tv_num.setText(ic.getTitle_num());
				tv_content.setText(ic.getContent());
			}
			return v;
		}

	}
}