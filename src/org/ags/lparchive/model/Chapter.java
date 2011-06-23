package org.ags.lparchive.model;

public class Chapter {
	private String url;
	private String title;
	
	public Chapter(String url, String title) {
		super();
		this.url = url;
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

}
