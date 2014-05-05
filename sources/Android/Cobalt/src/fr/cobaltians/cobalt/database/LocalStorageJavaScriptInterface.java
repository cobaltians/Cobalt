/**
 *
 * LocalStorageJavaScriptInterface
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.JavascriptInterface;
import junit.framework.Assert;

/**
 * Local storage substitution for Web views
 * @author Diane
 */
public class LocalStorageJavaScriptInterface {

    private static final String TAG = LocalStorageJavaScriptInterface.class.getSimpleName();

    private final Context mContext;
    private final LocalStorage mLocalStorage;

    public LocalStorageJavaScriptInterface(Context context) {
        Assert.assertNotNull(TAG + " - getInstance: context could not be null", context);
        mContext = context;
        mLocalStorage = LocalStorage.getInstance(mContext);
    }

    /**
     * Gets item for the given key
     * @param key: key to look for
     * @return item corresponding to the given key
     */
    @JavascriptInterface
    public String getItem(String key) {
        String value = null;

        if (key != null) {
            SQLiteDatabase database = mLocalStorage.getReadableDatabase();
            Cursor cursor = database.query( LocalStorage.LOCALSTORAGE_TABLE_NAME,
                                            null,
                                            LocalStorage.LOCALSTORAGE_ID + " = ?",  new String [] {key},
                                            null, null, null);
            if (cursor.moveToFirst()) {
                value = cursor.getString(1);
            }
            cursor.close();
            database.close();
        }

        return value;
    }

    /**
     * Sets value for the given key.
     * @param key
     * @param value
     */
    @JavascriptInterface
    public void setItem(String key, String value) {
        if (key != null
            && value != null) {
            SQLiteDatabase database = mLocalStorage.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LocalStorage.LOCALSTORAGE_ID, key);
            values.put(LocalStorage.LOCALSTORAGE_VALUE, value);

            if (getItem(key) != null) {
                database.update(	LocalStorage.LOCALSTORAGE_TABLE_NAME,
                                    values,
                                    LocalStorage.LOCALSTORAGE_ID + " = " + key,
                                    null);
            }
            else {
                database.insert(	LocalStorage.LOCALSTORAGE_TABLE_NAME, null,
                                    values);
            }
            database.close();
        }
    }

    /**
     * Removes item corresponding to the given key
     * @param key
     */
    @JavascriptInterface
    public void removeItem(String key) {
        if (key != null) {
            SQLiteDatabase database = mLocalStorage.getWritableDatabase();
            database.delete(	LocalStorage.LOCALSTORAGE_TABLE_NAME,
                                LocalStorage.LOCALSTORAGE_ID + " = " + key,
                                null);
            database.close();
        }
    }

    /**
     * Clears local storage.
     */
    @JavascriptInterface
    public void clear() {
        SQLiteDatabase database = mLocalStorage.getWritableDatabase();
        database.delete(LocalStorage.LOCALSTORAGE_TABLE_NAME, null, null);
        database.close();
    }
}