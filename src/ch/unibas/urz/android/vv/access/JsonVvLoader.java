package ch.unibas.urz.android.vv.access;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ch.unibas.urz.android.vv.helper.Logger;
import ch.unibas.urz.android.vv.provider.VvContentProvider;
import ch.unibas.urz.android.vv.provider.db.DB;

public class JsonVvLoader {

	private static final String JSON_URL = "http://urz-cfaa.urz.unibas.ch/dominik/vv_online/json.cfm";

	public static void loadEntries(Context ctx, long periodId, long parentId) {
		long now = System.currentTimeMillis();
		ContentResolver contentResolver = ctx.getContentResolver();
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		try {
			StringBuffer url = new StringBuffer(JSON_URL);
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
				Logger.i("Found: " + object.getString(DB.VvEntity.NAME_ACS_TITLE));
			}

			Builder delete = ContentProviderOperation.newDelete(DB.VvEntity.CONTENT_URI);
			delete.withSelection(DB.VvEntity.SELECTION_BY_PARENT_PERIOD_NOT_UPDATE, new String[] { Long.toString(periodId), Long.toString(parentId), Long.toString(now) });
			operations.add(delete.build());
			contentResolver.applyBatch(VvContentProvider.AUTHORITY, operations);
		} catch (Exception e) {
			Logger.e("Cannot get VV info from the network", e);
		}

	}

	// private static void insertOrUpdate(Context ctx, AppModel am) {
	// ContentResolver contentResolver = ctx.getContentResolver();
	// String[] selectionArgs = new String[] { Integer.toString(am.getAppId())
	// };
	// Cursor c = contentResolver.query(DB.DashboardApp.CONTENT_URI,
	// DB.DashboardApp.PROJECTION_DEFAULT, DB.DashboardApp.SELECTION_BY_APPID,
	// selectionArgs,
	// DB.DashboardApp.SORTORDER_DEFAULT);
	// try {
	// if (c.moveToFirst()) {
	// Logger.d("update " + am.getName());
	// am.setDbid(c.getLong(DB.INDEX_ID));
	// am.setHide(c.getInt(DB.DashboardApp.INDEX_HIDE));
	// contentResolver.update(DB.DashboardApp.CONTENT_URI, am.getValues(),
	// DB.DashboardApp.SELECTION_BY_APPID, selectionArgs);
	// } else {
	// Logger.d("insert " + am.getName());
	// contentResolver.insert(DB.DashboardApp.CONTENT_URI, am.getValues());
	// }
	// } catch (Throwable t) {
	// Logger.e("Fail to insert ", t);
	// }
	// }

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

}
