/**
 * OPR FIRST 2013
 * RegInfoInterface.java
 * Last Edit: 7/9/13 2:47PM
 * Michael Ray
 * Interface for viewing stats of a regional. Implements a pager view
 * Changes to be made:
 */
package net.TeamRUSH27.OPRFIRST;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards.AwardInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches.MatchInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks.RankInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoStats.StatInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoTeams.TeamInfoAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

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
public class RegInfoInterface extends SherlockFragmentActivity {

	private ActionBar actionBar;
	private ViewPager mPager;
	private String regCode, regName;
	private MenuItem menuitem;
	
	private RegInfo regInfo;
	private RegInfoTeams.TeamsFragment teamFragment;
	private RegInfoStats.StatsFragment statsFragment;
	private RegInfoRanks.RanksFragment ranksFragment;
	private RegInfoMatches.MatchesFragment matchesFragment;
	private RegInfoAwards.AwardsFragment awardsFragment;
	
	private int menuVisibility;
	
	/*
	 * Manages the navigation, fragments and panels for swipey tabs and action bar menus
	 * PreCondtion: Title has been determined and a view exists to put the content
	 * @param Title a string representing the title of the interface
	 * @return void
	 */
	private void managePageNavigation(String title) {
		//Sets an action bar with tabs
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
		//Creates a Pager view and sets a listener and adapter to monitor swipes
		Bundle bundl = new Bundle();
		bundl.putString("regCode", title);
		mPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        };
        mPager.setOnPageChangeListener(pageChangeListener);
        RegPagerAdapter fragmentPagerAdapter = new RegPagerAdapter(fm,regCode);
        mPager.setAdapter(fragmentPagerAdapter);
		//Create the tabs and fragments
		ActionBar.Tab tab1 = actionBar.newTab().setText(getResources().getString(R.string.tab1));
		teamFragment = new RegInfoTeams.TeamsFragment();
		tab1.setTabListener(new MyTabsListener(teamFragment));
		teamFragment.setArguments(bundl);
		actionBar.addTab(tab1);
		ActionBar.Tab tab2 = actionBar.newTab().setText(getResources().getString(R.string.tab2));
		ranksFragment = new RegInfoRanks.RanksFragment();
		tab2.setTabListener(new MyTabsListener(ranksFragment));
		ranksFragment.setArguments(bundl);
		actionBar.addTab(tab2);
		ActionBar.Tab tab3 = actionBar.newTab().setText(getResources().getString(R.string.tab3));
		statsFragment = new RegInfoStats.StatsFragment();
		tab3.setTabListener(new MyTabsListener(statsFragment));
		statsFragment.setArguments(bundl);
		actionBar.addTab(tab3);
		ActionBar.Tab tab4 = actionBar.newTab().setText(getResources().getString(R.string.tab4));
		matchesFragment = new RegInfoMatches.MatchesFragment();
		tab4.setTabListener(new MyTabsListener(matchesFragment));
		matchesFragment.setArguments(bundl);
		actionBar.addTab(tab4);
		ActionBar.Tab tab5 = actionBar.newTab().setText(getResources().getString(R.string.tab5));
		awardsFragment = new RegInfoAwards.AwardsFragment();
		tab5.setTabListener(new MyTabsListener(awardsFragment));
		awardsFragment.setArguments(bundl);
		actionBar.addTab(tab5);
		
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
		managePageNavigation(regName);
		//Set menu visibility
		menuVisibility = 0;
	}
	
	private class MyTabsListener implements ActionBar.TabListener {
		public MyTabsListener(Fragment fragment) {}
		public void onTabSelected(Tab tab, FragmentTransaction ft) { 
			//Set the current item in the pager to the tab in the current position
			mPager.setCurrentItem(tab.getPosition()); 
			//Set the menu position based on the pager position and update the menu
			menuVisibility=tab.getPosition();
			supportInvalidateOptionsMenu(); 
			}
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
        public void onTabReselected(Tab tab, FragmentTransaction ft) {}
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
			//Set the refresh button to the progressbar layout, which is a spinning wheel
			menuitem = item;
			menuitem.setActionView(R.layout.progressbar);
			//Start the refresh thread to update the regional
			refresh task = new refresh();
			task.execute();
			return true;
		case R.id.action_help: //Displays the help screen
			onCoachMark();
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
	
	private class refresh extends AsyncTask <Void, String, String> {
		private RegInfoMatches.MatchInfoAdapter mAdapter;
		private RegInfoRanks.RankInfoAdapter rAdapter;
		private RegInfoStats.StatInfoAdapter sAdapter;
		private RegInfoTeams.TeamInfoAdapter tAdapter;
		private RegInfoAwards.AwardInfoAdapter aAdapter;
		
		@Override
		protected String doInBackground(Void...params) {
			try {
				regInfo = new RegInfo(getApplicationContext(),regCode);
				publishProgress("Stats"); sAdapter = new StatInfoAdapter(RegInfoInterface.this, R.layout.row_reginfo_stats, regInfo.getRegInfoStats(true));
				publishProgress("Matches"); mAdapter = new MatchInfoAdapter(RegInfoInterface.this, R.layout.row_reginfo_matches, regInfo.getRegInfoMatches(false));
				publishProgress("Ranks"); rAdapter = new RankInfoAdapter(RegInfoInterface.this, R.layout.row_reginfo_ranks, regInfo.getRegInfoRanks(false));
				publishProgress("Awards"); aAdapter = new AwardInfoAdapter(RegInfoInterface.this, R.layout.row_reginfo_awards, regInfo.getRegInfoAwards(true)); 
				publishProgress("Teams"); tAdapter = new TeamInfoAdapter(RegInfoInterface.this, R.layout.row_reginfo_teams, regInfo.getRegInfoTeams(getResources().openRawResource(R.raw.allteams),true));
				if (regInfo.getError()) return getResources().getString(R.string.fUpdate);
			} catch (Exception ex) { return getResources().getString(R.string.fUpdate); }
			return getResources().getString(R.string.tUpdate);
		}
		protected void onProgressUpdate(String...progress) { actionBar.setTitle("Updating: " + progress[0]); }
		@Override
		protected void onPostExecute(String result) {
			actionBar.setTitle(regCode);
			teamFragment.setListAdapter(tAdapter);
			ranksFragment.setListAdapter(rAdapter);
			statsFragment.setListAdapter(sAdapter);
			matchesFragment.setListAdapter(mAdapter);
			awardsFragment.setListAdapter(aAdapter);
			AlertDialog.Builder adb = new AlertDialog.Builder(RegInfoInterface.this);
		    adb.setTitle(getResources().getString(R.string.updateS));
		    adb.setMessage(result);
		    adb.setPositiveButton("Ok", null);
		    adb.show();
			menuitem.collapseActionView();
			menuitem.setActionView(null);
		}
	}
}
