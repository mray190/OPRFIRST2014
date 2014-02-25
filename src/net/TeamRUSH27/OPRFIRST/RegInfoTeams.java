package net.TeamRUSH27.OPRFIRST;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;

public class RegInfoTeams {
	
	private int team;
	private String name, sponsors, location, website;
	private ArrayList<String> regionals;
	
	public RegInfoTeams(String[] data) {
        team = Integer.parseInt(data[0]);
        name = data[1];
        sponsors = data[2];
        location = data[3];
        website = data[4];
        regionals = new ArrayList<String>();
	}
	
	public String getTeam() { return Integer.toString(team); }
	public String getName() { return name; }
	public String getSponsors() { return sponsors; }
	public String getLocation() { return location; }
	public String getWebsite() { return website; }
	public void setRegionals(ArrayList<String> regionals) { this.regionals = regionals; }
	public ArrayList<String> getRegionals() { return this.regionals; }
	
	public static class TeamInfoAdapter extends ArrayAdapter<RegInfoTeams>{
		Context context;
		int layoutResourceId;
		RegInfoTeams data[] = null;
		
		public TeamInfoAdapter(Context context, int layoutResourceId, RegInfoTeams[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RegInfoTeamHolder holder = null;
			
			if (row==null) {
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new RegInfoTeamHolder();
				holder.numberTxt = (TextView)row.findViewById(R.id.numberTTxt);
				holder.nameTxt = (TextView)row.findViewById(R.id.nameTTxt);
				holder.locationTxt = (TextView)row.findViewById(R.id.locationTTxt);
				row.setTag(holder);
			} else {
				holder = (RegInfoTeamHolder)row.getTag();
			}
			RegInfoTeams rT = data[position];
			holder.numberTxt.setText(Integer.toString(rT.team));
			holder.nameTxt.setText(rT.name);
			holder.locationTxt.setText(rT.location);
			return row;
		}
		
		static class RegInfoTeamHolder {
			TextView numberTxt, nameTxt, locationTxt;
		}
	}
	
	public static class TeamsFragment extends SherlockListFragment {
    	private RegInfo regInfo;
    	private TeamInfoAdapter tAdapter;
    	private RegInfoTeams[] teams;
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    		regInfo = new RegInfo(getActivity().getApplicationContext(),getArguments().getString("regCode"));
    		calc task = new calc();
    		task.execute();
    	}
    	class calc extends AsyncTask <String, Integer, String> {
    		@Override
    		protected String doInBackground(String...params) {
    			try { 
    				teams = regInfo.getRegInfoTeams(getResources().openRawResource(R.raw.allteams),false);
    				tAdapter = new TeamInfoAdapter(getSherlockActivity(), R.layout.row_reginfo_teams, teams);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) { setListAdapter(tAdapter); }
    	}
    	@Override
    	public void onListItemClick(ListView l, View v, int position, long id) {
    		Intent intent = new Intent(getActivity().getApplicationContext(), TeamInfoInterface.class);
    		intent.putExtra(HomePage.EXTRA_MESSAGE, teams[position].getTeam());
        	startActivity(intent);
    	}
    }
	
	public static class InfoFragment extends SherlockFragment {
		private SharedPreferences prefs;
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			calc task = new calc();
    		task.execute();
	        return inflater.inflate(R.layout.row_teaminfo, container, false);
	    }
		
    	class calc extends AsyncTask <String, Integer, String> {
    		private TeamInfo teaminfo;
    		private RegInfoTeams team;
    		@Override
    		protected String doInBackground(String...params) {
    			try {
    				teaminfo = new TeamInfo(getActivity().getApplicationContext(),getArguments().getString("team"));
    				team = teaminfo.getTeamInfo(getResources().openRawResource(R.raw.allteams),true);
    			} catch (Exception ex) {}
    			return null;
    		}
    		@Override
    		protected void onPostExecute(String result) {
    			TextView name = (TextView)getActivity().findViewById(R.id.name);
    			TextView location = (TextView)getActivity().findViewById(R.id.location);
    			TextView sponsors = (TextView)getActivity().findViewById(R.id.sponsors);
    			TextView website = (TextView)getActivity().findViewById(R.id.website);
    			name.setText(team.getName());
    			location.setText(team.getLocation());
    			sponsors.setText(team.getSponsors());
    			website.setText(team.getWebsite());
    		}
    	}
    }
}
