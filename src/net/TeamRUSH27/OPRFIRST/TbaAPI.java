/**
 * OPR FIRST 2013
 * HomePage.java
 * Last Edit: 2/21/14 10:22AM
 * Colin Szechy
 */

package net.TeamRUSH27.OPRFIRST;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TbaAPI {

	//Demonstrate usage of class and for testing!
	//String url = "http://thebluealliance.com/api/v2/team/frc281/2011";
	//String placeholder = getPage(url);

	public String getPage(String url) {
		//This whole program requires so much error handling... I just threw a "throws MalformedURLException up top for now"
		URL team = null;
		try {
			team = new URL(url);
		} catch (Exception e) { }

		HttpURLConnection request = null;

		try {
			request = (HttpURLConnection)team.openConnection();
			request.setRequestProperty("X-TBA-App-Id", "OPRFIRST:scouting_app:2.1");
			request.setRequestMethod("GET");
			//this int below is for debug. It will report HTTP errors (like 404), etc. (200 is normal connection)
			int responseCode = request.getResponseCode();
		} catch (Exception e) { }
		String output = "";
		try {	
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) output += line;
		}
		catch(Exception e) { }
		request.disconnect();
		return output;
	}

}