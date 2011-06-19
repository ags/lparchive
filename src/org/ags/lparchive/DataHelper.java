package org.ags.lparchive;

import java.util.ArrayList;
import java.util.List;

import org.ags.lparchive.model.LetsPlay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {
	private static final String DATABASE_NAME = "lparchive.db";
	private static final int DATABASE_VERSION = 1;
	private static final String ARCHIVE_TABLE = "archive";
	private static final String TAGS_TABLE = "tags";
	private static final String UPDATES_TABLE = "updates";
	private static final String LATEST_TABLE = "latest";

	private static final String[] archiveColumns = new String[] { "id", "game",
			"author", "url", "type" };
	private static final String[] latestColumns = new String[] { "lp_id", "game",
		"author", "url", "type" };
	
	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertLpStmnt;
	private static final String INSERT_LP = "insert into " + ARCHIVE_TABLE
			+ "(game, author, url, type) values (?, ?, ?, ?)";

	private SQLiteStatement latestLpStmnt;
	private static final String LATEST_LP = "insert into " + LATEST_TABLE
			+ "(lp_id) values (?)";

	private SQLiteQueryBuilder recent_join;
	private static final String RECENT_JOIN = String.format(
			"%s LEFT OUTER JOIN %s ON (%s.id = %s.lp_id)", ARCHIVE_TABLE,
			LATEST_TABLE, ARCHIVE_TABLE, LATEST_TABLE);

	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertLpStmnt = this.db.compileStatement(INSERT_LP);
		this.latestLpStmnt = this.db.compileStatement(LATEST_LP);
		this.recent_join = new SQLiteQueryBuilder();
		this.recent_join.setTables(RECENT_JOIN);
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(RECENT_JOIN);
	}

	public SQLiteDatabase getDb() {
		return this.db;
	}

	public long insertLetsPlay(LetsPlay lp) {
		this.insertLpStmnt.bindString(1, lp.getGame());
		this.insertLpStmnt.bindString(2, lp.getAuthor());
		this.insertLpStmnt.bindString(3, lp.getUrl());
		this.insertLpStmnt.bindString(4, lp.getType());
		// TODO do inserts for tags and update urls
		return this.insertLpStmnt.executeInsert();
	}

	public void markRecentLetsPlay(LetsPlay lp) {
		Cursor cursor = this.db.query(ARCHIVE_TABLE, new String[] { "id" },
				"game=? AND author=?", new String[] { lp.getGame(),
						lp.getAuthor() }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
//				Log.d("LPA", String.valueOf(cursor.getInt(0)));
				this.latestLpStmnt.bindLong(1, cursor.getInt(0));
				this.latestLpStmnt.executeInsert();
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	public List<LetsPlay> getRecentLetsPlay() {
		Cursor cursor = recent_join.query(this.db, latestColumns, null, null,
				null, null, "game asc");
		return letsPlayList(cursor);
	}

	public void deleteAll() {
		this.db.delete(ARCHIVE_TABLE, null, null);
		this.db.delete(TAGS_TABLE, null, null);
		this.db.delete(UPDATES_TABLE, null, null);
	}

	public List<LetsPlay> getArchive() {
		Cursor cursor = this.db.query(ARCHIVE_TABLE, archiveColumns, null,
				null, null, null, "game asc");

		return letsPlayList(cursor);
	}

	private List<LetsPlay> letsPlayList(Cursor cursor) {
		List<LetsPlay> list = new ArrayList<LetsPlay>();
		if (cursor.moveToFirst()) {
			do {
				LetsPlay lp = new LetsPlay(cursor.getInt(0), cursor
						.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));
				list.add(lp);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		/* create db schema */
		public void onCreate(SQLiteDatabase db) {
			db
					.execSQL("CREATE TABLE "
							+ ARCHIVE_TABLE
							+ " (id INTEGER PRIMARY KEY, game TEXT, author TEXT, url TEXT, type TEXT)");
			db.execSQL("CREATE TABLE " + TAGS_TABLE
					+ " (id INTEGER PRIMARY KEY, tag TEXT)");
			db.execSQL("CREATE TABLE " + UPDATES_TABLE
					+ " (id INTEGER PRIMARY KEY, url TEXT, title TEXT)");
			db.execSQL("CREATE TABLE " + LATEST_TABLE
					+ " (id INTEGER PRIMARY KEY, lp_id INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("LPA",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + ARCHIVE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TAGS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + UPDATES_TABLE);
			onCreate(db);
		}
	}
}
