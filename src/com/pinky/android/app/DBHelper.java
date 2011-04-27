package com.pinky.android.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "PinkyBlocker.sqlite";
	private final static String[] TABLE_NAME = {"tRule","tLog"};
	private final static int DATABASE_VERSION = 1;

	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql1 = "CREATE TABLE " + TABLE_NAME[0] + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, rule_type VARCHAR NOT NULL, rule_detail VARCHAR NOT NULL)";
		String sql2 = "CREATE TABLE " + TABLE_NAME[1] + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, log_type VARCHAR NOT NULL, log_num VARCHAR NOT NULL, log_time VARCHAR NOT NULL, log_content VARCHAR)";
		db.execSQL(sql1);
		db.execSQL(sql2);	
		initDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		for(String c:TABLE_NAME){
			db.execSQL("DROP TABLE IF EXISTS" + c );
		}
		onCreate(db);
	}
	
	public Cursor selectAllRule(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.query(TABLE_NAME[0], null, null, null, null, null, null);
		
		return cur;
	}
	
	public Cursor selectAllLog(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.query(TABLE_NAME[1], null, null, null, null, null, null);
		
		return cur;
	}
	
	public long insertRule(String[] ruleArgs){
		SQLiteDatabase db = this.getWritableDatabase();
		return insertRule(db, ruleArgs);
	}
	
	public long insertRule(SQLiteDatabase db, String[] ruleArgs){
		String[] args = {"rule_type","rule_detail"};
		ContentValues cv = new ContentValues();
		for(int i=0;i<args.length;i++){
			cv.put(args[i], ruleArgs[i]);
		}
		long row = db.insert(TABLE_NAME[0], null, cv);
		return row;
	}
	
	public long insertLog(String[] logArgs){
		SQLiteDatabase db = this.getWritableDatabase();
		return insertLog(db, logArgs);
	}
	
	public long insertLog(SQLiteDatabase db,String[] logArgs){
		String[] args = {"log_type","log_num","log_time","log_content"};
		ContentValues cv = new ContentValues();
		for(int i=0;i<args.length;i++){
			cv.put(args[i], logArgs[i]);
		}
		long row = db.insert(TABLE_NAME[1], null, cv);
		
		return row;
	}
	
	
	public void deleteRule(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		String[] whereValue = {id};
		db.delete(TABLE_NAME[0], "_id = ?", whereValue);
		
	}
	
	public void deleteLog(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		String[] whereValue = {id};
		db.delete(TABLE_NAME[1], "_id = ?", whereValue);
		
	}
	
	public void clearTable(String table){
		SQLiteDatabase db = this.getWritableDatabase();
		if(table.equals("rule")){
			db.execSQL("DELETE FROM " + TABLE_NAME[0]);
		}else if(table.equals("log")){
			db.execSQL("DELETE FROM " + TABLE_NAME[1]);
		}
		
	}
	
	public Cursor customSelectRule(String[] fields, String[] values){
		SQLiteDatabase db = this.getReadableDatabase();
		String whereClause = "";
		for(int i=0;i<fields.length;i++){
			if(i == 0){
				whereClause += fields[i] + " = ?";
			}else{
				whereClause += " AND " + fields[i] + " = ?";
			}			
		}
		Cursor cur = db.query(TABLE_NAME[0], new String[]{"rule_detail"}, whereClause, values, null, null, null);		
		return cur;
	}
	
	private void initDatabase(SQLiteDatabase db) {
		// TODO Auto-generated method stub		
		
		insertRule(db, new String[]{"PHO_BEGIN_WITH","123"});
		insertRule(db, new String[]{"PHO_SPECIFIC","12435"});
		insertRule(db, new String[]{"SMS_BEGIN_WITH","321"});
		insertRule(db, new String[]{"SMS_SPECIFIC","35678"});
		insertRule(db, new String[]{"SMS_CONTAIN","pinky"});
		
		insertLog(db, new String[]{"PHONE","111111","1301483158",null});
		insertLog(db, new String[]{"SMS","222222","1301482358",
				"ÕâÊÇÊ¾Àý£¬ÇëÉ¾³ý£¡"});
		
		
	}


}
