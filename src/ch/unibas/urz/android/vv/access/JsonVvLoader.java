package ch.unibas.urz.android.vv.access;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.unibas.urz.android.vv.helper.Logger;
import ch.unibas.urz.android.vv.helper.Settings;
import ch.unibas.urz.android.vv.provider.VvContentProvider;
import ch.unibas.urz.android.vv.provider.db.DB;

public class JsonVvLoader {

	private static final String JSON_URL = "http://urz-cfaa.urz.unibas.ch/dominik/vv_online/json.cfm";

	private static String loadData(String url) throws Exception {
		URL aUrl = new URL(url);
		URLConnection conn = aUrl.openConnection();
		conn.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static void loadEntries(Context ctx, long periodId, long parentId) {
		long now = System.currentTimeMillis();
		ContentResolver contentResolver = ctx.getContentResolver();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		try {
			StringBuilder url = new StringBuilder(JSON_URL);
			url.append("?parent_id=").append(parentId);
			if (periodId > 0) {
				url.append("&period_id=").append(periodId);
			}
			JSONArray data = new JSONArray(loadData(url.toString()));
			for (int i = 0; i < data.length(); i++) {
				JSONObject object = data.getJSONObject(i);
				long acsId = object.getLong(DB.VvEntity.NAME_ACS_ID);
				String[] acsIdSelectionArgs = new String[] { Long.toString(acsId) };
				boolean exists = false;
				Cursor c = contentResolver.query(DB.VvEntity.CONTENT_URI, DB.VvEntity.PROJECTION_ACS_ID, DB.VvEntity.SELECTION_BY_ACSID, acsIdSelectionArgs, null);
				if (c != null) {
					exists = c.moveToFirst();
					c.close();
				}

				Builder insertOrUpdate;
				if (exists) {
					insertOrUpdate = ContentProviderOperation.newUpdate(DB.VvEntity.CONTENT_URI);
					insertOrUpdate.withSelection(DB.VvEntity.SELECTION_BY_ACSID, acsIdSelectionArgs);
				} else {
					insertOrUpdate = ContentProviderOperation.newInsert(DB.VvEntity.CONTENT_URI);
				}

				ContentValues values = new ContentValues();
				values.put(DB.VvEntity.NAME_PERIOD_ID, periodId);
				values.put(DB.VvEntity.NAME_UPDATE_TIMESTAMP, now);
				values.put(DB.VvEntity.NAME_ACS_ID, acsId);
				values.put(DB.VvEntity.NAME_ACS_NUMBER, object.getString(DB.VvEntity.NAME_ACS_NUMBER));
				values.put(DB.VvEntity.NAME_ACS_CATEGORY, object.getString(DB.VvEntity.NAME_ACS_CATEGORY));
				values.put(DB.VvEntity.NAME_ACS_TITLE, object.getString(DB.VvEntity.NAME_ACS_TITLE));
				values.put(DB.VvEntity.NAME_ACS_CREDITPOINTS, object.getInt(DB.VvEntity.NAME_ACS_CREDITPOINTS));
				values.put(DB.VvEntity.NAME_ACS_OTYPE, object.getString(DB.VvEntity.NAME_ACS_OTYPE));
				values.put(DB.VvEntity.NAME_ACS_OBJID, object.getLong(DB.VvEntity.NAME_ACS_OBJID));
				values.put(DB.VvEntity.NAME_ACS_SORT, object.getLong(DB.VvEntity.NAME_ACS_SORT));
				values.put(DB.VvEntity.NAME_ACS_PARENT, object.getLong(DB.VvEntity.NAME_ACS_PARENT));

				insertOrUpdate.withValues(values);
				operations.add(insertOrUpdate.build());
				Logger.i("Found entity: " + object.getString(DB.VvEntity.NAME_ACS_TITLE));
			}

			Builder delete = ContentProviderOperation.newDelete(DB.VvEntity.CONTENT_URI);
			delete.withSelection(DB.VvEntity.SELECTION_BY_PARENT_PERIOD_NOT_UPDATE, new String[] { Long.toString(periodId), Long.toString(parentId), Long.toString(now) });
			operations.add(delete.build());
			contentResolver.applyBatch(VvContentProvider.AUTHORITY, operations);
		} catch (Exception e) {
			Logger.e("Cannot get VV entity info from the network", e);
		}

	}

	public static void loadDetails(Context ctx, long periodId, long acsObjId) {
		long now = System.currentTimeMillis();
		ContentResolver contentResolver = ctx.getContentResolver();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		try {
			StringBuilder url = new StringBuilder(JSON_URL);
			url.append("?acs_objid=").append(acsObjId);
			if (periodId > 0) {
				url.append("&period_id=").append(periodId);
			}
			JSONObject baseObject = new JSONObject(loadData(url.toString()));
			JSONObject object = baseObject.getJSONArray("event").getJSONObject(0);

			long acsId = object.getLong(DB.VvDetails.NAME_ACS_OBJ_ID);
			String[] acsIdSelectionArgs = new String[] { Long.toString(acsId) };
			boolean exists = false;
			Cursor c = contentResolver.query(DB.VvDetails.CONTENT_URI, DB.VvDetails.PROJECTION_ACS_ID, DB.VvDetails.SELECTION_BY_ACSID, acsIdSelectionArgs, null);
			if (c != null) {
				exists = c.moveToFirst();
				c.close();
			}

			Builder insertOrUpdate;
			if (exists) {
				insertOrUpdate = ContentProviderOperation.newUpdate(DB.VvDetails.CONTENT_URI);
				insertOrUpdate.withSelection(DB.VvDetails.SELECTION_BY_ACSID, acsIdSelectionArgs);
			} else {
				insertOrUpdate = ContentProviderOperation.newInsert(DB.VvDetails.CONTENT_URI);
			}

			ContentValues values = new ContentValues();
			values.put(DB.VvDetails.NAME_PERIOD_ID, periodId);
			values.put(DB.VvDetails.NAME_UPDATE_TIMESTAMP, now);
			values.put(DB.VvDetails.NAME_ACS_OBJ_ID, acsId);
			values.put(DB.VvDetails.NAME_TITEL, object.getString(DB.VvDetails.NAME_TITEL));
			values.put(DB.VvDetails.NAME_INHALT, object.getString(DB.VvDetails.NAME_INHALT));
			values.put(DB.VvDetails.NAME_AMUSTER, object.getString(DB.VvDetails.NAME_AMUSTER));
			values.put(DB.VvDetails.NAME_OEINHEIT, object.getString(DB.VvDetails.NAME_OEINHEIT));
			values.put(DB.VvDetails.NAME_NOTETIME, object.getString(DB.VvDetails.NAME_NOTETIME));
			values.put(DB.VvDetails.NAME_INTERVAL, object.getString(DB.VvDetails.NAME_INTERVAL));
			values.put(DB.VvDetails.NAME_STARTDATE, object.getLong(DB.VvDetails.NAME_STARTDATE));
			values.put(DB.VvDetails.NAME_ENDDATE, object.getLong(DB.VvDetails.NAME_ENDDATE));
			values.put(DB.VvDetails.NAME_FAKULTAT, object.getString(DB.VvDetails.NAME_FAKULTAT));
			values.put(DB.VvDetails.NAME_USPRACHE, object.getString(DB.VvDetails.NAME_USPRACHE));
			values.put(DB.VvDetails.NAME_SKALA, object.getString(DB.VvDetails.NAME_SKALA));
			values.put(DB.VvDetails.NAME_TVORAUSSETZUNG, object.getString(DB.VvDetails.NAME_TVORAUSSETZUNG));
			values.put(DB.VvDetails.NAME_WBELEGEN, object.getString(DB.VvDetails.NAME_WBELEGEN));
			values.put(DB.VvDetails.NAME_ANABMELDUNG, object.getString(DB.VvDetails.NAME_ANABMELDUNG));
			values.put(DB.VvDetails.NAME_CREDITP, object.getString(DB.VvDetails.NAME_CREDITP));
			values.put(DB.VvDetails.NAME_LPRUEF, object.getString(DB.VvDetails.NAME_LPRUEF));
			values.put(DB.VvDetails.NAME_WIEDERHOLUNGPRUEF, object.getString(DB.VvDetails.NAME_WIEDERHOLUNGPRUEF));
			values.put(DB.VvDetails.NAME_PRAESENZ, object.getString(DB.VvDetails.NAME_PRAESENZ));
			values.put(DB.VvDetails.NAME_BEMERKUNG, object.getString(DB.VvDetails.NAME_BEMERKUNG));
			values.put(DB.VvDetails.NAME_TYPE, object.getString(DB.VvDetails.NAME_TYPE));
			values.put(DB.VvDetails.NAME_LERNZIELE, object.getString(DB.VvDetails.NAME_LERNZIELE));
			values.put(DB.VvDetails.NAME_HNOTE, object.getString(DB.VvDetails.NAME_HNOTE));
			values.put(DB.VvDetails.NAME_LINK, object.getString(DB.VvDetails.NAME_LINK));
			values.put(DB.VvDetails.NAME_LINKDESC, object.getString(DB.VvDetails.NAME_LINKDESC));


			// special fields
			StringBuilder module = new StringBuilder();
			JSONArray moduleArray = baseObject.getJSONArray("module");
			for (int i = 0; i < moduleArray.length(); i++) {
				if (module.length() > 0) {
					module.append("\n");
				}
				JSONObject obj = moduleArray.getJSONObject(i);
				// {"MODUL":"Modul Judentum","STUDIENGANG":"BSF - Religionswissenschaft"}
				module.append(obj.getString("MODUL")).append("(").append(obj.getString("STUDIENGANG")).append(")");
			}
			values.put(DB.VvDetails.NAME_MODULE, module.toString());

			StringBuilder lecturer = new StringBuilder();
			JSONArray lecturerArray = baseObject.getJSONArray("lecturer");
			for (int i = 0; i < lecturerArray.length(); i++) {
				if (lecturer.length() > 0) {
					lecturer.append("\n");
				}
				JSONObject obj = lecturerArray.getJSONObject(i);
				// {"LECMAIL":"hans-peter.mathys@unibas.ch","LEC_LASTNAME":"Mathys","LEC_FIRSTNAME":"Hans-Peter"}
				lecturer.append(obj.getString("LEC_FIRSTNAME")).append(" ").append(obj.getString("LEC_LASTNAME"));
				lecturer.append("(").append(obj.getString("LECMAIL")).append(")");
			}
			values.put(DB.VvDetails.NAME_LECTURER, lecturer.toString());

			StringBuilder timePlace = new StringBuilder();
			JSONArray timeArray = baseObject.getJSONArray("time");
			for (int i = 0; i < timeArray.length(); i++) {
				if (timePlace.length() > 0) {
					timePlace.append("\n");
				}
				JSONObject obj = timeArray.getJSONObject(i);
				// {"ORT_ID":510,"WKD_ID":1,"DAY":"Montag","RAUM_ID":8150,"RAUM":"Seminarraum 106",
				// "STARTTIME":1.015E8,"ENDTIME":1.2E8,"ORT":"Kollegienhaus"}
				// Mittwoch, 08.15-10.00 Chemie, Organische , Grosser Hörsaal OC
				timePlace.append(obj.getString("DAY")).append(", ");
				SimpleDateFormat sdf = Settings.getInstance().getTimeFomat();
				timePlace.append(sdf.format(new Date(obj.getLong("STARTTIME")))).append(" - ").append(sdf.format(new Date(obj.getLong("ENDTIME"))));
				timePlace.append("; ").append(obj.getString("ORT")).append(", ").append(obj.getString("RAUM"));
			}
			values.put(DB.VvDetails.NAME_TIME_PLACE, timePlace.toString());

			insertOrUpdate.withValues(values);
			operations.add(insertOrUpdate.build());
			Logger.i("Found detail: " + object.getString(DB.VvDetails.NAME_TITEL));

			contentResolver.applyBatch(VvContentProvider.AUTHORITY, operations);
		} catch (Exception e) {
			Logger.e("Cannot get VV details info from the network", e);
		}

	}

}
