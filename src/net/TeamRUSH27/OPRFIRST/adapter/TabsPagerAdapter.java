package net.TeamRUSH27.OPRFIRST.adapter;

import net.TeamRUSH27.OPRFIRST.RegInfoAwards;
import net.TeamRUSH27.OPRFIRST.RegInfoMatches;
import net.TeamRUSH27.OPRFIRST.RegInfoRanks;
import net.TeamRUSH27.OPRFIRST.RegInfoStats;
import net.TeamRUSH27.OPRFIRST.RegInfoTeams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
	private String regCode;
	private FragmentManager fm;
	
	public TabsPagerAdapter(FragmentManager fm, String regCode) {
        super(fm);
        this.fm = fm;
        this.regCode = regCode;
    }
 
    @Override
    public Fragment getItem(int position) {
    	Bundle data = new Bundle();
		data.putInt("current_page", position+1);
		data.putString("regCode", regCode);
        switch(position){
            case 0:
            	RegInfoTeams.TeamsFragment frag1 = new RegInfoTeams.TeamsFragment();
                frag1.setArguments(data);
                return frag1;
            case 1:
            	RegInfoRanks.RanksFragment frag2 = new RegInfoRanks.RanksFragment();
                frag2.setArguments(data);
                return frag2;
            case 2:
            	RegInfoStats.StatsFragment frag3 = new RegInfoStats.StatsFragment();
                frag3.setArguments(data);
                return frag3;
        	case 3:
        		RegInfoMatches.MatchesFragment frag4 = new RegInfoMatches.MatchesFragment();
                frag4.setArguments(data);
                return frag4;
            case 4:
        		RegInfoAwards.AwardsFragment frag5 = new RegInfoAwards.AwardsFragment();
                frag5.setArguments(data);
                return frag5;
        }
        return null;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
    
    public Fragment getActiveFragment(ViewPager container, int position) {
    	String name = makeFragmentName(container.getId(), position);
    	return  fm.findFragmentByTag(name);
	}

	private static String makeFragmentName(int viewId, int index) {
	    return "android:switcher:" + viewId + ":" + index;
	}
 
    @Override
    public int getCount() {
        return 5;
    }
}
