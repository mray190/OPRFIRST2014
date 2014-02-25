package net.TeamRUSH27.OPRFIRST;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RSSFeed {
    private final URL url;
    private ArrayList<FeedMessage> messages;

    public RSSFeed(String feedUrl) {
        messages = new ArrayList<FeedMessage>();
        try { this.url = new URL(feedUrl);
        } catch (MalformedURLException e) { throw new RuntimeException(e); }
    }

    public ArrayList<FeedMessage> getBlank() { 
    	FeedMessage fm = new FeedMessage();
    	fm.setTitle("Go to Settings to enable the RSS feed");
    	fm.setLink("http://www.chiefdelphi.com");
    	messages.add(fm);
    	return messages;
    }
    
    public ArrayList<FeedMessage> readFeed() {
        InputStream stream = null;
        URLConnection conn = null;
        DocumentBuilder builder = null;
        Document document = null;
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            conn = url.openConnection();
            stream = conn.getInputStream();
            document = builder.parse(stream);
            stream.close();
	        Element docEle = document.getDocumentElement();
	        NodeList n = docEle.getElementsByTagName("item");
	        for (int i=0; i<n.getLength(); i++) {
	        	Element e = (Element)n.item(i);
	        	FeedMessage m = new FeedMessage();
	        	m.setTitle(e.getElementsByTagName("title").item(0).getTextContent());
	        	m.setLink(e.getElementsByTagName("link").item(0).getTextContent());
	        	String date = e.getElementsByTagName("pubDate").item(0).getTextContent();
	        	m.setDate(date.substring(5,date.length()-4));
	        	m.setDescription(e.getElementsByTagName("description").item(0).getTextContent());
	        	m.setCategory(e.getElementsByTagName("category").item(0).getTextContent());
	        	messages.add(m);
	        	
	        }
        } catch (Exception e) { }
        return messages;
    }
    
    public class FeedMessage {
          private String title, description,link,date,category;
          public String getTitle() { return title; }
          public void setTitle(String title) { this.title = title; }
          public String getDescription() { return description; }
          public void setDescription(String description) { this.description = description; }
          public String getLink() { return link; }
          public void setLink(String link) { this.link = link; }
          public String getDate() { return date; }
          public void setDate(String date) { this.date = date; }
          public String getCategory() { return category; }
          public void setCategory(String category) { this.category = category; }
          @Override
          public String toString() {
              return "FeedMessage [title=" + title + ", description=" + description
                  + ", link=" + link + ", date=" + date + ", category=" + category
                  + "]";
          }
      }
    
    public static class RSSAdapter extends ArrayAdapter<FeedMessage>{
		Context context;
		int layoutResourceId;
		ArrayList<FeedMessage> data = null;
		DecimalFormat df = new DecimalFormat("0.00");
		
		public RSSAdapter(Context context, int layoutResourceId, ArrayList<FeedMessage> data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RSSHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RSSHolder();
				holder.titleTxt = (TextView)row.findViewById(R.id.titleTxt);
				holder.categoryTxt = (TextView)row.findViewById(R.id.categoryTxt);
				holder.pubdateTxt = (TextView)row.findViewById(R.id.pubdateTxt);
				row.setTag(holder);
			} else {
				holder = (RSSHolder)row.getTag();
			}
			FeedMessage rS = data.get(position);
			holder.titleTxt.setText(rS.getTitle());
			holder.categoryTxt.setText(rS.getCategory());
			holder.pubdateTxt.setText(rS.getDate());
			return row;
		}
		
		static class RSSHolder {
			TextView titleTxt, pubdateTxt,categoryTxt;
		}
	}
}
