package com.emergency.services.rssparser;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.PrintStream;
import java.util.ArrayList;

public class RSSParser {
	private SAXParser parser;
	private XMLReader xr;
	private RSSHandler rh;

	final String INVALID = "INVALID";

	public RSSParser() throws Exception {
		System.setProperty("org.xml.sax.driver",
			"org.xmlpull.v1.sax2.Driver");

		xr = XMLReaderFactory.createXMLReader();
		rh = new RSSHandler();
		xr.setContentHandler(rh);
		xr.setErrorHandler(rh);
	}

	public ArrayList<RSSEntry> parse(InputSource is) throws Exception {
		xr.parse(is);
		return (rh.getOutput());
	}

	public class RSSHandler extends DefaultHandler {
		private RSSEntry entry;
		private String title;
		private String link;
		private String description;
		private String element;
		private ArrayList<RSSEntry> output;

		public RSSHandler() {
			this.entry = new RSSEntry();
			this.element = INVALID;
			this.title = "";
			this.link = "";
			this.description = "";
			this.output = new ArrayList<RSSEntry>();
		}

		public ArrayList<RSSEntry> getOutput() {
			return (this.output);
		}

		public void startElement(String uri, String name, String qName,
				Attributes attrs) {

			if (name.equals("title") || name.equals("link") || name.equals("description"))
				this.element = name;
			else if (name.equals("item"))
				this.entry = new RSSEntry();
				
			else
				System.out.println("Unknown element: " + name);
				
		}

		public void endElement(String uri, String name, String qName) {

			if (name.equals("title") && "title".equals(this.element))
				this.entry.setTitle(this.title);
			else if (name.equals("link") && "link".equals(this.element))
				this.entry.setLink(this.link);
			else if (name.equals("description") && "description".equals(this.element))
				this.entry.setDescription(this.description);
			else if (name.equals("item"))
				this.output.add(this.entry);
				/*
			else
				System.out.println("Unknown element: " + name);
				*/

			this.element = INVALID;
		}

		public void characters (char ch[], int start, int length) {
			if (length > 0) {
				if ("title".equals(this.element)) {
					this.title = new String(ch, start, length);
				}
				else if ("link".equals(this.element)) {
					this.link = new String(ch, start, length);
				}
				else if ("description".equals(this.element)) {
					this.description = new String(ch, start, length);
				}
			}
		}
	}
}
