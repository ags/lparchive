package org.ags.lparchive.model;

public class UpdateLink {
	private String href;
	private String title;
	
	public String getHref() {
		return href;
	}

	public String getTitle() {
		return title;
	}

	public UpdateLink(String href, String title) {
		super();
		this.href = href;
		this.title = title;
	}
}
