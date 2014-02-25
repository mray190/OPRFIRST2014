package net.TeamRUSH27.OPRFIRST;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RegPagerAdapter extends FragmentPagerAdapter{
	 
    final int PAGE_COUNT = 5;
    private String regCode;
 
    public RegPagerAdapter(FragmentManager fm, String regCode) {
        super(fm);
        this.regCode = regCode;
    }
 
    @Override
    public Fragment getItem(int arg0) {
        Bundle data = new Bundle();
		data.putInt("current_page", arg0+1);
		data.putString("regCode", regCode);
        switch(arg0){
            case 0:
            	RegInfoTeams.TeamsFragment fragment4 = new RegInfoTeams.TeamsFragment();
                fragment4.setArguments(data);
                return fragment4;
            case 1:
            	RegInfoRanks.RanksFragment fragment2 = new RegInfoRanks.RanksFragment();
                fragment2.setArguments(data);
                return fragment2;
            case 2:
        		RegInfoStats.StatsFragment fragment3 = new RegInfoStats.StatsFragment();
                fragment3.setArguments(data);
                return fragment3;
        	case 3:
        		RegInfoMatches.MatchesFragment fragment = new RegInfoMatches.MatchesFragment();
                fragment.setArguments(data);
                return fragment;
            case 4:
        		RegInfoAwards.AwardsFragment fragment5 = new RegInfoAwards.AwardsFragment();
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