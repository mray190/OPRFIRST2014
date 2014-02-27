package net.TeamRUSH27.OPRFIRST;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards.AwardInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches.MatchInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks.RankInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoStats.StatInfoAdapter;
import net.TeamRUSH27.OPRFIRST.adapter.TeamPagerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class TeamInfoInterface extends SherlockFragmentActivity implements ActionBar.TabListener {

	private ViewPager mPager;
	private TeamPagerAdapter mPagerAdapter;
	private ActionBar actionBar;
	private MenuItem menuitem;
	private int menuVisibility;
	private String team;
	
	public void onTabSelected(Tab tab, FragmentTransaction ft) { 
		menuVisibility=tab.getPosition();
		supportInvalidateOptionsMenu(); 
		mPager.setCurrentItem(tab.getPosition()); 
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

	public void onTabReselected(Tab tab, FragmentTransaction ft) { }
	
	private void managePageNavigation(String title) {
        mPager = (ViewPager) findViewById(R.id.teamPager);
		//Sets an action bar with tabs
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
        FragmentManager fm = getSupportFragmentManager();
		//Creates a Pager view and sets a listener and adapter to monitor swipes
		
		mPagerAdapter = new TeamPagerAdapter(fm, title);
		mPager.setAdapter(mPagerAdapter);
		
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { 
		    public void onPageSelected(int position) { actionBar.setSelectedNavigationItem(position); }
		    public void onPageScrolled(int arg0, float arg1, int arg2) { }
		    public void onPageScrollStateChanged(int arg0) { }
		});
		
		//Create the tabs and fragments
		ActionBar.Tab tab1 = actionBar.newTab().setText(getResources().getString(R.string.tab0));
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
		setContentView(R.layout.activity_team_interface);
		team = getIntent().getExtras().getString(HomePage.EXTRA_MESSAGE);
		managePageNavigation(team);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		Intent intent;
		switch(item.getItemId()) {
		case android.R.id.home:
			intent = new Intent(TeamInfoInterface.this, HomePage.class);
			startActivity(intent);
			return true;
		case R.id.action_settings:
			intent = new Intent(getBaseContext(),SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			//menuitem = item;
			//menuitem.setActionView(R.layout.progressbar);
			//refresh task = new refresh();
			//task.execute(true);
			return true;
		case R.id.action_help:
			onCoachMark();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressWarnings("deprecation")
	public void onCoachMark(){
	    final Dialog dialog = new Dialog(this);
	    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    dialog.setContentView(R.layout.activity_help_screen);
	    dialog.setCanceledOnTouchOutside(true);
	    View masterView = dialog.findViewById(R.id.helpLayout);
	    masterView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
	    });
	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.show();
	    dialog.getWindow().setAttributes(lp);
	}
	
	class refresh extends AsyncTask <Boolean, String, String> {
		RegInfoMatches.MatchInfoAdapter mAdapter;
		RegInfoAwards.AwardInfoAdapter aAdapter;
		RegInfoRanks.RankInfoAdapter rAdapter;
		RegInfoStats.StatInfoAdapter sAdapter;
		private boolean setRefreshIcon;
		@Override
		protected String doInBackground(Boolean...params) {
			try {
				setRefreshIcon = params[0];
				TeamInfo teamInfo = new TeamInfo(getApplicationContext(),team);
				publishProgress("Ranks"); rAdapter = new RankInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_ranks, null, 2014);
	    		publishProgress("Stats"); sAdapter = new StatInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_stats, null, 2014);
	    		publishProgress("Matches"); mAdapter = new MatchInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_matches, teamInfo.getTeamInfoMatches(params[0]));
	    		publishProgress("Awards"); aAdapter = new AwardInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_awards, teamInfo.getTeamInfoAwards(params[0]));
	    		if (teamInfo.getError()) return getResources().getString(R.string.fUpdate);
			} catch (Exception ex) { ex.printStackTrace();  return getResources().getString(R.string.fUpdate);}
			return getResources().getString(R.string.tUpdate);
		}
		protected void onProgressUpdate(String...progress) { actionBar.setTitle("Updating: " + progress[0]); }
		@Override
		protected void onPostExecute(String result) {
			actionBar.setTitle(team);
			if (setRefreshIcon) {
				AlertDialog.Builder adb = new AlertDialog.Builder(TeamInfoInterface.this);
			    adb.setTitle(getResources().getString(R.string.updateS));
			    adb.setMessage(result);
			    adb.setPositiveButton("Ok", null);
			    adb.show();
				menuitem.collapseActionView();
				menuitem.setActionView(null);
			}
		}
	}
}
