package net.TeamRUSH27.OPRFIRST;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class RegInfoAwards {

	private int team;
	private String teamName, location, award, personalName;
	private String[] data;
	
	public RegInfoAwards(String[] data) {
		this.data = data;
		award = data[0];
        try { team = Integer.parseInt(data[1]);
        } catch (Exception e) { team=0; }
        teamName = data[2];
        personalName = data[3];
        location = data[4];
	}
	
	public int getTeam() { return team; }
	public String getTeamName() { return teamName; }
	public String getLocation() { return location; }
	public String getAward() { return award; }
	public String getName() { return personalName; }
	public void setLocation(String location) { this.location = location; }
	public String[] toWrite() { return data; }
	
	public static class AwardInfoAdapter extends ArrayAdapter<RegInfoAwards>{
		Context context;
		int layoutResourceId;
		RegInfoAwards data[] = null;
		
		public AwardInfoAdapter(Context context, int layoutResourceId, RegInfoAwards[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RegInfoAwardHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RegInfoAwardHolder();
				holder.numberATxt = (TextView)row.findViewById(R.id.numberATxt);
				holder.nameATxt = (TextView)row.findViewById(R.id.nameATxt);
				holder.awardTxt = (TextView)row.findViewById(R.id.awardTxt);
				row.setTag(holder);
			} else {
				holder = (RegInfoAwardHolder)row.getTag();
			}
			RegInfoAwards rA = data[position];
			holder.numberATxt.setText(Integer.toString(rA.team));
			holder.nameATxt.setText(rA.location);
			holder.awardTxt.setText(rA.award);
			return row;
		}
		
		static class RegInfoAwardHolder {
			TextView numberATxt, nameATxt, awardTxt;
		}
	}
	
	public static class AwardsFragment extends SherlockListFragment {
    	private RegInfo regInfo;
    	private AwardInfoAdapter aAdapter;
    	private RegInfoAwards[] awards;
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    		regInfo = new RegInfo(getActivity().getApplicationContext(),getArguments().getString("regCode"));
    		populate(false);
    	}
    	
    	public void populate(boolean update) {
    		calc task = new calc();
    		task.execute(update);
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
    	class calc extends AsyncTask <Boolean, Integer, String> {
    		@Override
    		protected String doInBackground(Boolean...params) {
    			try { 
    				awards = regInfo.getRegInfoAwards(params[0]);
    				aAdapter = new AwardInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_awards, awards);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) { setListAdapter(aAdapter); }
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		Intent intent = new Intent(getActivity().getApplicationContext(), TeamInfoInterface.class);
    		intent.putExtra(HomePage.EXTRA_MESSAGE, Integer.toString(awards[position].getTeam()));
        	startActivity(intent);
    	}
	}
    
	public static class TeamAwardsFragment extends SherlockListFragment {
    	private AwardInfoAdapter aAdapter;
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    RegInfoAwards[] awards = new RegInfoAwards[0];
    	    aAdapter = new AwardInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_awards, awards);
    	    setListAdapter(aAdapter);
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
	}
}