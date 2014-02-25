/**
 * OPR FIRST 2013
 * HomePage.java
 * Last Edit: 7/9/13 2:48PM
 * Michael Ray
 * Main activity of OPR FIRST 2013. Controls regional and team selection and displays ChiefDelphi RSS feed
 * Changes to be made: Add a function to refresh all regionals
 */
package net.TeamRUSH27.OPRFIRST;

import java.util.ArrayList;
import java.util.Arrays;

import net.TeamRUSH27.OPRFIRST.RSSFeed.FeedMessage;
import net.TeamRUSH27.OPRFIRST.RSSFeed.RSSAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

/*
 * @author Michael Ray
 */
public class HomePage extends SherlockFragmentActivity {

	public final static String EXTRA_MESSAGE = "net.TeamRUSH27.OPRFRC2013.MESSAGE";
	private ActionBar actionBar;
	private MenuItem menuitem;
	private AutoCompleteTextView textView;
	private SearchView mSearchView;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_home_page);
		prefs = PreferenceManager.getDefaultSharedPreferences(HomePage.this);
		//Set the textview for inputting regional codes to auto-complete using the RegCode array element
		textView = (AutoCompleteTextView)findViewById(R.id.selection);
    	//Set the text color to white if not using Froyo
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) textView.setTextColor(Color.WHITE);
		String[] regCodes;
		if (prefs.getString("pref_year", "2014").equals("2013"))
			regCodes = getResources().getStringArray(R.array.regCodes2013);
		else
			regCodes = getResources().getStringArray(R.array.regCodes2014);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, regCodes);
		textView.setAdapter(adapter);
		//Set the action bar for the home page
		actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.app_name));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Start the RSS feed thread to update the RSS list on the main page
		RSS rss = new RSS();
		rss.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Create the menu in the action bar for the home page
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_home_page, menu);
		
		//Setup for the SearchView in the ActionBar's action_search for AutoCompleteTextView
		MenuItem searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//React to item being selected on menu bar
		switch(item.getItemId()) {
		case R.id.action_refresh: //Refresh all teams
			//Set the refresh button to the progressbar layout, which is a spinning wheel
			menuitem = item; 
			menuitem.setActionView(R.layout.progressbar);
			//Start the refreshAll thread to update all regionals
			RefreshAll task = new RefreshAll();
			task.execute();
			return true;
		case R.id.action_settings: //Open the settings panel
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.action_help: //Displays the help screen
			onCoachMark();
			return true;
		case R.id.action_search: //Searching for team or event, like the AutoCompletingTextView below
			
			return true;
		case R.id.action_about: //Displays the current information about the app
			String vName="", vCode="";
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				vName = pInfo.versionName;
				vCode = Integer.toString(pInfo.versionCode);
			} catch (NameNotFoundException e) {	}
			//Creates a dialog box to display to the users
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
		    adb.setTitle("About");
		    adb.setMessage(getResources().getString(R.string.app_name) + "\nVersion: " + vName + " (" + vCode + ")\n" + getResources().getString(R.string.author));
		    adb.setPositiveButton("Ok", null);
		    adb.show();
		    return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Displays the help dialog box
	 * @return void
	 */
	@SuppressWarnings("deprecation")
	private void onCoachMark(){
		//Displays the help screen as a dialog box
	    final Dialog dialog = new Dialog(this);
	    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    dialog.setContentView(R.layout.activity_help_screen);
	    dialog.setCanceledOnTouchOutside(true);
	    //Set a click listener to cancel the dialog when touched
	    View masterView = dialog.findViewById(R.id.helpLayout);
	    masterView.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { dialog.dismiss(); } });
	    //Set to full screen
	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.show();
	    dialog.getWindow().setAttributes(lp);
	}
	
	/*
	 * Starts a new intent when the button Search is clicked
	 * PreCondition: Text has been entered into the textview
	 * @param View automatically generated by the button that calls it
	 * @return void
	 */
	public void onClick(View view) {
		//Create an intent based on what the user is searching for, if is eligible intent
		Intent intent = new Intent();// = null;
		String[] regCodes = null;
		//If this is true, we have a team # on our hands to handle
		if(textView.getText().charAt(0) >= 48 && textView.getText().charAt(0) <=57) {
			try {
				int teamNumber= Integer.parseInt(textView.getText().toString());
				intent = new Intent(HomePage.this, TeamInfoInterface.class);
				if(prefs.getString("pref_year", "2013").equals("2013")) 
					regCodes = getResources().getStringArray(R.array.regCodes2013);
				else
					regCodes = getResources().getStringArray(R.array.regCodes2014);
				//before we start the activity, check that the team actually exists
				if(Utilities.teamExists(teamNumber, this)) {
					intent.putExtra(HomePage.EXTRA_MESSAGE, String.valueOf(teamNumber));
					startActivity(intent);
				}
				else {	//notify user
					Toast.makeText(this, "FRC#" + teamNumber+ " doesn't exist!", Toast.LENGTH_SHORT).show();
				}
			}
			catch(NumberFormatException e) {
				//make the user try again! Shoulda formatted their number properly!
				//So do nothing here. Exit this if-statement, and stop processing the request.
			}
		}
		else { //check if it's an event!
			if(prefs.getString("pref_year", "2013").equals("2013")) {
				regCodes = getResources().getStringArray(R.array.regCodes2013);
			}
			else
				regCodes = getResources().getStringArray(R.array.regCodes2014);
			//Log.d("OPFIRST2014 code", Arrays.toString(regCodes));
			int val = 0;
			String code = textView.getText().toString();
			for(int i = 0; i < regCodes.length; i++) {
				if(code.equals(regCodes[i]))
					val = i;
			}
			if(val%2==1) {
				val--;
				code = regCodes[val];
			//}
			Log.d("OPRFIRST2014 code, regCodes", code+";"+regCodes[val+1]);
			//if(code.equals(regCodes[val+1])) {
				intent = new Intent(HomePage.this, RegInfoInterface.class);
				intent.putExtra(HomePage.EXTRA_MESSAGE,  code+";"+regCodes[val+1]);
				startActivity(intent);
			}
			else {	//notify user of failure
				Toast.makeText(this, "Event \"" + textView.getText() + "\" doesn't exist!", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		//if (textView.getText().toString().length()>4)  
		//	intent = new Intent(HomePage.this, RegInfoInterface.class);
		
		/*if (prefs.getString("pref_year", "2013").equals("2013"))
			regCodes = getResources().getStringArray(R.array.regCodes2013);
		else
			regCodes = getResources().getStringArray(R.array.regCodes2014);
		int val = 0;
		String code = textView.getText().toString();
		for (int i=0; i<regCodes.length; i++) { 
			if (code.equals(regCodes[i])) 
				val = i; 
		}
		if (val%2==1) { 
			val--; 
			code = regCodes[val]; 
		}
		//Add the info to the intent and start the activity
		intent.putExtra(HomePage.EXTRA_MESSAGE, code+";"+regCodes[val+1]);
		if (code.length()!=0)
			startActivity(intent);*/
	}
	
	private class RefreshAll extends AsyncTask <Void, Integer, String> {
		@Override
		protected String doInBackground(Void...params) { 
			//NOTE: Add the calculations to refresh all of the regionals here
			return null; 
			}
		
		@Override
		protected void onPostExecute(String result) { menuitem.collapseActionView(); menuitem.setActionView(null); }
	}
	
	private class RSS extends AsyncTask <Void, Integer, ArrayList<FeedMessage>> {
		@Override
		protected ArrayList<FeedMessage> doInBackground(Void...params) {
			//Start a refresh object with this URL
			RSSFeed rss = new RSSFeed("http://www.chiefdelphi.com/forums/external.php?type=RSS2");
			try {
				if (prefs.getBoolean("pref_autoUpdate", true)) return rss.readFeed();
				else return rss.getBlank();
			} catch (Exception e) { return rss.getBlank(); }
		}
		
		@Override
		protected void onPostExecute(final ArrayList<FeedMessage> messages) {
			//Puts the information into the listview on the main page
			final ListView lv = (ListView)findViewById(R.id.lst_rss_feed_holder);
			lv.setAdapter(new RSSAdapter(HomePage.this, R.layout.row_rss, messages));
			lv.setCacheColorHint(Color.TRANSPARENT);
			//Determine if using autoscroll function from the preference menu
			if (prefs.getBoolean("pref_autoScroll", true))
				lv.post(new Runnable() { public void run() { lv.smoothScrollBy(1000, 120000); } });
			//Set a list click listener to display the web browswer when a rss item is clicked
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) { 
			    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.get(position).getLink()));
			    	startActivity(browserIntent);
			    }
			});
		}
	}
	
}
