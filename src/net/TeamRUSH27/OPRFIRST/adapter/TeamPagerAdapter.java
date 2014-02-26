package net.TeamRUSH27.OPRFIRST.adapter;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks;
import net.TeamRUSH27.OPRFIRST.RegInfoStats;
import net.TeamRUSH27.OPRFIRST.RegInfoTeams;
import net.TeamRUSH27.OPRFIRST.RegInfoAwards.TeamAwardsFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches.TeamMatchesFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks.TeamRanksFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoStats.TeamStatsFragment;
import net.TeamRUSH27.OPRFIRST.RegInfoTeams.InfoFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TeamPagerAdapter extends FragmentPagerAdapter{
	 
    final int PAGE_COUNT = 5;
    private String team;
 
    public TeamPagerAdapter(FragmentManager fm, String team) {
        super(fm);
        this.team = team;
    }
 
    @Override
    public Fragment getItem(int arg0) {
        Bundle data = new Bundle();
		data.putInt("current_page", arg0+1);
		data.putString("team", team);
        switch(arg0){
            case 0:
            	RegInfoTeams.InfoFragment fragment4 = new RegInfoTeams.InfoFragment();
                fragment4.setArguments(data);
                return fragment4;
            case 1:
            	RegInfoRanks.TeamRanksFragment fragment2 = new RegInfoRanks.TeamRanksFragment();
                fragment2.setArguments(data);
                return fragment2;
            case 2:
        		RegInfoStats.TeamStatsFragment fragment3 = new RegInfoStats.TeamStatsFragment();
                fragment3.setArguments(data);
                return fragment3;
        	case 3:
        		RegInfoMatches.TeamMatchesFragment fragment = new RegInfoMatches.TeamMatchesFragment();
                fragment.setArguments(data);
                return fragment;
            case 4:
        		RegInfoAwards.TeamAwardsFragment fragment5 = new RegInfoAwards.TeamAwardsFragment();
                fragment5.setArguments(data);
                return fragment5;
        }
        return null;
    }
    
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}