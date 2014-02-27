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

public class RegInfoMatches {

	private int match;
	private int redTeam1, redTeam2, redTeam3, blueTeam1, blueTeam2, blueTeam3, redScore, blueScore;
	private String time;
	
	public RegInfoMatches(String[] data) {
		int i=0;
		time = data[0];
		if (data.length==11) {
			time=data[1];
			i++;
		}
		match = Integer.parseInt(data[i+1]);
		redTeam1 = Integer.parseInt(data[i+2]);
		redTeam2 = Integer.parseInt(data[i+3]);
		redTeam3 = Integer.parseInt(data[i+4]);
		blueTeam1 = Integer.parseInt(data[i+5]);
		blueTeam2 = Integer.parseInt(data[i+6]);
		blueTeam3 = Integer.parseInt(data[i+7]);
		redScore = Integer.parseInt(data[i+8]);
		blueScore = Integer.parseInt(data[i+9]);
	}
	
	public String getTime() { return time; }
	public String getMatch() { return Integer.toString(match); }
	public int getRed1() { return redTeam1; }
	public int getRed2() { return redTeam2; }
	public int getRed3() { return redTeam3; }
	public int getBlue1() { return blueTeam1; }
	public int getBlue2() { return blueTeam2; }
	public int getBlue3() { return blueTeam3; }
	public int getRedScore() { return redScore; }
	public int getBlueScore() { return blueScore; }
	
	public boolean checkForTeam(int teamNumber) {
        boolean condition=false;
        if (redTeam1==teamNumber || redTeam2==teamNumber || redTeam3==teamNumber || blueTeam1==teamNumber || blueTeam2==teamNumber || blueTeam3==teamNumber) condition=true;
        return condition;
    }
    
    public int[] getMatchData(int teamNumber) {
        int[] data = new int[5];
        if (redTeam1==teamNumber || redTeam2==teamNumber || redTeam3==teamNumber) {
            data[0]=redScore;
            data[1]=blueScore;
            data[2]=redTeam1;
            data[3]=redTeam2;
            data[4]=redTeam3;
        } else {
            data[0]=blueScore;
            data[1]=redScore;
            data[2]=blueTeam1;
            data[3]=blueTeam2;
            data[4]=blueTeam3;
        }
        return data;
    }
	
    public static class MatchInfoAdapter extends ArrayAdapter<RegInfoMatches>{
		Context context;
		int layoutResourceId;
		RegInfoMatches data[] = null;
		
		public MatchInfoAdapter(Context context, int layoutResourceId, RegInfoMatches[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RegInfoMatchesHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RegInfoMatchesHolder();
				holder.timeTxt = (TextView)row.findViewById(R.id.timeTxt);
				holder.matchTxt = (TextView)row.findViewById(R.id.matchTxt);
				holder.red1Txt = (TextView)row.findViewById(R.id.red1Txt);
				holder.red2Txt = (TextView)row.findViewById(R.id.red2Txt);
				holder.red3Txt = (TextView)row.findViewById(R.id.red3Txt);
				holder.blue1Txt = (TextView)row.findViewById(R.id.blue1Txt);
				holder.blue2Txt = (TextView)row.findViewById(R.id.blue2Txt);
				holder.blue3Txt = (TextView)row.findViewById(R.id.blue3Txt);
				holder.redScore = (TextView)row.findViewById(R.id.redScore);
				holder.blueScore = (TextView)row.findViewById(R.id.blueScore);
				row.setTag(holder);
			} else {
				holder = (RegInfoMatchesHolder)row.getTag();
			}
			RegInfoMatches mR = data[position];
			holder.timeTxt.setText(mR.time);
			holder.matchTxt.setText(Integer.toString(mR.match));
			holder.red1Txt.setText(Integer.toString(mR.redTeam1));
			holder.red2Txt.setText(Integer.toString(mR.redTeam2));
			holder.red3Txt.setText(Integer.toString(mR.redTeam3));
			holder.blue1Txt.setText(Integer.toString(mR.blueTeam1));
			holder.blue2Txt.setText(Integer.toString(mR.blueTeam2));
			holder.blue3Txt.setText(Integer.toString(mR.blueTeam3));
			if (mR.redScore==0 && mR.blueScore==0) {
				holder.redScore.setText(" ");
				holder.blueScore.setText(" ");
			} else {
				holder.redScore.setText(Integer.toString(mR.redScore));
				holder.blueScore.setText(Integer.toString(mR.blueScore));
			}
			return row;
		}
		
		static class RegInfoMatchesHolder {
			TextView timeTxt, matchTxt, red1Txt, red2Txt, red3Txt, blue1Txt, blue2Txt, blue3Txt, redScore, blueScore;
		}
	}
    
    public static class MatchesFragment extends SherlockListFragment {
    	private RegInfo regInfo;
    	private MatchInfoAdapter mAdapter;
		private RegInfoMatches[] matches;
		private String year;
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    year = getArguments().getString("regCode").substring(0,4);
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
    				matches = regInfo.getRegInfoMatches(params[0]);
    				mAdapter = new MatchInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_matches, matches);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) {  setListAdapter(mAdapter); }
    	}
    	
    	class openPredictor extends AsyncTask <Integer, Integer, String[]> { 
    		@Override 
    		protected String[] doInBackground(Integer...params) { 
    			regInfo.getRegInfoStats(false);
    			int[] teams = new int[6];
    			for (int i=0; i<teams.length; i++) { teams[i] = params[i]; }
    			return regInfo.getStatsForPredict(year, teams);
    		} 
    		@Override
    		protected void onPostExecute(String[] result) {
    			Intent intent = new Intent(getActivity().getApplicationContext(), MatchPredictor.class);
        		intent.putExtra(HomePage.EXTRA_MESSAGE, result);
        		startActivity(intent);
    		}
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		openPredictor run = new openPredictor();
    		run.execute(matches[position].getRed1(),matches[position].getRed2(),matches[position].getRed3(),matches[position].getBlue1(),matches[position].getBlue2(),matches[position].getBlue3());
    	}
    }
    
    public static class TeamMatchesFragment extends SherlockListFragment {
    	private MatchInfoAdapter mAdapter;
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    //mAdapter = new MatchInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_matches, null);
    	    //setListAdapter(mAdapter);
    	}
    	@Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setCacheColorHint(Color.TRANSPARENT);
        }
    	public void populate(boolean update) {
    		calc task = new calc();
    		task.execute(update);
    	}
    	class calc extends AsyncTask <Boolean, Integer, String> {
    		@Override
    		protected String doInBackground(Boolean...params) {
    			try { 
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) { }
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		Intent intent = new Intent(getActivity().getApplicationContext(), RegInfoInterface.class);
    		intent.putExtra(HomePage.EXTRA_MESSAGE, "");
        	startActivity(intent);
    	}
    }
    
}
