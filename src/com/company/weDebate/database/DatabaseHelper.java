package com.company.weDebate.database;

import com.company.weDebate.ILog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 描述：用于创建、打开、更新数据库文件
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	
	private static final String DB_CREAT = "CREATE TABLE " + WeDebateDatabase.FRAME_TABLE_NAME + " (" + WeDebateTableColumns._ID + " INTEGER PRIMARY KEY" + ");";
	
	DatabaseHelper(Context context) {
		super(context, WeDebateDatabase.DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREAT);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ILog.LogI(DatabaseHelper.class, "Upgrading database from version " + oldVersion + " to " + newVersion + ".");
		
		db.execSQL("DROP TABLE IF EXISTS " + WeDebateDatabase.FRAME_TABLE_NAME);
		onCreate(db);
	}
}
