/**
 *
 * LocalStorage
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

import fr.cobaltians.cobalt.Cobalt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import junit.framework.Assert;

/**
 * {@link SQLiteOpenHelper} that is used as replacement of the localStorage of the webviews.
 * @details this class should not be used. Everything about the localStorage through the application is already handled in CobaltFragment.
 * @author Diane
 */
public class LocalStorage extends SQLiteOpenHelper {

    // TAG
    private static final String TAG = LocalStorage.class.getSimpleName();

	/**
	 * Name of LocalStorage table
	 */
	public static final String LOCALSTORAGE_TABLE_NAME = "local_storage_table";
	/**
	 * ID column of LocalStorage table
	 */
	public static final String LOCALSTORAGE_ID = "_id";
	/**
	 * Value column of LocalStorage table
	 */
	public static final String LOCALSTORAGE_VALUE = "value";
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "local_storage.db";
	private static final String CREATE_TABLE = 	"CREATE TABLE " + LOCALSTORAGE_TABLE_NAME + " (" 
													+ LOCALSTORAGE_ID + " TEXT PRIMARY KEY, "
													+ LOCALSTORAGE_VALUE + " TEXT NOT NULL" 
												+ ");";

    /*************************************
     * MEMBERS
     *************************************/

    private static LocalStorage sInstance;

    /*********************************************************
     * CONSTRUCTORS
     *********************************************************/

    private LocalStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	/**
	 * Returns the instance of LocalStorage
	 * @param context: context used to create the database
	 * @return the instance of LocalStorage of the application.
	 */
	public static LocalStorage getInstance(Context context) {
        if (sInstance == null) {
            Assert.assertNotNull(TAG + " - getInstance: context could not be null", context);
            sInstance = new LocalStorage(context.getApplicationContext());
        }

        return sInstance;
    }

    /*****************************************************************
     * LIFECYCLE
     *****************************************************************/

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (Cobalt.DEBUG) Log.w(Cobalt.TAG, TAG + " - onUpgrade: upgrading database from version " + oldVersion + " to " + newVersion + ", All data will be lost.");
		
		db.execSQL("DROP TABLE IF EXISTS " + LOCALSTORAGE_TABLE_NAME);
		onCreate(db);
	}
}
