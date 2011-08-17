package ch.unibas.urz.android.vv.application;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import ch.unibas.urz.android.vv.R;
import ch.unibas.urz.android.vv.helper.Logger;
import ch.unibas.urz.android.vv.provider.db.DB.VvEntity;

public class VvApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(VvEntity.CONTENT_URI, VvEntity.PROJECTION_ACS_ID, VvEntity.SELECTION_VV_FIXED_ENTRY, null, VvEntity.SORTORDER_DEFAULT);
			if (cursor != null) {
				if (!cursor.moveToFirst()) {
					ContentValues values = new ContentValues();
					values.put(VvEntity.NAME_ACS_ID, VvEntity.VV_FIXED_ENTRY_ACS_ID);
					values.put(VvEntity.NAME_ACS_TITLE, getString(R.string.label_vv) );
					values.put(VvEntity.NAME_FAVORITE, 1);
					// values.put(VvEntity.NAME_, );
					// values.put(VvEntity.NAME_, );
					contentResolver.insert(VvEntity.CONTENT_URI, values);
				}
				cursor.close();
			}
		} catch (Exception e) {
			Logger.w("Cannot init DB", e);
		}
	}

}
