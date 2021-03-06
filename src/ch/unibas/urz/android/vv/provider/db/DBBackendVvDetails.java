package ch.unibas.urz.android.vv.provider.db;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ch.unibas.urz.android.vv.provider.VvContentProvider;
import ch.unibas.urz.android.vv.provider.db.DB.OpenHelper;
import ch.unibas.urz.android.vv.provider.db.DB.VvDetails;

public class DBBackendVvDetails {

	private static HashMap<String, String> sVirtualGovernorProjectionMap;

	private static final int ENTITY = 1;
	private static final int ENTITY_ID = 2;

	private static final UriMatcher sUriMatcher;

	public static int delete(OpenHelper openHelper, Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			count = db.delete(VvDetails.TABLE_NAME, selection, selectionArgs);
			break;

		case ENTITY_ID:
			String id = uri.getPathSegments().get(1);
			count = db.delete(VvDetails.TABLE_NAME, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			return VvDetails.CONTENT_TYPE;

		case ENTITY_ID:
			return VvDetails.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	public static Cursor query(OpenHelper openHelper, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(VvDetails.TABLE_NAME);
		qb.setProjectionMap(sVirtualGovernorProjectionMap);
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			break;

		case ENTITY_ID:
			qb.appendWhere(DB.NAME_ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = VvDetails.SORTORDER_DEFAULT;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		return c;
	}

	public static int update(OpenHelper openHelper, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ENTITY:
			count = db.update(VvDetails.TABLE_NAME, values, selection, selectionArgs);
			break;

		case ENTITY_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(VvDetails.TABLE_NAME, values, DB.NAME_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public static Uri insert(OpenHelper openHelper, Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != ENTITY) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(VvDetails.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(VvDetails.CONTENT_URI, rowId);
			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(VvContentProvider.AUTHORITY, VvDetails.CONTENT_ITEM_NAME, ENTITY);
		sUriMatcher.addURI(VvContentProvider.AUTHORITY, VvDetails.CONTENT_ITEM_NAME + "/#", ENTITY_ID);

	}
}
