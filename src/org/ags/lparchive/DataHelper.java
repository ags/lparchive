package org.ags.lparchive;

import org.ags.lparchive.model.Chapter;
import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {
	private static final String DATABASE_NAME = "lparchive.db";
	private static final int DATABASE_VERSION = 1;
	
	// tables
	public static final String ARCHIVE_TABLE = "archive";
	public static final String TAGS_TABLE = "tags";
	public static final String CHAPTERS_TABLE = "chapters";
	public static final String LATEST_TABLE = "latest";
	
	// keys
	public static String KEY_ID = "_id";
	public static String KEY_AUTHOR = "author";
	public static String KEY_GAME = "game";
	public static String KEY_URL = "url";
	public static String KEY_TYPE = "type";
	public static String KEY_LATEST_ID = "lp_id";
	public static String KEY_TAG_ID = "lp_id";
	public static String KEY_TAG = "tag";
	public static String KEY_CHAPTER_LP_ID = "lp_id";
	public static String KEY_CHAPTER_URL = "url";
	public static String KEY_CHAPTER_TITLE = "title";
	
	public static String SORT_GAME_ASC = "game asc";
//	private static String SORT_URL_ASC = "url asc";
	
	public static final String[] projectArchive = new String[] { KEY_ID, 
		KEY_GAME, KEY_AUTHOR, KEY_URL, KEY_TYPE };
	private static final String[] projectChapter = new String[] { KEY_ID, 
		KEY_CHAPTER_LP_ID, KEY_CHAPTER_URL, KEY_CHAPTER_TITLE };
	
	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertLpStmnt;
	private static final String INSERT_LP = String.format("insert into %s"
			+ "(%s, %s, %s, %s) values (?, ?, ?, ?)", ARCHIVE_TABLE,
			KEY_GAME, KEY_AUTHOR, KEY_URL, KEY_TYPE);

	private SQLiteStatement insertTagStmnt;
	private static final String INSERT_TAG = String.format("insert into %s"
			+ "(%s, %s) values (?, ?)", TAGS_TABLE, KEY_TAG_ID, KEY_TAG);
	
	private SQLiteStatement insertChapterStmnt;
	private static final String INSERT_CHAPTER = String.format("insert into %s"
			+ "(%s, %s, %s) values (?, ?, ?)", CHAPTERS_TABLE, KEY_CHAPTER_LP_ID,
			KEY_CHAPTER_URL, KEY_CHAPTER_TITLE);
	
	private SQLiteStatement latestLpStmnt;
	private static final String LATEST_LP = String.format(
			"insert into %s (%s) values (?)", LATEST_TABLE, KEY_LATEST_ID);
	
//	private SQLiteQueryBuilder recent_join;
	private static final String RECENT_JOIN = "SELECT archive._id, archive.game, " +
			"archive.author, archive.url, archive.type FROM archive, latest " +
			"WHERE (archive._id = latest.lp_id) ORDER BY game asc";
	
	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertLpStmnt = this.db.compileStatement(INSERT_LP);
		this.latestLpStmnt = this.db.compileStatement(LATEST_LP);
		this.insertTagStmnt = this.db.compileStatement(INSERT_TAG);
		this.insertChapterStmnt = this.db.compileStatement(INSERT_CHAPTER);
	}

	public SQLiteDatabase getDb() {
		return this.db;
	}
	
	public long insertLetsPlay(String game, String author, String url, String type) {
		this.insertLpStmnt.bindString(1, game);
		this.insertLpStmnt.bindString(2, author);
		this.insertLpStmnt.bindString(3, url);
		this.insertLpStmnt.bindString(4, type);
		return this.insertLpStmnt.executeInsert();
	}
		
	public void markRecentLetsPlay(String game, String author) {
		Cursor cursor = this.db.query(ARCHIVE_TABLE, new String[] { KEY_ID },
				"game=? AND author=?", new String[] { game, author }, 
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				this.latestLpStmnt.bindLong(1, cursor.getInt(0));
				this.latestLpStmnt.executeInsert();
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void addTag(long lp_id, String tag) {
		this.insertTagStmnt.bindLong(1, lp_id);
		this.insertTagStmnt.bindString(2, tag);
		this.insertTagStmnt.executeInsert();
	}
	
	public Cursor getRecentLetsPlay() {
		return this.db.rawQuery(RECENT_JOIN, null);
	}

	public void deleteAll() {
		this.db.delete(ARCHIVE_TABLE, null, null);
		this.db.delete(TAGS_TABLE, null, null);
		this.db.delete(CHAPTERS_TABLE, null, null);
	}

	public Cursor getArchive() {
		return this.db.query(ARCHIVE_TABLE, projectArchive, null,
				null, null, null, SORT_GAME_ASC);
	}
	
	public Cursor getChapters(long lpId) {
		return this.db.query(CHAPTERS_TABLE, projectChapter, "lp_id=?",
				new String[] { String.valueOf(lpId) }, null, null, null);
	}

	public long insertChapter(long lpId, String url, String title) {
		this.insertChapterStmnt.bindLong(1, lpId);
		this.insertChapterStmnt.bindString(2, url);
		this.insertChapterStmnt.bindString(3, title);
		return this.insertChapterStmnt.executeInsert();
	}

	public LetsPlay getLP(long id) {
		Cursor cursor = this.db.query(ARCHIVE_TABLE, projectArchive, "_id=?", 
				new String[] { String.valueOf(id)}, null, null, null);
		LetsPlay lp = null;
		if (cursor.moveToFirst()) {
			do {
				lp = new LetsPlay(cursor.getInt(0), cursor
						.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return lp;
	}

	public Chapter getChapter(long id) {
		Cursor cursor = this.db.query(CHAPTERS_TABLE, projectChapter, "_id=?", 
				new String[] { String.valueOf(id)}, null, null, null);
		Chapter c = null;
		if (cursor.moveToFirst()) {
			do {
				c = new Chapter(cursor.getString(2), cursor.getString(3));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return c;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		/* create db schema */
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + ARCHIVE_TABLE
					+ " (_id INTEGER PRIMARY KEY, game TEXT, " +
							"author TEXT, url TEXT, type TEXT)");
			db.execSQL("CREATE TABLE " + TAGS_TABLE
					+ " (_id INTEGER PRIMARY KEY, lp_id INTEGER, tag TEXT)");
			db.execSQL("CREATE TABLE " + CHAPTERS_TABLE
					+ " (_id INTEGER PRIMARY KEY, lp_id INTEGER, url TEXT, title TEXT)");
			db.execSQL("CREATE TABLE " + LATEST_TABLE
					+ " (_id INTEGER PRIMARY KEY, lp_id INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("LPA",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + ARCHIVE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TAGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + CHAPTERS_TABLE);
			onCreate(db);
		}
	}

}
