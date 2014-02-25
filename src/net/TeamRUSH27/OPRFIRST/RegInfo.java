/**
 * OPR FIRST 2013
 * RegInfo.java
 * Last Edit: 7/11/13 1:52PM
 * Michael Ray
 * Class in charge of handling all IO and calculations for a regional
 * Changes to be made:
 */
package net.TeamRUSH27.OPRFIRST;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Jama.Matrix;
import android.content.Context;
import android.util.Log;

/*
 * @author Michael Ray
 */
public class RegInfo {

	private String regCode;
	private static final String API = "http://www.thebluealliance.com/api/";
	private RegInfoAwards[] awards;
	private RegInfoMatches[] matches;
	private RegInfoRanks[] ranks;
	private RegInfoStats[] stats;
	private RegInfoTeams[] teams;
	private boolean error;
	private Context c;
	
	/*
	 * Constructor
	 * @param c which is the application context in order to obtain resources for the app
	 * @param regCode which is the string representation of a 7-8 character regional code
	 */
	public RegInfo(Context c,String regCode) {
		this.c = c;
		this.regCode = regCode;
		this.error=false;
	}
	
	/*
	 * Retrieve the regional code
	 * @return String representing the 7-8 character regional code
	 */
	public String getRegCode() { return regCode; }
	
	/*
	 * Retrieve if any errors have surfaced while running through the calculations
	 * @return boolean if error has occurred
	 */
	public boolean getError() { return error; }
	
