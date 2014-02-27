/**
 * OPR FIRST 2013
 * RegInfoInterface.java
 * Last Edit: 7/9/13 2:47PM
 * Michael Ray
 * Interface for viewing stats of a regional. Implements a pager view
 * Changes to be made:
 */
package net.TeamRUSH27.OPRFIRST;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards.AwardsFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches.MatchesFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks.RanksFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoStats.StatsFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoTeams.TeamsFragment;
import net.TeamRUSH27.OPRFIRST.adapter.TabsPagerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

/*
 * @author Michael Ray
 */
public class RegInfoInterface extends SherlockFragmentActivity implements ActionBar.TabListener {

	private ViewPager mPager;
	private TabsPagerAdapter mPagerAdapter;
	private ActionBar actionBar;
	
	private String regCode, regName;
	private MenuItem menuitem;
	
	private int menuVisibility;

	public void onTabSelected(Tab tab, FragmentTransaction ft) { 
		menuVisibility=tab.getPosition();
		supportInvalidateOptionsMenu(); 
		mPager.setCurrentItem(tab.getPosition()); 
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	
	/*
	 * Manages the navigation, fragments and panels for swipey tabs and action bar menus
	 * PreCondtion: Title has been determined and a view exists to put the content
	 * @param Title a string representing the title of the interface
	 * @return void
	 */
	private void managePageNavigation() {
        mPager = (ViewPager) findViewById(R.id.pager);
		//Sets an action bar with tabs
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(regName);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
        FragmentManager fm = getSupportFragmentManager();
		//Creates a Pager view and sets a listener and adapter to monitor swipes
		
		mPagerAdapter = new TabsPagerAdapter(fm, regCode);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOffscreenPageLimit(5);
		
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { 
		    public void onPageSelected(int position) { actionBar.setSelectedNavigationItem(position); }
		    public void onPageScrolled(int arg0, float arg1, int arg2) { }
		    public void onPageScrollStateChanged(int arg0) { }
		});
		
		//Create the tabs and fragments
		ActionBar.Tab tab1 = actionBar.newTab().setText(getResources().getString(R.string.tab1));
		actionBar.addTab(tab1.setTabListener(this));
		ActionBar.Tab tab2 = actionBar.newTab().setText(getResources().getString(R.string.tab2));
		actionBar.addTab(tab2.setTabListener(this));
		ActionBar.Tab tab3 = actionBar.newTab().setText(getResources().getString(R.string.tab3));
		actionBar.addTab(tab3.setTabListener(this));
		ActionBar.Tab tab4 = actionBar.newTab().setText(getResources().getString(R.string.tab4));
		actionBar.addTab(tab4.setTabListener(this));
		ActionBar.Tab tab5 = actionBar.newTab().setText(getResources().getString(R.string.tab5));
		actionBar.addTab(tab5.setTabListener(this));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg_info_interface);
		//Get the bundle from the home page and parse for the RegionalCode
		String temp = getIntent().getExtras().getString(HomePage.EXTRA_MESSAGE);
		regCode = temp.substring(0,temp.indexOf(";"));
		regName = temp.substring(temp.indexOf(";")+1);
		//Set the page navigation
		managePageNavigation();
		//Set menu visibility
		menuVisibility = 0;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Create the menu in the action bar for the interface
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_reg_interface, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Determine the current page and then set the corresponding sorting methods in the menu
		SubMenu sub = menu.findItem(R.id.action_sort).getSubMenu();
		if (menuVisibility==1) {
			sub.setGroupEnabled(R.id.stats, false); sub.setGroupVisible(R.id.stats, false);
			sub.setGroupEnabled(R.id.ranks, true);  sub.setGroupVisible(R.id.ranks, true);
		} else if (menuVisibility==2) {
			sub.setGroupEnabled(R.id.stats, true);  sub.setGroupVisible(R.id.stats, true);
			sub.setGroupEnabled(R.id.ranks, false);	sub.setGroupVisible(R.id.ranks, false);
		} else {
			sub.setGroupEnabled(R.id.stats, false);	sub.setGroupVisible(R.id.stats, false);
			sub.setGroupEnabled(R.id.ranks, false);	sub.setGroupVisible(R.id.ranks, false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//React to item being selected on menu bar
		Intent intent;
		switch(item.getItemId()) {
		case android.R.id.home: //Go back to the home page
			intent = new Intent(RegInfoInterface.this, HomePage.class);
			startActivity(intent);
			return true;
		case R.id.action_settings: //Open up the settings page
			intent = new Intent(getBaseContext(),SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh: //Refresh all teams			
			//Start the refresh thread to update the regional
			refresh task = new refresh();
			task.execute(true);
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
		case R.id.action_acronyms:
			AlertDialog.Builder adb2 = new AlertDialog.Builder(this);
		    adb2.setTitle("About");
		    if (Integer.parseInt(regCode.substring(0,4))==2014) adb2.setMessage(getResources().getString(R.string.acro14));
		    else adb2.setMessage(getResources().getString(R.string.acro13));
		    adb2.setPositiveButton("Ok", null);
		    adb2.show();
		    return true;
		case R.id.action_help: onCoachMark();
			return true;
		case R.id.sort_team: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(0); 
			return true;
		case R.id.sort_opr: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(1); 
			return true;
		case R.id.sort_aopr: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(2); 
			return true;
		case R.id.sort_topr: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(3); 
			return true;
		case R.id.sort_eopr: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(4); 
			return true;
		case R.id.sort_avg: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(5); 
			return true;
		case R.id.sort_max: ((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).statsSort(6); 
			return true;
		case R.id.sort_rteam: ((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).ranksSort(0);
			return true;
		case R.id.sort_rank: ((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).ranksSort(1);
			return true;
		case R.id.sort_auton: ((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).ranksSort(2);
			return true;
		case R.id.sort_teleop: ((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).ranksSort(3);
			return true;
		case R.id.sort_end: ((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).ranksSort(4);
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
	
	private class refresh extends AsyncTask <Boolean, String, String> {
		
		@Override
		protected String doInBackground(Boolean...params) {
			try {
				publishProgress("Teams"); 
				((TeamsFragment) mPagerAdapter.getRegisteredFragment(0)).populate(true);
				publishProgress("Stats");
				((StatsFragment) mPagerAdapter.getRegisteredFragment(2)).populate(true);
				publishProgress("Matches"); 
				((MatchesFragment) mPagerAdapter.getRegisteredFragment(3)).populate(false);
				publishProgress("Ranks"); 
				((RanksFragment) mPagerAdapter.getRegisteredFragment(1)).populate(false);
				publishProgress("Awards"); 
				((AwardsFragment) mPagerAdapter.getRegisteredFragment(4)).populate(true);
			} catch (Exception ex) { return getResources().getString(R.string.fUpdate); }
			return getResources().getString(R.string.tUpdate);
		}
		protected void onProgressUpdate(String...progress) { 
			actionBar.setTitle("Updating: " + progress[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			actionBar.setTitle(regCode);
			Toast.makeText(RegInfoInterface.this, "Updating/Update Complete", Toast.LENGTH_SHORT).show();
		}
	}
}
