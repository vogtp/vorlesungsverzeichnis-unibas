package ch.unibas.urz.android.vv.helper;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class MenuHelper {

	public static boolean onOptionsItemSelected(Activity act, MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.itemSearch:
		// act.onSearchRequested();
		// return true;
		// case R.id.itemSettings:
		// Intent i = new Intent(act, SettingsActivity.class);
		// act.startActivity(i);
		// return true;

		default:
			return false;

		}
	}

	public static void onCreateOptionsMenu(Activity act, Menu menu) {
		// act.getMenuInflater().inflate(R.menu.options_general, menu);
	}

}
