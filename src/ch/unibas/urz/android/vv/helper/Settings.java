package ch.unibas.urz.android.vv.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	public enum SearchType {
		ALL, STAFF, STUDENTS
	}

	private static final String TYPE_STAFF = "1";
	private static final String TYPE_STUDENTS = "2";

	public static final int APP_APPEARIANCE_UNIBAS_TURQUISE = 1;
	public static final int APP_APPEARIANCE_ANDROID = 2;

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

	public SearchType getSearchType() {
		String seachType = getPreferences().getString("prefKeySearchType", "0");
		if (TYPE_STAFF.equals(seachType)) {
			return SearchType.STAFF;
		} else if (TYPE_STUDENTS.equals(seachType)) {
			return SearchType.STUDENTS;
		}
		return SearchType.ALL;
	}

	public int getAppAppearance() {
		try {
			return Integer.parseInt(getPreferences().getString("prefKeyAppAppearance", "1"));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}
