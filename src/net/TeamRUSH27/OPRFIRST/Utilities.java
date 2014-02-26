package net.TeamRUSH27.OPRFIRST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;

public class Utilities {

	/*Reads the res/raw/allteams.txt to see if team exists, 
	 *by checking for a real team name
	 */
	public static boolean teamExists(int teamNumber, Context context) {
		boolean match = false;
		String teamNumberString = String.valueOf(teamNumber);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.allteams)));
		String nextLine = " ";
		
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			Log.d("OPRFIRST2014 Utilites = teamExists", "R.raw.allteams is missing! Please notify the dev");
		}
		//if match returns true or we hit the end of the file, end while loop
		while(!match & nextLine != null) {
			String[] fields = nextLine.split(",");
			match = match || (fields[0].equals(teamNumberString) && (fields[1] != "--"));
			try{
				nextLine = br.readLine();
			}
			catch(IOException e) {
				//should have already reported error above, keep on truckin'!
			}
		}
		return match;
	}
	
}
