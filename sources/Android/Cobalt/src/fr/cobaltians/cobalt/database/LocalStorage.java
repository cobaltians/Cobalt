package fr.cobaltians.cobalt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * {@link SQLiteOpenHelper} that is used as replacement of the localStorage of the webviews.
 * @details this class should not be used. Everything about the localStorage through the application is already handled in HTMLFragment.
 * @author Diane
 */
public class LocalStorage extends SQLiteOpenHelper {

	protected final static boolean sDebug = false;
	
	private static LocalStorage sInstance;
	
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

	/**
	 * Returns the instance of LocalStorage
	 * @param context: context used to create the database
	 * @return the instance of LocalStorage of the application.
	 */
	public static LocalStorage getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new LocalStorage(context.getApplicationContext());
		}
		
		return sInstance;
	}

	private LocalStorage(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (sDebug) Log.w(getClass().getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", All data will be lost.");
		
		db.execSQL("DROP TABLE IF EXISTS " + LOCALSTORAGE_TABLE_NAME);
		onCreate(db);
	}
}
