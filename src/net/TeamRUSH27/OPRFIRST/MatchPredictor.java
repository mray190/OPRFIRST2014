package net.TeamRUSH27.OPRFIRST;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MatchPredictor extends SherlockFragmentActivity {
	
	private ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_match_predictor);
		actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.title_activity_match_predictor));
		actionBar.setDisplayHomeAsUpEnabled(true);
		String[] data = getIntent().getExtras().getStringArray(HomePage.EXTRA_MESSAGE);
		TextView[] tvs = new TextView[22];
		int[] ids = {R.id.egy1,R.id.egy2,R.id.redTeam1,R.id.redOpr1,R.id.redTeam2,R.id.redOpr2,R.id.redTeam3,R.id.redOpr3,
				R.id.blueTeam1,R.id.blueOpr1,R.id.blueTeam2,R.id.blueOpr2,R.id.blueTeam3,R.id.blueOpr3,
				R.id.redScore,R.id.redAuton,R.id.redTeleop,R.id.redEnd,
				R.id.blueScore,R.id.blueAuton,R.id.blueTeleop,R.id.blueEnd};
		for (int i=0; i<ids.length; i++) {
			tvs[i] = (TextView)findViewById(ids[i]);
			if (i==0 || i==1) {
				if (data[i].equals("2014")) tvs[i].setText(getResources().getString(R.string.matchPredictEnd14));
				else tvs[i].setText(getResources().getString(R.string.matchPredictEnd13));
			} else tvs[i].setText(data[i]);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Create the menu in the action bar for the home page
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_home_page, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//React to item being selected on menu bar
		switch(item.getItemId()) {
		case android.R.id.home: //Go back to the home page
			finish();
			return true;
		case R.id.action_settings: //Open the settings panel
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.action_about: //Displays the current information about the app
			String vName="", vCode="";
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				vName = pInfo.versionName;
				vCode = Integer.toString(pInfo.versionCode);
			} catch (NameNotFoundException e) {	}
			//Creates a dialog box to display to the users
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
		    adb.setTitle("About");
		    adb.setMessage(getResources().getString(R.string.app_name) + "\nVersion: " + vName + " (" + vCode + ")\n" + R.string.author);
		    adb.setPositiveButton("Ok", null);
		    adb.show();
		    return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
