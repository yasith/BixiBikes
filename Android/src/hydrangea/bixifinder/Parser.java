package hydrangea.bixifinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class Parser {
	private static final String namespace = null;

	public ArrayList<StationEntry> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser p = Xml.newPullParser();
			p.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			p.setInput(in, null);
			p.nextTag();
			return readFeed(p);
		} finally {
			in.close();
		}
	}

	private ArrayList<StationEntry> readFeed(XmlPullParser p) throws XmlPullParserException, IOException {
		ArrayList<StationEntry> entries = new ArrayList<StationEntry>();
		p.require(XmlPullParser.START_TAG, namespace, "feed");
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name.equals("station")){
				entries.add(readStationEntry(p));
			} else {
				skip(p);
			}
		}
		return entries;
		
		
		
	}
	
	private StationEntry readStationEntry(XmlPullParser p)
			throws XmlPullParserException, IOException {
		p.require(XmlPullParser.START_TAG, null, "station");
		String name = null;
		String latitude = null;
		String longitude = null;
		String numBikes = null;
		String numEmpty = null;
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String n = p.getName();
			if (n.equals("name")) {
				name = readStuff(p, "name");
			} else if (n.equals("lat")) {
				latitude = readStuff(p, "lat");
			} else if (n.equals("long")) {
				longitude = readStuff(p, "long");
			} else if (n.equals("nbBikes")) {
				numBikes = readStuff(p, "nbBikes");
			} else if (n.equals("nbEmptyDocks")) {
				numEmpty = readStuff(p, "nbEmptyDocks");
			} else {
				skip(p);
			}
		}
		return new StationEntry(name, latitude, longitude, numBikes, numEmpty);
	}

	private String readStuff(XmlPullParser p, String tag) throws XmlPullParserException, IOException {
		p.require(XmlPullParser.START_TAG, null, tag);
		String name = readText(p);
		p.require(XmlPullParser.END_TAG, null, tag);
		return name;
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	public static class StationEntry {
		public final String name;
		public final String latitude;
		public final String longitude;
		public final String numBikes;
		public final String numSpots;

		private StationEntry(String name, String latitude, String longitude,
				String numBikes, String numSpots) {
			this.name = name;
			this.latitude = latitude;
			this.longitude = longitude;
			this.numBikes = numBikes;
			this.numSpots = numSpots;
		}

		
	}
	
}