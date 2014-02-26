package net.TeamRUSH27.OPRFIRST;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class TeamInfo {

	private String team;
	private boolean error;
	private Context c;
	
	private RegInfoMatches[] matches;
	private RegInfoRanks[] ranks;
	private RegInfoStats[] stats;
	private RegInfoAwards[] awards;
	private RegInfoTeams teamObject;
	private static final String API = "http://www.thebluealliance.com/api/";
	
	public TeamInfo(Context c,String team) {
		this.c = c;
		this.team = team;
	}

	public String getTeam() { return team; }
	public boolean getError() { return error; }
	
	private void writeFile(String name, ArrayList<String> data) {
		try {
			FileOutputStream fos = c.getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE);
	        for (int i=0; i<data.size(); i++) { fos.write((data.get(i)+"\n").getBytes()); }
	        fos.close();
		} catch (Exception e) { error=true; }
    }
	
	private ArrayList<String> openFile(String teamNum) {
		ArrayList<String> tempData = new ArrayList<String>();
		String line;
		try {
			FileInputStream in = c.getApplicationContext().openFileInput(teamNum);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine())!=null) { tempData.add(line); }
			br.close();
			matches = new RegInfoMatches[tempData.size()/11];
			for (int i=0; i<matches.length; i++) {
				String[] data = new String[11];
				for (int j=0; j<11; j++) { data[j] = tempData.get((i*11)+j); }
				if (!(data[9].equals("") && data[10].equals(""))) matches[i]= new RegInfoMatches(data);
			}
		} catch (Exception e) { error=true; }
		return tempData;
	}
	
	private ArrayList<String> getRegionals() {
		//V2 API of blue alliance
		String url  = API + "v1/team/details?team=frc" + team;
		ArrayList<String> data = new ArrayList<String>();
		try {
			TbaAPI tbaAPI = new TbaAPI();
			JSONObject json = new JSONObject(tbaAPI.getPage(url));
			JSONArray jarray = json.getJSONArray("events");
			for (int i=0; i<jarray.length(); i++) { 
				if (jarray.getString(i).equals("2013gal")) data.add("2013galileo");
				else if (jarray.getString(i).equals("2013new")) data.add("2013newton");
				else if (jarray.getString(i).equals("2013arc")) data.add("2013archimedes");
				else if (jarray.getString(i).equals("2013cur")) data.add("2013curie");
				else if (jarray.getString(i).equals("2013ein")) data.add("2013einstein");
				else if (!jarray.getString(i).equals("2013iri")) data.add(jarray.getString(i));
				else data.add(jarray.getString(i)); }
		} catch (JSONException e) {}
		return data;
	}
	
	private JSONObject jsonReader(String url) {
		JSONObject json = null;
		try {
			HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String line, input="";
			while ((line = reader.readLine()) != null) input += line;
			json = new JSONObject(input);
		} catch (Exception e) {error=true;}
		return json;
	}
	
	public String[] getRegData(String regional) {
		String[] data = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new URL("http://www.chiefdelphi.com/forums/frcspy.php?xml=2&events=" + regional).openStream());
			doc.getDocumentElement().normalize();
			NodeList[] info = {doc.getElementsByTagName("event"),doc.getElementsByTagName("mch"),
				doc.getElementsByTagName("red1"),doc.getElementsByTagName("red2"),doc.getElementsByTagName("red3"),doc.getElementsByTagName("blue1"),
				doc.getElementsByTagName("blue2"),doc.getElementsByTagName("blue3"),doc.getElementsByTagName("rfin"),doc.getElementsByTagName("bfin")};
			NodeList tempInfo = doc.getElementsByTagName("typ");
			data = new String[10];
			for (int j=0; j<10; j++) {
				if (j==1) data[j] = tempInfo.item(0).getTextContent()+" "+info[j].item(0).getTextContent();
				else data[j] = info[j].item(0).getTextContent();
			}
		} catch (Exception e) {error = true;}
		return data;
	}
	
	private String[][] getXMLData(String team) {
		String[][] data = null;
		ArrayList<String> writeInfo = new ArrayList<String>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new URL("http://www.chiefdelphi.com/forums/frcspy.php?xml=2&teams=" + team).openStream());
			doc.getDocumentElement().normalize();
			NodeList[] info = {doc.getElementsByTagName("pubdate"),doc.getElementsByTagName("event"),doc.getElementsByTagName("mch"),
				doc.getElementsByTagName("red1"),doc.getElementsByTagName("red2"),doc.getElementsByTagName("red3"),doc.getElementsByTagName("blue1"),
				doc.getElementsByTagName("blue2"),doc.getElementsByTagName("blue3"),doc.getElementsByTagName("rfin"),doc.getElementsByTagName("bfin")};
			NodeList tempInfo = doc.getElementsByTagName("typ");
			data = new String[info[0].getLength()][11];
			for (int i = 0; i < info[0].getLength(); i++) {
				for (int j=0; j<11; j++) {
					if (j==1) data[i][j] = tempInfo.item(i).getTextContent()+" "+info[j].item(i).getTextContent();
					else data[i][j] = info[j].item(i).getTextContent();
					writeInfo.add(data[i][j]);
				}
			}
			writeFile(team,writeInfo);
		} catch (Exception e) {error = true;}
		return data;
	}
	
	public RegInfoMatches[] getTeamInfoMatches(boolean update) {
		error=false;
		if (update || !c.getApplicationContext().getFileStreamPath(team).exists()) {
			String[][] origData = getXMLData(team);
	        matches = new RegInfoMatches[origData.length];
	        for (int i=0; i<origData.length; i++) { matches[i] = new RegInfoMatches(origData[i]); }
		} else openFile(team);
		return matches;
	}
	
	public RegInfoStats[] getTeamInfoStats(boolean update) {
		error=false;
		ArrayList<String> regionals = getRegionals();
		this.stats = new RegInfoStats[regionals.size()];
		for (int i=0; i<regionals.size(); i++) {
			RegInfo reginfo = new RegInfo(c,regionals.get(i));
			ArrayList<RegInfoStats> stats = reginfo.getRegInfoStats(update);
			for (int j=0; j<stats.size(); j++) { 
				if (stats.get(j).getTeam().equals(team)) { 
					this.stats[i] = stats.get(j); 
					this.stats[i].setTeam(regionals.get(i));
					break; 
				} 
			}
		}
        return this.stats;
	}
	
	public RegInfoRanks[] getTeamInfoRanks(boolean update) {
		error=false;
		ArrayList<String> regionals = getRegionals();
		this.ranks = new RegInfoRanks[regionals.size()];
		for (int i=0; i<regionals.size(); i++) {
			RegInfo reginfo = new RegInfo(c,regionals.get(i));
			ArrayList<RegInfoRanks> ranks = reginfo.getRegInfoRanks(update);
			for (int j=0; j<ranks.size(); j++) { 
				if (ranks.get(j).getTeam()==Integer.parseInt(team)) { 
					this.ranks[i] = ranks.get(j);
					this.ranks[i].setTeam(regionals.get(i));
					break; 
				} 
			}
		}
        return this.ranks;
	}
	
	public RegInfoAwards[] getTeamInfoAwards(boolean update) {
		error=false;
		ArrayList<String> regionals = getRegionals();
		ArrayList<RegInfoAwards> tempAwards = new ArrayList<RegInfoAwards>();
		for (int i=0; i<regionals.size(); i++) {
			RegInfo reginfo = new RegInfo(c,regionals.get(i));
			RegInfoAwards[] awards = reginfo.getRegInfoAwards(update);
			for (int j=0; j<awards.length; j++) { 
				if (awards[j].getTeam()==Integer.parseInt(team)) { 
					awards[j].setLocation(regionals.get(i));
					tempAwards.add(awards[j]); 
					break;
				} 
			}
		}
		this.awards = new RegInfoAwards[tempAwards.size()];
		for (int i=0; i<awards.length; i++) { this.awards[i] = tempAwards.get(i); }
        return this.awards;
	}
	
	public RegInfoTeams getTeamInfo(InputStream is, boolean update) {
		if (update) {
			try {
				String url = API + "v1/team/details?team=frc" + team;
				//Pull the json object which has the list of teams for that regional
				TbaAPI tbaAPI = new TbaAPI();
				JSONObject json = new JSONObject(tbaAPI.getPage(url));
		        String[] data = {json.getString("team_number"),json.getString("nickname"),json.getString("name"),json.getString("location"),json.getString("website")};
		        teamObject = new RegInfoTeams(data);
			} catch (Exception e) { error=true; }
		} else {
			//Initialize variables
			String line;
	        try {
	        	//Establish a buffered reader from the input stream to read the file
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            ArrayList<String> tempData = new ArrayList<String>();
	            //Read all of the data into a temporary array list
	            while ((line = br.readLine())!=null) { tempData.add(line);}
	            //Iterate through each line of data
	            for (int i=0; i<tempData.size(); i++) {
	            	String[] temp = new String[5];
	            	//Separate the line into 5 sections based on comma location
	            	String tS = tempData.get(i);
	            	for (int j=0; j<4; j++) {
	            		temp[j] = tS.substring(0,tS.indexOf(","));
	            		tS = tS.substring(tS.indexOf(",")+1);
	            	}
	            	temp[4] = tS;
	            	if (temp[0].equals(team)) { teamObject = new RegInfoTeams(temp); break; }
	            }
	            br.close();
	        } catch (Exception e) { error = true;}
		}
        return teamObject;
	}
}
