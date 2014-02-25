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

public class RegInfoRanks implements Comparable<RegInfoRanks> {

	private String team;
	private int autonPts, teleopPts, climbPts, rank, qualifyingPts;
	private String record;
	int sortMethod;
	String[] data;
	
	public RegInfoRanks(String[] data) {
		this.data=data;
		rank = Integer.parseInt(data[0]);
		team = data[1];
		qualifyingPts = (int)Double.parseDouble(data[2]);
		autonPts = (int)Double.parseDouble(data[3]);
		climbPts = (int)Double.parseDouble(data[4]);
		teleopPts = (int)Double.parseDouble(data[5]);
		if (data[7].indexOf("-")!=-1) record = data[7];
        else record = data[6];
	}

	public String[] toWrite() { return data; }
	public int getTeam() { return Integer.parseInt(team); }
	public int getAutonPts() { return autonPts; }
	public int getTeleopPts() { return teleopPts; }
	public int getClimbPts() { return climbPts; }
	public void setSortMethod(int sortMethod) { this.sortMethod = sortMethod;}
	public void setTeam(String team) { this.team = team; }
	
	/*
	 * Conditions:
	 * (0) Team (1) Rank (2) Qualifying pts (3) Record (4) Auton Pts (5) Teleop Pts (6) Climb Pts
	 */
	public int compareTo(RegInfoRanks compareTeam) {
    	if (sortMethod==0) {
    		int compareQuantity = Integer.parseInt(((RegInfoRanks) compareTeam).team);
    		return Integer.parseInt(this.team) - compareQuantity; 
    	} else if (sortMethod==1) {
    		int compareQuantity = ((RegInfoRanks) compareTeam).rank;
    		return this.rank - compareQuantity; 
    	} else if (sortMethod==2) {
    		int compareQuantity = ((RegInfoRanks) compareTeam).qualifyingPts;
    		return compareQuantity - this.qualifyingPts; 
    	} else if (sortMethod==3) {
    		int compareQuantity = Integer.parseInt(((RegInfoRanks)compareTeam).record.replaceAll("-",""));
    		return compareQuantity-Integer.parseInt(this.record.replaceAll("-",""));
    	} else if (sortMethod==4) {
    		int compareQuantity = ((RegInfoRanks) compareTeam).autonPts;
    		return compareQuantity-this.autonPts; 
    	} else if (sortMethod==5) {
    		int compareQuantity = ((RegInfoRanks) compareTeam).teleopPts;
    		return compareQuantity-this.teleopPts; 
    	} else {
    		int compareQuantity = ((RegInfoRanks) compareTeam).climbPts;
    		return compareQuantity-this.climbPts; 
    	}
	}
	
	public static class RankInfoAdapter extends ArrayAdapter<RegInfoRanks>{
		Context context;
		int layoutResourceId;
		RegInfoRanks data[] = null;
		
		public RankInfoAdapter(Context context, int layoutResourceId, RegInfoRanks[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RegInfoRankHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RegInfoRankHolder();
				holder.teamTxt = (TextView)row.findViewById(R.id.teamTxt);
				holder.rankTxt = (TextView)row.findViewById(R.id.rankTxt);
				holder.recordTxt = (TextView)row.findViewById(R.id.recordTxt);
				holder.qsTxt = (TextView)row.findViewById(R.id.qsTxt);
				holder.autonTxt = (TextView)row.findViewById(R.id.autonTxt);
				holder.teleopTxt = (TextView)row.findViewById(R.id.teleopTxt);
				holder.climbTxt = (TextView)row.findViewById(R.id.climbTxt);
				row.setTag(holder);
			} else {
				holder = (RegInfoRankHolder)row.getTag();
			}
			RegInfoRanks rD = data[position];
			holder.teamTxt.setText(rD.team);
			holder.rankTxt.setText(Integer.toString(rD.rank));
			holder.recordTxt.setText(rD.record);
			holder.qsTxt.setText(Integer.toString(rD.qualifyingPts));
			holder.autonTxt.setText(Integer.toString(rD.autonPts));
			holder.teleopTxt.setText(Integer.toString(rD.teleopPts));
			holder.climbTxt.setText(Integer.toString(rD.climbPts));
			return row;
		}
		
		static class RegInfoRankHolder {
			TextView teamTxt, rankTxt, recordTxt, qsTxt, autonTxt, teleopTxt, climbTxt;
		}
	}
	
	public static class RanksFragment extends SherlockListFragment {
    	private RegInfo regInfo;
    	private RankInfoAdapter rAdapter;
    	private RegInfoRanks[] ranks;
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    		regInfo = new RegInfo(getActivity().getApplicationContext(),getArguments().getString("regCode"));
    		calc task = new calc();
    		task.execute();
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
    	class calc extends AsyncTask <String, Integer, String> {
    		@Override
    		protected String doInBackground(String...params) {
    			try { 
    				ranks = regInfo.getRegInfoRanks(false);
    				rAdapter = new RankInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_ranks, ranks);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) { setListAdapter(rAdapter); }
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		Intent intent = new Intent(getActivity().getApplicationContext(), TeamInfoInterface.class);
    		intent.putExtra(HomePage.EXTRA_MESSAGE, Integer.toString(ranks[position].getTeam()));
        	startActivity(intent);
    	}
    }

	public static class TeamRanksFragment extends SherlockListFragment {
    	private RankInfoAdapter rAdapter;
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    RegInfoRanks[] ranks = new RegInfoRanks[0];
    	    rAdapter = new RankInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_ranks, ranks);
    	    setListAdapter(rAdapter);
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
    }
}
