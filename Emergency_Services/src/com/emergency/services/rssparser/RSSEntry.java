package com.emergency.services.rssparser;

public class RSSEntry {
	private String title;
	private String link;
	private String description;

	public void setTitle(String s) {
		this.title = s;
	}

	public String getTitle() {
		return title;
	}

	public void setLink(String s) {
		this.link = s;
	}

	public String getLink() {
		return link;
	}

	public void setDescription(String s) {
		this.description = s;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return (this.title);
	}
}
