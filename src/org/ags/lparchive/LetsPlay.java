package org.ags.lparchive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LetsPlay implements Serializable {
	private static final long serialVersionUID = 2806939216184750744L;
	private String game;
	private String author;
	private String url;
	private List<String> tags;
	private List<UpdateLink> update_urls;

	public LetsPlay(String game, String author, String url, List<String> tags) {
		this.game = game;
		this.author = author;
		this.url = url;
		this.tags = tags;
		this.update_urls = new ArrayList<UpdateLink>();
	}

	public LetsPlay(String game, String author, String url) {
		this(game, author, url, new ArrayList<String>());
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

	public List<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public List<UpdateLink> getUpdateUrls() {
		return update_urls;
	}

	public void addUpdateUrl(UpdateLink updateLink) {
		update_urls.add(updateLink);
	}

	public String toString() {
		return String.format("%s by %s (%s) (%d updates)", game, author, url,
				update_urls.size());
	}
}
