package org.ags.lparchive.model;

import org.ags.lparchive.LPArchiveApplication;
import org.ags.lparchive.LPArchiveApplication.LPTypes;

public class LetsPlay {
	private long id;
	private String game;
	private String author;
	private String url;
	private LPTypes type;

	public LetsPlay(long id, String game, String author, String url,
			LPTypes type) {
		this.id = id;
		this.game = game;
		this.author = author;
		this.url = url;
		this.type = type;
	}

	public String getGame() {
		return game;
	}

	public String getAuthor() {
		return author;
	}

	public String getUrl() {
		return url;
	}

	public LPTypes getType() {
		return type;
	}

	public long getId() {
		return id;
	}

	public int getIconResource() {
		return LPArchiveApplication.getIconResource(type);
	}

	public String toString() {
		return String.format("%s by %s (%s)", game, author, url);
	}

}
