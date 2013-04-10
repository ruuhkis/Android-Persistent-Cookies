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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CookieSQLSource {

	private static final String TAG = "CookieSQLSOurce";
	private CookieSQLHelper helper;
	private SQLiteDatabase db;
	 
	public CookieSQLSource(Context context) {
		this.helper = new CookieSQLHelper(context);
	}
	
	public void open() {
		db = helper.getWritableDatabase();
	}
	
	public void close() {
		db.close();
		db = null;
	}
	
	/**
	 * 
	 * @return a list of all cookies stored in cookies table
	 */
	
	public List<SQLCookie> getCookies() {
		List<SQLCookie> cookies = new ArrayList<SQLCookie>();
		Cursor cursor = db.query(CookieSQLHelper.COOKIE_TABLE_NAME, null, null, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			cookies.add(cursorToCookie(cursor));
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return cookies;
	}
	
	/**
	 * Updates cookie if it already exists else adds new cookie tot the table
	 * @param cookie
	 */
	
	public void addCookie(Cookie cookie) {
		long cookieId = -1;
		
		Cursor cursor = db.query(CookieSQLHelper.COOKIE_TABLE_NAME, null, CookieSQLHelper.COLUMN_NAME + "=?", new String[]{cookie.getName()}, null, null, null);
		
		ContentValues cookieValues = new ContentValues();
		cookieValues.put(CookieSQLHelper.COLUMN_COMMENT, cookie.getComment());
		cookieValues.put(CookieSQLHelper.COLUMN_COMMENT_URL, cookie.getCommentURL());
		cookieValues.put(CookieSQLHelper.COLUMN_DOMAIN, cookie.getDomain());
		Date date = cookie.getExpiryDate();
		cookieValues.put(CookieSQLHelper.COLUMN_EXPIRY_DATE, date != null ? date.getTime() : -1);
		cookieValues.put(CookieSQLHelper.COLUMN_NAME, cookie.getName());
		cookieValues.put(CookieSQLHelper.COLUMN_PATH, cookie.getPath());

		cookieValues.put(CookieSQLHelper.COLUMN_PERSISTENT, cookie.isPersistent() ? 1 : 0);
		cookieValues.put(CookieSQLHelper.COLUMN_SECURE, cookie.isSecure() ? 1 : 0);
		cookieValues.put(CookieSQLHelper.COLUMN_VALUE, cookie.getValue());
		cookieValues.put(CookieSQLHelper.COLUMN_VERSION, cookie.getVersion());
		
		if(cursor.moveToFirst()) {
			cookieId = cursor.getLong(cursor.getColumnIndex(CookieSQLHelper.COLUMN_COOKIE_ID));
			db.update(CookieSQLHelper.COOKIE_TABLE_NAME, cookieValues, CookieSQLHelper.COLUMN_ID + "=?", new String[]{Long.toString(cookieId)});
			db.delete(CookieSQLHelper.PORT_TABLE_NAME, CookieSQLHelper.COLUMN_COOKIE_ID + "=?", new String[]{Long.toString(cookieId)});
		} else {
			cookieId = db.insert(CookieSQLHelper.COOKIE_TABLE_NAME, null, cookieValues);
		}
		
		if(cookieId != -1) {
			
			int[] ports = cookie.getPorts();
			if(ports != null) {
				for(int i = 0; i < ports.length; i++) {
					ContentValues portValues = new ContentValues();
					portValues.put(CookieSQLHelper.COLUMN_COOKIE_ID, cookieId);
					portValues.put(CookieSQLHelper.COLUMN_PORT, ports[i]);
					db.insert(CookieSQLHelper.PORT_TABLE_NAME, null, portValues);
				}
			}
		} else {
			Log.e(TAG, "id = -1");
		}
		cursor.close();
	}
	
	/**
	 * Creates SQLCookie from cursor that is assumed
	 * to be located at SQLCookie entry
	 * @param cursor
	 * @return
	 */

	private SQLCookie cursorToCookie(Cursor cursor) {
		SQLCookie cookie = new SQLCookie();
		cookie.setComment(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_COMMENT)));
		cookie.setCommentURL(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_COMMENT_URL)));
		cookie.setDomain(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_DOMAIN)));
		long expiryDate = cursor.getLong(cursor.getColumnIndex(CookieSQLHelper.COLUMN_EXPIRY_DATE));
		cookie.setExpiryDate(expiryDate != -1 ? new Date(expiryDate) : null);
		cookie.setName(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_NAME)));
		cookie.setPath(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_PATH)));
		cookie.setPersistent(cursor.getInt(cursor.getColumnIndex(CookieSQLHelper.COLUMN_PERSISTENT)) == 1);		
		cookie.setPorts(getPorts(cursor.getLong(cursor.getColumnIndex(CookieSQLHelper.COLUMN_ID))));
		cookie.setSecure(cursor.getInt(cursor.getColumnIndex(CookieSQLHelper.COLUMN_SECURE)) == 1);
		cookie.setValue(cursor.getString(cursor.getColumnIndex(CookieSQLHelper.COLUMN_VALUE)));
		cookie.setVersion(cursor.getInt(cursor.getColumnIndex(CookieSQLHelper.COLUMN_VERSION)));
		cookie.setId(cursor.getLong(cursor.getColumnIndex(CookieSQLHelper.COLUMN_ID)));
		return cookie;
	}

	private int[] getPorts(long cookieId) {
		List<Integer> ports = new ArrayList<Integer>();
		
		Cursor cursor = db.query(CookieSQLHelper.PORT_TABLE_NAME, null, CookieSQLHelper.COLUMN_COOKIE_ID + "=?", new String[]{Long.toString(cookieId)}, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			ports.add(cursor.getInt(cursor.getColumnIndex(CookieSQLHelper.COLUMN_PORT)));
			cursor.moveToNext();
		}
		
		cursor.close();
		
		int[] arrayPorts = new int[ports.size()];
		for(int i = 0; i < ports.size(); i++) {
			arrayPorts[i] = ports.get(i);
		}
		
		
		return arrayPorts;
	}

	public void clear() {
		db.delete(CookieSQLHelper.COOKIE_TABLE_NAME, null, null);
		db.delete(CookieSQLHelper.PORT_TABLE_NAME, null, null);
	}

	public void deleteCookie(SQLCookie cookie) {
		db.delete(CookieSQLHelper.COOKIE_TABLE_NAME, CookieSQLHelper.COLUMN_ID + "=?", new String[]{Long.toString(cookie.getId())});
	}
	
}
