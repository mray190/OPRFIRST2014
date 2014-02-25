package net.TeamRUSH27.OPRFIRST;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards.AwardInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches.MatchInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks.RankInfoAdapter;
import net.TeamRUSH27.OPRFIRST.RegInfoStats.StatInfoAdapter;
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

public class TeamInfoInterface extends SherlockFragmentActivity {

	private ActionBar actionBar;
	private ViewPager mPager;
	private MenuItem menuitem;
	private String team;
	
	RegInfoTeams.InfoFragment teamFragment;
	RegInfoStats.TeamStatsFragment statsFragment;
	RegInfoRanks.TeamRanksFragment ranksFragment;
	RegInfoMatches.TeamMatchesFragment matchesFragment;
	RegInfoAwards.TeamAwardsFragment awardsFragment;
	
	private void managePageNavigation(String title) {
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(title);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Bundle bundl = new Bundle();
		bundl.putString("team", title);
		
		mPager = (ViewPager) findViewById(R.id.teamPager);
        FragmentManager fm = getSupportFragmentManager();
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        };
        mPager.setOnPageChangeListener(pageChangeListener);
        TeamPagerAdapter fragmentPagerAdapter = new TeamPagerAdapter(fm,team);
        mPager.setAdapter(fragmentPagerAdapter);
		
		ActionBar.Tab tab1 = actionBar.newTab().setText("Info");
		teamFragment = new RegInfoTeams.InfoFragment();
		tab1.setTabListener(new MyTabsListener(teamFragment));
		teamFragment.setArguments(bundl);
		actionBar.addTab(tab1);
		
		ActionBar.Tab tab2 = actionBar.newTab().setText("Rankings");
		ranksFragment = new RegInfoRanks.TeamRanksFragment();
		tab2.setTabListener(new MyTabsListener(ranksFragment));
		ranksFragment.setArguments(bundl);
		actionBar.addTab(tab2);
		
		ActionBar.Tab tab3 = actionBar.newTab().setText("Stats");
		statsFragment = new RegInfoStats.TeamStatsFragment();
		tab3.setTabListener(new MyTabsListener(statsFragment));
		statsFragment.setArguments(bundl);
		actionBar.addTab(tab3);
		
		ActionBar.Tab tab4 = actionBar.newTab().setText("Matches");
		matchesFragment = new RegInfoMatches.TeamMatchesFragment();
		tab4.setTabListener(new MyTabsListener(matchesFragment));
		matchesFragment.setArguments(bundl);
		actionBar.addTab(tab4);
		
		ActionBar.Tab tab5 = actionBar.newTab().setText("Awards");
		awardsFragment = new RegInfoAwards.TeamAwardsFragment();
		tab5.setTabListener(new MyTabsListener(awardsFragment));
		awardsFragment.setArguments(bundl);
		actionBar.addTab(tab5);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_interface);
		team = getIntent().getExtras().getString(HomePage.EXTRA_MESSAGE);
		managePageNavigation(team);
		refresh task = new refresh();
		task.execute(false);
	}
	
	class MyTabsListener implements ActionBar.TabListener {
		public MyTabsListener(Fragment fragment) {}
		public void onTabSelected(Tab tab, FragmentTransaction ft) { mPager.setCurrentItem(tab.getPosition()); }
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
        public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_reg_interface, menu);
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
			menuitem = item;
			menuitem.setActionView(R.layout.progressbar);
			refresh task = new refresh();
			task.execute(true);
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
				publishProgress("Ranks"); rAdapter = new RankInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_ranks, teamInfo.getTeamInfoRanks(params[0]));
	    		publishProgress("Stats"); sAdapter = new StatInfoAdapter(TeamInfoInterface.this, R.layout.row_reginfo_stats, teamInfo.getTeamInfoStats(params[0]));
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
			matchesFragment.setListAdapter(mAdapter);
			awardsFragment.setListAdapter(aAdapter);
			ranksFragment.setListAdapter(rAdapter);
			statsFragment.setListAdapter(sAdapter);
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
