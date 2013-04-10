/*******************************************************************************
 * Copyright (c) 2013 Pasi Matalamäki
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.ruuhkis.cookies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CookieSQLHelper extends SQLiteOpenHelper {

	private static final String COOKIE_DB = "cookies.db";
	private static final int COOKIE_DB_VERSION = 1;

	public static final String COOKIE_TABLE_NAME = "cookies";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMMENT = "commnets";
	public static final String COLUMN_COMMENT_URL = "comments_url";
	public static final String COLUMN_DOMAIN = "domain";
	public static final String COLUMN_EXPIRY_DATE = "expiry_data";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PATH = "path";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_VERSION = "version";
	public static final String COLUMN_PERSISTENT = "persistent";
	public static final String COLUMN_SECURE = "secure";
	
	public static final String CREATE_COOKIE_TABLE = "CREATE TABLE " + COOKIE_TABLE_NAME + 
			"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			COLUMN_COMMENT + " TEXT, " +
			COLUMN_COMMENT_URL + " TEXT, " +
			COLUMN_DOMAIN + " TEXT, " +
			COLUMN_EXPIRY_DATE + " INTEGER, " +
			COLUMN_NAME + " TEXT, " +
			COLUMN_PATH + " TEXT, " +
			COLUMN_VALUE + " TEXT, " +
			COLUMN_VERSION + " INTEGER, " +
			COLUMN_PERSISTENT + " INTEGER, " +
			COLUMN_SECURE + " INTEGER" +
			");";
	
	public static final String PORT_TABLE_NAME = "ports";

	public static final String COLUMN_COOKIE_ID = "cookie_id";
	public static final String COLUMN_PORT = "port";
	

	
	public static final String CREATE_PORT_TABLE = "CREATE TABLE " + PORT_TABLE_NAME + 
			"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			COLUMN_COOKIE_ID + " INTEGER, " +
			COLUMN_PORT + " INTEGER" +
			");";
	
	
	public CookieSQLHelper(Context context) {
		super(context, COOKIE_DB, null, COOKIE_DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_COOKIE_TABLE);
		db.execSQL(CREATE_PORT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
