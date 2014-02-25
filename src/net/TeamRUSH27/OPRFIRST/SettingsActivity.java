package net.TeamRUSH27.OPRFIRST;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
	
public class SettingsActivity extends SherlockPreferenceActivity {
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Settings");
		actionBar.setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//React to item being selected on menu bar
		Intent intent;
		switch(item.getItemId()) {
		case android.R.id.home: //Go back to the home page
			intent = new Intent(SettingsActivity.this, HomePage.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}