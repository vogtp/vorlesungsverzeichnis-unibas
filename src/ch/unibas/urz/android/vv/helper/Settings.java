package ch.unibas.urz.android.vv.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	private static final long DAY_IN_MILLIES = 1000 * 60 * 60 * 24;
	private static final long WEEK_IN_MILLIES = DAY_IN_MILLIES * 7;
	private static final long MONTH_IN_MILLIES = DAY_IN_MILLIES * 30;
	private static Settings instance;
	private final Context ctx;

	public Settings(Context ctx) {
		super();
		this.ctx = ctx.getApplicationContext();
	}

	public static void initInstance(Context ctx) {
		if (instance == null) {
			instance = new Settings(ctx);
		}
	}

	public static Settings getInstance() {
		return instance;
	}

	protected SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public int getDetailsMaxLines() {
		return 3;
	}

	public long getUpdateFrequency() {
		long f = DAY_IN_MILLIES;
		try {
			String s =  getPreferences().getString("prefKeyUpdateIntervall", "1");
			if ("2".equals(s)) {
				f = WEEK_IN_MILLIES;
			} else if ("3".endsWith(s)) {
				f = MONTH_IN_MILLIES;
			}
		} catch (Exception e) {
			Logger.w("Cannot parse update frequency", e);
		}
		return f;
	}

}
