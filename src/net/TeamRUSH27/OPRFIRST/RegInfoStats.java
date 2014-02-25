package net.TeamRUSH27.OPRFIRST;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

public class RegInfoStats implements Comparable<RegInfoStats> {

	private String team;
	private int totalPts, oppTotalPts, maxScore, matchesPlayed, sortMethod;
	private ArrayList<Integer> teamsPlayedWith;
	private double OPR, DPR, teleopOPR, autonOPR, climbOPR, avgPts;
	
	public RegInfoStats(int team) {
        this.team = Integer.toString(team);
        maxScore=-1; totalPts=0; matchesPlayed=0; oppTotalPts=0;
        teamsPlayedWith=new ArrayList<Integer>();
        OPR=0; avgPts=0; autonOPR=0; teleopOPR=0; climbOPR=0; DPR=0;
    }
	
	public void setTeam(String team) { this.team = team;}
    
    public RegInfoStats(String[] data) {
        team = data[0];
        maxScore = Integer.parseInt(data[1]);
        totalPts = Integer.parseInt(data[2]);
        avgPts = Double.parseDouble(data[3]);
        OPR = Double.parseDouble(data[4]);
        DPR = Double.parseDouble(data[5]);
        autonOPR = Double.parseDouble(data[6]);
        teleopOPR = Double.parseDouble(data[7]);
        climbOPR = Double.parseDouble(data[8]);
    }
    
    public void setOPRs(double opr, double dpr, double hpopr, double tpopr, double ppopr) {
        this.OPR = opr;
        this.DPR = dpr;
        this.autonOPR = hpopr;
        this.teleopOPR = tpopr;
        this.climbOPR = ppopr;
    }
    
    public int[] getOPRs() {
    	int[] data = {(int)OPR,(int)autonOPR,(int)teleopOPR,(int)climbOPR};
    	return data;
    }
    
    public int[] getTeamsPlayedWith() {
        int[] array = new int[teamsPlayedWith.size()];
        for (int i=0; i<array.length; i++) { array[i] = teamsPlayedWith.get(i); }
        return array;
    }
    
    public void calcAvgScore() { avgPts = ((double)totalPts)/matchesPlayed; }
    public int getTtlPts() { return totalPts; }
    public int getOppScore() { return oppTotalPts; }
	public void setSortMethod(int sortMethod) { this.sortMethod = sortMethod;}
	public String getTeam() {return team;}
    
    public void setMatchInfo(int[] info) {
        matchesPlayed++;
        if (info[0]>maxScore) maxScore=info[0];
        totalPts+=info[0];
        oppTotalPts+=info[1];
        for (int i=2; i<5; i++) { teamsPlayedWith.add(info[i]); }
    }
    
    public int compareTo(RegInfoStats compareTeam) {
    	if (sortMethod==0) {
    		int compareQuantity = Integer.parseInt(((RegInfoStats) compareTeam).team);
    		return Integer.parseInt(this.team) - compareQuantity; 
    	} else if (sortMethod==1) {
    		int compareQuantity = ((RegInfoStats) compareTeam).maxScore;
    		return compareQuantity - this.maxScore; 
    	} else {
    		double compareQuantity, thisQuantity;
    		if (sortMethod==2) {
    			compareQuantity = ((RegInfoStats) compareTeam).OPR;
    			thisQuantity = this.OPR;
    		} else if (sortMethod==3) {
    			compareQuantity = ((RegInfoStats) compareTeam).autonOPR;
    			thisQuantity = this.autonOPR;
    		} else if (sortMethod==4) {
    			compareQuantity = ((RegInfoStats) compareTeam).teleopOPR;
    			thisQuantity = this.teleopOPR;
    		} else {
    			compareQuantity = ((RegInfoStats) compareTeam).climbOPR;
    			thisQuantity = this.climbOPR;
    		}
    		if (compareQuantity>thisQuantity) return 1;
    		else if (compareQuantity<thisQuantity) return -1;
    		else return 0; 
    	}
	}
    
    public String[] toWrite() {
        String[] data = new String[9];
        data[0] = team;
        data[1] = Integer.toString(maxScore);
        data[2] = Integer.toString(totalPts);
        data[3] = Double.toString(avgPts);
        data[4] = Double.toString(OPR);
        data[5] = Double.toString(DPR);
        data[6] = Double.toString(autonOPR);
        data[7] = Double.toString(teleopOPR);
        data[8] = Double.toString(climbOPR);
        return data;
    }
    
    public static class StatInfoAdapter extends ArrayAdapter<RegInfoStats>{
		Context context;
		int layoutResourceId;
		RegInfoStats data[] = null;
		DecimalFormat df = new DecimalFormat("0.00");
		
		public StatInfoAdapter(Context context, int layoutResourceId, RegInfoStats[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RegInfoStatHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RegInfoStatHolder();
				holder.teamTxt = (TextView)row.findViewById(R.id.teamTxt);
				holder.oprTxt = (TextView)row.findViewById(R.id.maxScoreTxt);
				holder.maxScoreTxt = (TextView)row.findViewById(R.id.oprTxt);
				holder.avgPtsTxt = (TextView)row.findViewById(R.id.avgPtsTxt);
				holder.autonOPRTxt = (TextView)row.findViewById(R.id.autonOPRTxt);
				holder.teleopOPRTxt = (TextView)row.findViewById(R.id.teleopOPRTxt);
				holder.climbOPRTxt = (TextView)row.findViewById(R.id.climbOPRTxt);
				row.setTag(holder);
			} else {
				holder = (RegInfoStatHolder)row.getTag();
			}
			RegInfoStats rS = data[position];
			holder.teamTxt.setText(rS.team);
			holder.oprTxt.setText(Integer.toString(rS.maxScore));
			holder.maxScoreTxt.setText(df.format(rS.OPR));
			holder.avgPtsTxt.setText(df.format(rS.avgPts));
			holder.autonOPRTxt.setText(df.format(rS.autonOPR));
			holder.teleopOPRTxt.setText(df.format(rS.teleopOPR));
			holder.climbOPRTxt.setText(df.format(rS.climbOPR));
			return row;
		}
		
		static class RegInfoStatHolder {
			TextView teamTxt, oprTxt, maxScoreTxt, avgPtsTxt, autonOPRTxt, teleopOPRTxt, climbOPRTxt;
		}
	}
    
    public static class StatsFragment extends SherlockListFragment {
    	private RegInfo regInfo;
    	private StatInfoAdapter sAdapter;
		private RegInfoStats[] stats;
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
    				stats = regInfo.getRegInfoStats(false);
    				sAdapter = new StatInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_stats, stats);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) { setListAdapter(sAdapter); }
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		Intent intent = new Intent(getActivity().getApplicationContext(), TeamInfoInterface.class);
    		intent.putExtra(HomePage.EXTRA_MESSAGE, stats[position].getTeam());
        	startActivity(intent);
    	}
    }
	
    public static class TeamStatsFragment extends SherlockListFragment {
    	private StatInfoAdapter sAdapter;
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    RegInfoStats[] stats = new RegInfoStats[0];
    	    sAdapter = new StatInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_stats, stats);
    	    setListAdapter(sAdapter);
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
    }
}
