package org.ags.lparchive.model;

import java.io.Serializable;

import org.ags.lparchive.R;

public class LetsPlay implements Serializable {
	private static final long serialVersionUID = 2806939216184750744L;
	private int id;
	private String game;
	private String author;
	private String url;
	private String type;
//	private List<String> tags;
//	private List<UpdateLink> update_urls;
	
	public LetsPlay(int id, String game, String author, String url, String type) {
		this.id = id;
		this.game = game;
		this.author = author;
		this.url = url;
		this.type = type;
//		this.tags = new ArrayList<String>();
//		this.update_urls = new ArrayList<UpdateLink>();
	}

//	public LetsPlay(Element e) {
//		tags = new ArrayList<String>();
////		update_urls = new ArrayList<UpdateLink>();
//		url = "";
//		
//		List<Element> links = e.getAllElements(HTMLElementName.A);
//		for (Element link : links) {
//			String l_class = link.getAttributeValue("class");
//			String href = link.getAttributeValue("href");
//			if (l_class != null && href != null) { 
//				if(l_class.equals("tag")) {
//					tags.add(href.substring(5).split("#")[0]);
//				} else {
//					url = href;
//				}
//			}
//		}
//		
//		Element strong = e.getFirstElement(HTMLElementName.STRONG);
//		if (strong != null)
//			game = strong.getContent().toString();
//
//		Element span = e.getFirstElement(HTMLElementName.SPAN);
//		if (span != null) {
//			author = span.getContent().toString();
//			// remove "by " from author
//			author = author.substring(3, author.length());
//		}
//		
//		Element img = e.getFirstElement(HTMLElementName.IMG);
//		if (img != null) {
//			type = img.getAttributeValue("alt").toLowerCase();
//		} else {
//			type = "unknown";
//		}
//	}

	public String getGame() {
		return game;
	}

	public String getAuthor() {
		return author;
	}

	public String getUrl() {
		return url;
	}
//
//	public List<String> getTags() {
//		return tags;
//	}
//
//	public void addTag(String tag) {
//		tags.add(tag);
//	}

//	public List<UpdateLink> getUpdateUrls() {
//		return update_urls;
//	}
//
//	public void addUpdateUrl(UpdateLink updateLink) {
//		update_urls.add(updateLink);
//	}

	public String toString() {
		return String.format("%s by %s (%s)", game, author, url);
	}

	public int getIconResource() {
		return getIconResource(type);
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}
		
	public static int getIconResource(String type) {
		if (type.equals("text"))
			return R.drawable.icon_text;
		else if (type.equals("screenshot"))
			return R.drawable.icon_screenshot;
		else if (type.equals("video"))
			return R.drawable.icon_video;
		else if (type.equals("hybrid"))
			return R.drawable.icon_hybrid;
		else
			return R.drawable.icon;
	}

}
