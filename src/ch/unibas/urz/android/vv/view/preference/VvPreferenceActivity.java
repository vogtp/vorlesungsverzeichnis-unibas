package ch.unibas.urz.android.vv.view.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import ch.unibas.urz.android.vv.R;

public class VvPreferenceActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}

}
