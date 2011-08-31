package ch.unibas.urz.android.vv.helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import ch.unibas.urz.android.vv.R;
import ch.unibas.urz.android.vv.view.activity.VvMainActivity;
import ch.unibas.urz.android.vv.view.preference.VvPreferenceActivity;


public class GeneralMenuHelper {

	public static boolean onOptionsItemSelected(Context ctx, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.itemSettings:
			i = new Intent(ctx, VvPreferenceActivity.class);
			ctx.startActivity(i);
			return true;

		case R.id.itemHome:
			i = new Intent(ctx, VvMainActivity.class);
			ctx.startActivity(i);
			return true;
		default:
			return false;

		}

	}

}
