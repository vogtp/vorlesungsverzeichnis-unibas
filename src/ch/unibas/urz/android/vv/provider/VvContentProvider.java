package ch.unibas.urz.android.vv.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import ch.unibas.urz.android.vv.provider.db.DB;
import ch.unibas.urz.android.vv.provider.db.DB.OpenHelper;
import ch.unibas.urz.android.vv.provider.db.DBBackendVvEntity;

public class VvContentProvider extends ContentProvider {

	public static final String AUTHORITY = "ch.unibas.urz.android.vorlesungsverzeichnis";

	private static final int VV_ENTITY = 1;

	private static final UriMatcher sUriMatcher;

	private OpenHelper openHelper;

	@Override
	public boolean onCreate() {
		openHelper = new OpenHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case VV_ENTITY:
			count = DBBackendVvEntity.delete(openHelper, uri, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case VV_ENTITY:
			return DBBackendVvEntity.getType(uri);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri ret;
		switch (sUriMatcher.match(uri)) {
		case VV_ENTITY:
			ret = DBBackendVvEntity.insert(openHelper, uri, initialValues);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return ret;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (sUriMatcher.match(uri)) {
		case VV_ENTITY:
			c = DBBackendVvEntity.query(openHelper, uri, projection, selection, selectionArgs, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sUriMatcher.match(uri)) {
		case VV_ENTITY:
			count = DBBackendVvEntity.update(openHelper, uri, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		notifyChange(uri);
		return count;
	}

	private void notifyChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, DB.VvEntity.CONTENT_ITEM_NAME, VV_ENTITY);
		sUriMatcher.addURI(AUTHORITY, DB.VvEntity.CONTENT_ITEM_NAME + "/#", VV_ENTITY);
	}

}
