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

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.content.Context;

public class PersistentCookieStore implements CookieStore {
	
	private CookieSQLSource source;
	
	public PersistentCookieStore(Context context) {
		this.source = new CookieSQLSource(context);
		source.open();
	}
	
	@Override
	public void addCookie(Cookie cookie) {
		source.addCookie(cookie);
	}

	@Override
	public void clear() {
		source.clear();
	}

	@Override
	public boolean clearExpired(Date date) {
		boolean anyExpired = false;
		List<SQLCookie> cookies = source.getCookies();
		for(SQLCookie cookie: cookies) {
			if(cookie.isExpired(date)) {
				source.deleteCookie(cookie);
				anyExpired = true;
			}
		}
		return anyExpired;
	}

	@Override
	public List<Cookie> getCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		
		for(SQLCookie cookie: source.getCookies()) {
			cookies.add(cookie);
		}
		
		return cookies;
	}
	
	public void close() {
		source.close();
	}

}
