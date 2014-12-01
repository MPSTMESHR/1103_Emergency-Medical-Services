/* Based on code from:
 * 	http://code.google.com/appengine/docs/java/urlfetch/overview.html
 * On:
 * 	Thu Mar 24 15:56:02 EDT 2011
 */
package com.emergency.services.rssparser;

import org.xml.sax.InputSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class URLFetch {

	public static void main(String args[]) throws Exception {

		try {

			RSSParser parser = new RSSParser();
			for (int i = 0; i < args.length; i++) {
				URL url = new URL(args[i]);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String line;

				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();
				
				parser.parse(new InputSource(url.openStream()));

			}
		} catch (MalformedURLException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