	/*
	 * Write data into a local file stored on the local cache for the application
	 * PostCondition: File has been written to the local cache
	 * @param name in string format of the file to write to
	 * @param data as an array list of string information to write to the file
	 * @return boolean if the file had correctly written or not
	 */
	private boolean writeFile(String name, ArrayList<String> data) {
		try {
			Log.d(c.getResources().getString(R.string.app_name),"Writing to: " + c.getFileStreamPath(name).toString());
			//Open a file stream in the application cache location
			FileOutputStream fos = c.getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE);
			//Iterate through the file and get the data
	        for (int i=0; i<data.size(); i++) { fos.write((data.get(i)+"\n").getBytes()); }
	        fos.close();
	        return true;
		} catch (Exception e) { 
			//Error has occurred so display to the log and set the error to true
			Log.d(c.getResources().getString(R.string.app_name),"Error writing file: " + name); 
			error=true; 
			return false; 
		}
    }
	
	/*
	 * Open a local file stored in the cache
	 * PreCondition: File at name exists
	 * PostCondition: The data according to type has been saved to its appropriate array
	 * @param name of the file in string format
	 * @param type of the file as an integer code (0=matches, 1=ranks, 2=stats, 3=awards, 4=teams)
	 * @return boolean if the file had correctly read in or not
	 */
	private boolean openLocalFile(String name, int type) {
        String line;
        int size;
        String data[];
        ArrayList<String> tempData = new ArrayList<String>();
        Log.d(c.getResources().getString(R.string.app_name),"Reading from: " + c.getFileStreamPath(name).toString());
        try {
        	//Open the file in the application cache and create the reader for it
        	FileInputStream in = c.getApplicationContext().openFileInput(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //Iterate through the file to read all of the data and store into a temp array list
            while ((line = br.readLine())!=null) { tempData.add(line); }
            //Determine what the size of the data is by using modulus division to find # of entries
        	if (tempData.size()%9==0) size=9;
        	else if (tempData.size()%10==0) size=10;
        	else size=11;
            if (type==0) { //Store information into Matches
                matches = new RegInfoMatches[tempData.size()/size];
                data = new String[size];
            	//Iterate through each match
                for (int i=0; i<tempData.size(); i+=size) {
                	//Iterate through all the information for each match
                    for (int j=0; j<size; j++) { data[j] = tempData.get(i+j); }
                    if (data[size-2].equals("") && data[size-1].equals("")) { data[size-1]="0"; data[size-2]="0"; }
                    //Store into a match object
                    matches[i/size]= new RegInfoMatches(data);
                }
            } else if (type==1) { //Store information into Ranks
        		ranks = new RegInfoRanks[tempData.size()/size];
                data = new String[size];
                //Iterate through each rank
                for (int i=0; i<tempData.size(); i+=size) {
                	//Iterate through all the information for each rank
                    for (int j=0; j<size; j++) { data[j] = tempData.get(i+j); }
                    //Store into a rank object
                    ranks[i/size]= new RegInfoRanks(data);
                }
            } else if (type==2) { //Store information into Stats
                stats = new RegInfoStats[tempData.size()/size];
                data = new String[size];
                //Iterate through each stat
                for (int i=0; i<tempData.size(); i+=size) {
                	//Iterate through all the information for each stat
                    for (int j=0; j<size; j++) { data[j] = tempData.get(i+j); }
                    //Store into a stat object
                    stats[i/size]= new RegInfoStats(data);
                }
            } else if (type==3) { //Store information into Awards
                awards = new RegInfoAwards[tempData.size()/5];
                data = new String[5];
                //Iterate through each award
                for (int i=0; i<tempData.size(); i+=5) {
                	//Iterate through all the information for each award
                    for (int j=0; j<5; j++) { data[j] = tempData.get(i+j); }
                    //Store into an award object
                    awards[i/5]= new RegInfoAwards(data);
                }
            } else if (type==4) { //Store information into Teams
                teams = new RegInfoTeams[tempData.size()/5];
                data = new String[5];
                //Iterate through each team
                for (int i=0; i<tempData.size(); i+=5) {
                	//Iterate through all the information for each team
                    for (int j=0; j<5; j++) { data[j] = tempData.get(i+j); }
                    //Store into a team object
                    teams[i/5]= new RegInfoTeams(data);
                }
            }
            br.close();
            return true;
        } catch (Exception e) { error=true; return false;}
    }
	
	/*
	 * Open the url for the specific type of information and pull the data into an array
	 * PreCondition: Internet connection exists
	 * PostCondition: The data according to type has been saved to its appropriate array
	 * @param infotype where it is the string to append at the end of the base url (matchresults, awards, rankings, elimmatchresults)
	 * @return String[][] 2d array with all of the data in its correct length and format
	 */
	private String[][] getHTMLData(String infoType) {
		//Initialize variables
        ArrayList<String> origData = new ArrayList<String>();
        boolean startAwardSearch = false;
        int cselector, selector=0, size;
        String[][] finalData = null;
        String inputLine;
        URL url;
        //Elimination results are on the same page as the match results so set the cSelector to 2
        if (infoType.equals("elimmatchresults")) cselector=2;
        else cselector=1;
        try {
        	//Use match result page for elim results, and the base url + infoType for all others
        	if (cselector==2) url = new URL(c.getApplicationContext().getResources().getString(R.string.firstWebsite)+regCode.substring(0,4)+"comp/Events/"+regCode.substring(4)+"/matchresults.html");
            else url = new URL(c.getApplicationContext().getResources().getString(R.string.firstWebsite)+regCode.substring(0,4)+"comp/Events/"+regCode.substring(4)+"/"+infoType+".html");
        	//Start the html parser
        	HttpURLConnection con = (HttpURLConnection)url.openConnection();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((inputLine = reader.readLine())!=null) {
            	//Stop parsing once it has reached the end of the readable table
                if ((inputLine.indexOf("</tbody")!=-1) && selector==cselector) break;
                //Add information between the TD Align or TD Style tags and its end tag to the array list
                if ((selector==cselector && cselector==1) && (inputLine.indexOf("<TD align")!=-1 || inputLine.indexOf("<TD style")!=-1))
                    origData.add(inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("\u003C\u002F")));
                //Add information between the TD Align or TD Style tags and its end tag to the array list with extra parameters for time
                if ((selector==cselector && cselector==2) && (inputLine.indexOf("<TD align")!=-1 || inputLine.indexOf("<TD style")!=-1) && inputLine.indexOf("PM")==-1 && inputLine.indexOf("AM")==-1) 
                    origData.add(inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("\u003C\u002F")));
                //Add information between standard tags
                if (startAwardSearch && inputLine.indexOf("font-family:Arial'>")!=-1)
                    origData.add(inputLine.substring(inputLine.indexOf("Arial'")+7, inputLine.indexOf("<o:p>")));
                //Add 1 to the selector which tells the parser that it has reached another division in the table or start parsing
                if (inputLine.indexOf("Blue Score")!=-1 || inputLine.indexOf("Played")!=-1) selector++;
                //Starts parsing the table for award urls
                if (inputLine.indexOf("<td style='background:white;padding:3.75pt")!=-1) startAwardSearch=true;
            }
            //Determine which information is being read and set the size of the output array accordingly
        	if (origData.size()%9==0) size=9;
        	else if (origData.size()%10==0) size=10;
        	else if (infoType.equals(c.getApplicationContext().getResources().getString(R.string.awardType))) size=5;
        	else size=11;
        	//Loop through the raw data and place into the output array
            finalData = new String[origData.size()/size][size];
            for (int i=0; i<origData.size(); i++) { finalData[i/size][i%size] = origData.get(i); }
            //Write the information out to a file
            writeFile(regCode+infoType, origData);
        } catch (Exception e) { error=true;}
        return finalData;
    }
	
	/*
	 * Get the information about the match schedule for the regional
	 * PreCondition: Internet connection exists and a regional code has been set
	 * @param update which is a boolean to express whether to update the file from the internet or not
	 * @return RegInfoMatches[] which is an array of match objects so an adapter can display the results
	 */
	public RegInfoMatches[] getRegInfoMatches(boolean update) {
		//Set the error code to false
		error=false;
		//Check if there is a forced update or a file doesnt exist
		if (update || !c.getApplicationContext().getFileStreamPath(regCode + c.getApplicationContext().getResources().getString(R.string.matchType)).exists()) {
			//Pull data from the internet
			String[][] origData = getHTMLData(c.getApplicationContext().getResources().getString(R.string.matchType));
	        matches = new RegInfoMatches[origData.length];
	        //Save the information to each object in the output array
	        for (int i=0; i<origData.length; i++) { matches[i] = new RegInfoMatches(origData[i]); }
		} else openLocalFile(regCode + c.getApplicationContext().getResources().getString(R.string.matchType),0);
		return matches;
	}

	/*
	 * Get the information about the rankings for the regional
	 * PreCondition: Internet connection exists and a regional code has been set
	 * @param update which is a boolean to express whether to update the file from the internet or not
	 * @return RegInfoRanks[] which is an array of rank objects so an adapter can display the results
	 */
	public RegInfoRanks[] getRegInfoRanks(boolean update) {
		//Set the error code to false
		error=false;
		//Check if there is a forced update or a file doesnt exist
		if (update || !c.getApplicationContext().getFileStreamPath(regCode + c.getApplicationContext().getResources().getString(R.string.rankType)).exists()) {
			//Pull data from the internet
			String[][] origData = getHTMLData(c.getApplicationContext().getResources().getString(R.string.rankType));
			
	        ranks = new RegInfoRanks[origData.length];
	        //Save the information to each object in the output array
	        for (int i=0; i<origData.length; i++) { ranks[i] = new RegInfoRanks(origData[i]); }
		} else openLocalFile(regCode + c.getApplicationContext().getResources().getString(R.string.rankType),1);
		return ranks;
	}

	/*
	 * Get the information about the awards for the regional
	 * PreCondition: Internet connection exists and a regional code has been set
	 * @param update which is a boolean to express whether to update the file from the internet or not
	 * @return RegInfoAwards[] which is an array of award objects so an adapter can display the results
	 */
	public RegInfoAwards[] getRegInfoAwards(boolean update) {
		//Set the error code to false
		error=false;
		//Check if there is a forced update or a file doesnt exist
		if (update || !c.getApplicationContext().getFileStreamPath(regCode + c.getApplicationContext().getResources().getString(R.string.awardType)).exists()) {
			//Pull data from the internet
			String[][] origData = getHTMLData(c.getApplicationContext().getResources().getString(R.string.awardType));
	        awards = new RegInfoAwards[origData.length];
	        //Save the information to each object in the output array
	        for (int i=0; i<origData.length; i++) { awards[i] = new RegInfoAwards(origData[i]); }
		} else openLocalFile(regCode+c.getApplicationContext().getResources().getString(R.string.awardType),3);
		return awards;
	}

	/*
	 * Get the information about the statistics for the regional
	 * PreCondition: Internet connection exists and a regional code has been set
	 * @param update which is a boolean to express whether to update the file from the internet or not
	 * @return RegInfoStats[] which is an array of stats objects so an adapter can display the results
	 */
	public RegInfoStats[] getRegInfoStats(boolean update) {
		//Set the error code to false
		error=false;
		//Check if there is a forced update or a file doesnt exist
		if (update || !c.getApplicationContext().getFileStreamPath(regCode + c.getApplicationContext().getResources().getString(R.string.statType)).exists()) {
			//Pull the information from matches and ranks according to the update parameter
			getRegInfoMatches(update);
	        getRegInfoRanks(update);
	        double[] teamSums, teamAutonSums, teamTeleopSums, teamClimbSums, teamOppSums;
	        double[][] matrix;
	        try {
	        	//Establish arrays for holding sums of points in climb, auton, teleop, total and opponent
	        	stats = new RegInfoStats[ranks.length];
		        teamSums = new double[ranks.length]; teamAutonSums = new double[ranks.length]; teamTeleopSums = new double[ranks.length]; 
		        teamClimbSums = new double[ranks.length]; teamOppSums = new double[ranks.length];
		        //Iterate through each team
		        for (int i=0; i<ranks.length; i++) {
		            stats[i] = new RegInfoStats(ranks[i].getTeam());
		            //Iterate through each match
		            for (int j=0; j<matches.length; j++) {
		            	//Check to see if the current team is playing in the match, and if the match has been played already
		                if (matches[j].checkForTeam(ranks[i].getTeam()) && matches[j].getBlueScore()!=0 && matches[j].getRedScore()!=0) stats[i].setMatchInfo(matches[j].getMatchData(ranks[i].getTeam()));
		            }
		            //Calculate the average score of the team and sum up the total points scored by the team in several categories
		            stats[i].calcAvgScore();
		            teamSums[i] = stats[i].getTtlPts();
		            teamOppSums[i] = stats[i].getOppScore();
		            teamAutonSums[i] = ranks[i].getAutonPts();
		            teamTeleopSums[i] = ranks[i].getTeleopPts();
		            teamClimbSums[i] = ranks[i].getClimbPts();
		        }
		        //Develop the 2d variable matrix for OPR calculations
		        matrix = new double[ranks.length][ranks.length];
		        //Iterate through each team
		        for (int i=0; i<matrix.length; i++) {
		        	//Get the teams that the current team has played with
		            int[] inputTeams = stats[i].getTeamsPlayedWith();
		            //Iterate through all of the teams the current team has played with
		            for (int j=0; j<inputTeams.length; j++) {
		            	//Match up each team that has played with the current team with the corresponding location and add 1 to that cell
		                for (int k=0; k<matrix.length; k++) {  if (ranks[k].getTeam()==inputTeams[j]) matrix[i][k]++; }
		            }
		        }
		        Matrix a = new Matrix(matrix);
		        //Solve the matrices by several categories to calculate several OPR categories
		        Matrix opr = a.solve(new Matrix(teamSums, teamSums.length));
		        Matrix dpr = a.solve(new Matrix(teamOppSums, teamOppSums.length));
		        Matrix hpopr = a.solve(new Matrix(teamAutonSums, teamAutonSums.length));
		        Matrix tpopr = a.solve(new Matrix(teamTeleopSums, teamTeleopSums.length));
		        Matrix ppopr = a.solve(new Matrix(teamClimbSums, teamClimbSums.length));
		        //Set the OPRs to the appropriate teams
		        for (int i=0; i<opr.getArray().length; i++) { stats[i].setOPRs(opr.get(i,0),dpr.get(i,0),hpopr.get(i,0),tpopr.get(i,0),ppopr.get(i,0)); }
		        ArrayList<String> data = new ArrayList<String>();
		        //Create and set an array to write a file to for caching purposes
		        for (int i=0; i<stats.length; i++) {
		        	for (int j=0; j<9; j++) { data.add(stats[i].toWrite()[j]); }
		        }
		        //Write the file
		        writeFile(regCode+c.getApplicationContext().getResources().getString(R.string.statType), data);
	        } catch (Exception e) { error=true; Log.d(c.getApplicationContext().getResources().getString(R.string.app_name),"Error calculating statistics"); }
		} else openLocalFile(regCode+c.getApplicationContext().getResources().getString(R.string.statType),2);
		return stats;
	}
	
	public String[] getStatsForPredict(int[] teams) {
		int[] data = new int[30];
		for (int i=0; i<6; i++) {
			RegInfoStats stat = null;
			for (int j=0; j<stats.length; j++) { if (teams[i]==Integer.parseInt(stats[j].getTeam())) { stat = stats[j]; break; } }
			data[i*5] = Integer.parseInt(stat.getTeam());
			int[] temp = stat.getOPRs();
			for (int j=0; j<temp.length; j++) { data[i*5+j+1] = temp[j];}
		}
		int[] fData = new int[20];
		String[] f = new String[20];
		int c=0;
		for (int i=0; i<data.length; i+=5) { fData[c] = data[i]; fData[c+1] = data[i+1]; c+=2; }
		for (int j=0; j<4; j++) { for (int i=1+j; i<data.length/2; i+=5) {  fData[12+j] += data[i];} }
		for (int j=0; j<4; j++) { for (int i=16+j; i<data.length; i+=5) { fData[16+j] += data[i];} }
		for (int i=0; i<fData.length; i++) { f[i] = Integer.toString(fData[i]);}
		return f;
	}

	/*
	 * Get the information about the teams for the regional
	 * PreCondition: Internet connection exists and a regional code has been set
	 * @param is which is an InputStream representing a saved file storing all of the teams in cache memory for easy access
	 * @param update which is a boolean to express whether to update the file from the internet or not
	 * @return RegInfoTeams[] which is an array of team objects so an adapter can display the results
	 */
	public RegInfoTeams[] getRegInfoTeams(InputStream is, boolean update) {
		//Set the error code to false
		error=false;
		//Check if there is a forced update or a file doesnt exist
		if (update || !c.getApplicationContext().getFileStreamPath(regCode+"teams").exists()) {
			//Initialize variables
			String line;
			int[] tempIndexer = new int[5000];
	        ArrayList<String[]> data = new ArrayList<String[]>();
	        ArrayList<String> printedData = new ArrayList<String>();
	        try {
	        	//Establish a buffered reader from the input stream to read the file
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            ArrayList<String> tempData = new ArrayList<String>();
	            //Read all of the data into a temporary array list
	            while ((line = br.readLine())!=null) { tempData.add(line);}
	            //Iterate through each line of data
	            for (int i=0; i<tempData.size(); i++) {
	            	String[] temp = new String[5];
	            	//Seprate the line into 5 sections based on comma location
	            	String tS = tempData.get(i);
	            	for (int j=0; j<4; j++) {
	            		temp[j] = tS.substring(0,tS.indexOf(","));
	            		tS = tS.substring(tS.indexOf(",")+1);
	            	}
	            	temp[4] = tS;
	            	data.add(temp);
	            	tempIndexer[Integer.parseInt(temp[0])] = i;
	            }
	            br.close();
	        } catch (Exception e) { error = true;}
	        //Get the url for the event in question
	        //Change regional codes for championship events
			String url;
			String api2code = "event/details?event=";
			String apiVersion = "v1/";
			if (regCode.equals(regCode.substring(0,4) + "galileo")) { url = API + apiVersion + api2code + regCode.substring(0,4) + "gal"; }
			else if (regCode.equals(regCode.substring(0,4) + "archimedes")) {url = API + apiVersion + api2code + regCode.substring(0,4) + "arc";  }
			else if (regCode.equals(regCode.substring(0,4) + "newton")) {url = API + apiVersion + api2code + regCode.substring(0,4) + "new";  }
			else if (regCode.equals(regCode.substring(0,4) + "curie")) {url = API + apiVersion + api2code + regCode.substring(0,4) + "cur"; }
			else if (regCode.equals(regCode.substring(0,4) + "einstein")) { url = API + apiVersion + api2code + regCode.substring(0,4) + "ein"; }
			else url = API + apiVersion + api2code + regCode; 
			//Pull the json object which has the list of teams for that regional

			//V1 API of blue alliance
	        //JSONArray jsonarray = jsonReader(url).optJSONArray("teams");

			//V2 API of blue alliance
			TbaAPI tbaAPI = new TbaAPI();
			JSONObject json = null;
			try {
				json = new JSONObject(tbaAPI.getPage(url));
			} catch (JSONException e1) { }
	        JSONArray jsonarray = json.optJSONArray("teams");
	        teams = new RegInfoTeams[jsonarray.length()];
	        try { 
	        	//Iterate through each team
	        	for (int i=0; i<teams.length; i++) { 
	        		//Get the stored data from earlier based on which team the loop is on
	        		String[] temp = data.get(tempIndexer[Integer.parseInt(jsonarray.getString(i).substring(3))]);
	        		for (int j=0; j<temp.length; j++) printedData.add(temp[j]);
	        		//Set that team to a team object
	        		teams[i] = new RegInfoTeams(temp); }
	        } catch (Exception e) { error=true;}
	        //Write the file
	        writeFile(regCode+"teams",printedData);
		} else openLocalFile(regCode+"teams",4);
		return teams;
	}
	
	/*
	 * Use json format to read a http/url connection
	 * PreCondition: Url has been approved and internet connection exists
	 * @param url which is the string url to get the json object from
	 * @return JSONObject which is a specific data type
	 */
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
}