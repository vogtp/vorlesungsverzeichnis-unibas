package ch.unibas.urz.android.vv.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

public class Formater {

	public static String formatLecturer(String jsonStr) {
		try {
			JSONArray lecturerArray = new JSONArray(jsonStr);
			StringBuilder lecturer = new StringBuilder();
			for (int i = 0; i < lecturerArray.length(); i++) {
				if (lecturer.length() > 0) {
					lecturer.append("\n");
				}
				JSONObject obj = lecturerArray.getJSONObject(i);
				// {"LECMAIL":"hans-peter.mathys@unibas.ch","LEC_LASTNAME":"Mathys","LEC_FIRSTNAME":"Hans-Peter"}
				lecturer.append(obj.getString("LEC_FIRSTNAME")).append(" ").append(obj.getString("LEC_LASTNAME"));
				lecturer.append(" (").append(obj.getString("LECMAIL")).append(")");
			}
			return lecturer.toString();
		} catch (JSONException e) {
			Logger.e("Cannot parse lecurer array", e);
			return "";
		}
	}

	public static String formatModule(String jsonStr) {
		try {
			StringBuilder module = new StringBuilder();
			JSONArray moduleArray = new JSONArray(jsonStr);
			for (int i = 0; i < moduleArray.length(); i++) {
				if (module.length() > 0) {
					module.append("\n");
				}
				JSONObject obj = moduleArray.getJSONObject(i);
				// {"MODUL":"Modul Judentum","STUDIENGANG":"BSF - Religionswissenschaft"}
				module.append(obj.getString("MODUL")).append(" (").append(obj.getString("STUDIENGANG")).append(")");
			}
			return module.toString();
		} catch (JSONException e) {
			Logger.e("Cannot parse module array", e);
			return "";
		}
	}

	public static String formatTimePlace(Context ctx, String jsonStr, String note) {
		try {
			StringBuilder timePlace = new StringBuilder();
			JSONArray timeArray = new JSONArray(jsonStr);
			for (int i = 0; i < timeArray.length(); i++) {
				if (timePlace.length() > 0) {
					timePlace.append("\n");
				}
				JSONObject obj = timeArray.getJSONObject(i);
				// {"ORT_ID":510,"WKD_ID":1,"DAY":"Montag","RAUM_ID":8150,"RAUM":"Seminarraum 106",
				// "STARTTIME":1.015E8,"ENDTIME":1.2E8,"ORT":"Kollegienhaus"}
				// Mittwoch, 08.15-10.00 Chemie, Organische , Grosser Hörsaal OC
				timePlace.append(obj.getString("DAY")).append(", ");
				timePlace.append(formatTime(ctx, obj.getLong("STARTTIME")));
				timePlace.append(" - ").append(formatTime(ctx, obj.getLong("ENDTIME")));
				timePlace.append("; ").append(obj.getString("ORT")).append(", ").append(obj.getString("RAUM"));
			}
			if (!TextUtils.isEmpty(note)) {
				timePlace.append("\n\n").append(note);
			}
			return timePlace.toString();
		} catch (Exception e) {
			Logger.e("Cannot parse timeplace array", e);
			return "";
		}
	}

	private static String formatTime(Context ctx, long time) throws ParseException, JSONException {
		String timeStr = Long.toString(time);
		if (time < 99999999) {
			timeStr = "0" + timeStr;
		}
		SimpleDateFormat parseFormat = new SimpleDateFormat("HHmmssss");
		return DateFormat.getTimeFormat(ctx).format(parseFormat.parse(timeStr));
	}

}
