package com.company.weDebate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 描述：数据库增删改查
 */
public class WeDebateDatabase {
	public static final String DATABASE_NAME = "weDebate";
	public static final String FRAME_TABLE_NAME = "weDabate_table";

	private Context context;
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mSQLiteDatabase;

	public WeDebateDatabase(Context context) {
		this.context = context;
		mDatabaseHelper = new DatabaseHelper(context);
	}

	public void open() {
		mDatabaseHelper = new DatabaseHelper(context);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public void close() {
		if (mSQLiteDatabase != null) {
			mSQLiteDatabase.close();
		}
		if (mDatabaseHelper != null) {
			mDatabaseHelper.close();
		}
	}

	public void beginTransaction() {
		mDatabaseHelper.getWritableDatabase().beginTransaction();
	}

	public void setTransactionSuccessful() {
		mDatabaseHelper.getWritableDatabase().setTransactionSuccessful();
	}

	public void endTransaction() {
		mDatabaseHelper.getWritableDatabase().endTransaction();
	}
}
