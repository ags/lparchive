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
	public static final String TABLE_ARCHIVE = "archive";
	public static final String TABLE_TAGS = "tags";
	public static final String TABLE_CHAPTERS = "chapters";
	public static final String TABLE_LATEST = "latest";
	public static final String TABLE_FAVORITES = "favorites";
	
	// keys
	public static final String KEY_ID = "_id";
	
	public static final String KEY_ARCHIVE_AUTHOR = "author";
	public static final String KEY_ARCHIVE_GAME = "game";
	public static final String KEY_ARCHIVE_URL = "url";
	public static final String KEY_ARCHIVE_TYPE = "type";
	
	public static final String KEY_LATEST_LP_ID = "lpId";
	
	public static final String KEY_TAG_ID = "lpId";
	public static final String KEY_TAG = "tag";
	
	public static final String KEY_CHAPTER_LP_ID = "lpId";
	public static final String KEY_CHAPTER_URL = "url";
	public static final String KEY_CHAPTER_TITLE = "title";
	
	public static final String KEY_FAVS_LP_ID = "lpId";
	
	// sorting
	public static final String SORT_GAME_ASC = String.format("%s asc", 
			KEY_ARCHIVE_GAME);
	
	// schema
	private static final String ARCHIVE_TABLE_SCHEMA = String.format(
					"CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, " +
					"%s TEXT, %s TEXT, %s TEXT)", TABLE_ARCHIVE, KEY_ID, 
					KEY_ARCHIVE_GAME, KEY_ARCHIVE_AUTHOR, KEY_ARCHIVE_URL,
					KEY_ARCHIVE_TYPE);
	
	private static final String[] projectArchive = new String[] { 
		KEY_ID, 
		KEY_ARCHIVE_GAME, 
		KEY_ARCHIVE_AUTHOR, 
		KEY_ARCHIVE_URL, 
		KEY_ARCHIVE_TYPE 
	};
	
	private static final String[] projectChapter = new String[] { 
		KEY_ID, 
		KEY_CHAPTER_LP_ID, 
		KEY_CHAPTER_URL, 
		KEY_CHAPTER_TITLE 
	};
	
	private static final String[] projectFavorite = new String[] {
		KEY_ID, 
		KEY_FAVS_LP_ID 
	};
	
	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertLpStmnt;
	private static final String INSERT_LP = String.format(
			"insert into %s (%s, %s, %s, %s) values (?, ?, ?, ?)", 
			TABLE_ARCHIVE, KEY_ARCHIVE_GAME, KEY_ARCHIVE_AUTHOR, 
			KEY_ARCHIVE_URL, KEY_ARCHIVE_TYPE);

	private SQLiteStatement insertTagStmnt;
	private static final String INSERT_TAG = String.format(
			"insert into %s (%s, %s) values (?, ?)", 
			TABLE_TAGS, KEY_TAG_ID, KEY_TAG);
	
	private SQLiteStatement insertChapterStmnt;
	private static final String INSERT_CHAPTER = String.format(
			"insert into %s (%s, %s, %s) values (?, ?, ?)", 
			TABLE_CHAPTERS, KEY_CHAPTER_LP_ID, KEY_CHAPTER_URL, 
			KEY_CHAPTER_TITLE);
	
	private SQLiteStatement insertFavStmnt;
	private static final String INSERT_FAV = String.format(
			"insert into %s (%s) values (?)", TABLE_FAVORITES, KEY_FAVS_LP_ID);
	
	private SQLiteStatement deleteFavStmnt;
	private static final String DELETE_FAV = String.format(
			"delete from %s WHERE %s = ?", TABLE_FAVORITES, KEY_FAVS_LP_ID);
	
	private SQLiteStatement latestLpStmnt;
	private static final String INSERT_LATEST = String.format(
			"insert into %s (%s) values (?)", TABLE_LATEST, KEY_LATEST_LP_ID);
	
	private static final String ARCHIVE_PROJECT = String.format(
			"%s.%s, %s.%s, %s.%s, %s.%s, %s.%s",
			TABLE_ARCHIVE, KEY_ID, 
			TABLE_ARCHIVE, KEY_ARCHIVE_GAME, 
			TABLE_ARCHIVE, KEY_ARCHIVE_AUTHOR,
			TABLE_ARCHIVE, KEY_ARCHIVE_URL,
			TABLE_ARCHIVE, KEY_ARCHIVE_TYPE);
	
	// TODO use constants in these joins
	private static final String RECENT_JOIN = String.format(
			"SELECT %s FROM %s, %s WHERE (%s.%s = %s.%s) ORDER BY %s",
			ARCHIVE_PROJECT, TABLE_ARCHIVE, TABLE_LATEST, TABLE_ARCHIVE, 
			KEY_ID, TABLE_LATEST, KEY_LATEST_LP_ID, SORT_GAME_ASC);
	
	private static final String RECENT_JOIN_FILTERED = "SELECT archive._id, " +
			"archive.game, archive.author, archive.url, archive.type FROM " +
			"archive, latest WHERE (archive._id = latest.lpId) AND " +
			"archive.game LIKE ? ORDER BY game asc";
	
	private static final String TAG_SEARCH = "SELECT archive._id, archive.game," +
			" archive.author, archive.url, archive.type, tags.tag" +
			" FROM archive, tags WHERE archive._id = tags.lpId AND" +
			" tags.tag = ? GROUP BY archive._id ORDER BY game asc"; 
	
	private static final String FAV_JOIN = "SELECT archive._id, archive.game, " +
	"archive.author, archive.url, archive.type FROM archive, favorites " +
	"WHERE (archive._id = favorites.lpId) ORDER BY game asc";
	
	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		db = openHelper.getWritableDatabase();
		insertLpStmnt = db.compileStatement(INSERT_LP);
		latestLpStmnt = db.compileStatement(INSERT_LATEST);
		insertTagStmnt = db.compileStatement(INSERT_TAG);
		insertChapterStmnt = db.compileStatement(INSERT_CHAPTER);
		insertFavStmnt = db.compileStatement(INSERT_FAV);
		deleteFavStmnt = db.compileStatement(DELETE_FAV);
	}

	public SQLiteDatabase getDb() {
		return this.db;
	}
	
	public long insertLetsPlay(String game, String author, String url, String type) {
		insertLpStmnt.bindString(1, game);
		insertLpStmnt.bindString(2, author);
		insertLpStmnt.bindString(3, url);
		// TODO could save space using a long instead of string
		insertLpStmnt.bindString(4, type);
		return insertLpStmnt.executeInsert();
	}
		
	public void markRecentLetsPlay(String game, String author) {
		Cursor cursor = this.db.query(TABLE_ARCHIVE, new String[] { KEY_ID },
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
		insertTagStmnt.bindLong(1, lp_id);
		insertTagStmnt.bindString(2, tag);
		insertTagStmnt.executeInsert();
	}
	
	public Cursor getLatestLPs() {
		// join doesn't appear to work unless using raw query
		return this.db.rawQuery(RECENT_JOIN, null);
	}

	public void deleteAll() {
		db.delete(TABLE_ARCHIVE, null, null);
		db.delete(TABLE_TAGS, null, null);
		db.delete(TABLE_CHAPTERS, null, null);
	}

	public Cursor getArchive() {
		return db.query(TABLE_ARCHIVE, projectArchive, null,
				null, null, null, SORT_GAME_ASC);
	}
	
	public Cursor getChapters(long lpId) {
		return this.db.query(TABLE_CHAPTERS, projectChapter, "lpId=?",
				new String[] { String.valueOf(lpId) }, null, null, null);
	}

	public long insertChapter(long lpId, String url, String title) {
		insertChapterStmnt.bindLong(1, lpId);
		insertChapterStmnt.bindString(2, url);
		insertChapterStmnt.bindString(3, title);
		return insertChapterStmnt.executeInsert();
	}

	public LetsPlay getLP(long id) {
		Cursor cursor = db.query(TABLE_ARCHIVE, projectArchive, "_id=?", 
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
		Cursor cursor = db.query(TABLE_CHAPTERS, projectChapter, "_id=?", 
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

	public Cursor lpNameSearch(String name) {
        String[] args = new String[] { "%" + name + "%"};
        return db.query(DataHelper.TABLE_ARCHIVE, DataHelper.projectArchive,
        		KEY_ARCHIVE_GAME + " LIKE ?", args, null, null, 
        				SORT_GAME_ASC);
	}
	
	public Cursor lpLatestNameSearch(String name) {
        String[] args = new String[] { "%" + name + "%"};
		return db.rawQuery(RECENT_JOIN_FILTERED, args);
	}
	
	public Cursor tagSearch(String tag) {
		String[] args = new String[] { tag };
		return db.rawQuery(TAG_SEARCH, args);	
	}
	
	public long toggleFavoriteLP(long id) {
		if(isFavoriteLP(id)) {
			deleteFavStmnt.bindLong(1, id);
			// executeUpdateDelete is API level 11, targeting 8
			deleteFavStmnt.execute();
			return 0;
		} else {
			insertFavStmnt.bindLong(1, id);
			return insertFavStmnt.executeInsert();
		}
	}
	
	public boolean isFavoriteLP(long id) {
		Cursor cursor = this.db.query(TABLE_FAVORITES, projectFavorite, 
				KEY_FAVS_LP_ID+"=?", new String[] { String.valueOf(id)}, 
				null, null, null);
		return cursor.moveToFirst();
	}
	
	public Cursor getFavoriteLPs() {
		return this.db.rawQuery(FAV_JOIN, null);
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		/* create db schema */
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(ARCHIVE_TABLE_SCHEMA);
			db.execSQL("CREATE TABLE " + TABLE_TAGS
					+ " (_id INTEGER PRIMARY KEY, lpId INTEGER, tag TEXT)");
			db.execSQL("CREATE TABLE " + TABLE_CHAPTERS
					+ " (_id INTEGER PRIMARY KEY, lpId INTEGER, url TEXT, title TEXT)");
			db.execSQL("CREATE TABLE " + TABLE_LATEST
					+ " (_id INTEGER PRIMARY KEY, lpId INTEGER)");
			db.execSQL("CREATE TABLE " + TABLE_FAVORITES
					+ " (_id INTEGER PRIMARY KEY, lpId INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("LPA",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
			onCreate(db);
		}
	}

}
