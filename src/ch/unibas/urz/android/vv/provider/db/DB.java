package ch.unibas.urz.android.vv.provider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import ch.unibas.urz.android.vv.helper.Logger;
import ch.unibas.urz.android.vv.provider.VvContentProvider;

public interface DB {

	public static final String DATABASE_NAME = "vorlesungsverzeichnis-unibas";

	public static final String NAME_ID = "_id";
	public static final int INDEX_ID = 0;

	public static final String SELECTION_BY_ID = NAME_ID + "=?";

	public class OpenHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 2;

		private static final String CREATE_TRIGGERS_TABLE = "create table if not exists " + VvEntity.TABLE_NAME + " (" + DB.NAME_ID + " integer primary key, "
				+ VvEntity.NAME_ACS_ID + " long," + VvEntity.NAME_ACS_NUMBER + " text, " + VvEntity.NAME_ACS_CATEGORY + " text, " + VvEntity.NAME_ACS_TITLE + " text, "
				+ VvEntity.NAME_ACS_CREDITPOINTS + " int," + VvEntity.NAME_ACS_OTYPE + " text, " + VvEntity.NAME_ACS_OBJID + " long," + VvEntity.NAME_ACS_SORT + " long,"
				+ VvEntity.NAME_ACS_PARENT + " long," + VvEntity.NAME_PERIOD_ID + " long," + VvEntity.NAME_UPDATE_TIMESTAMP + " long, " + VvEntity.NAME_FAVORITE
				+ " int DEFAULT 0)";

		public OpenHelper(Context context) {
			super(context, DB.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TRIGGERS_TABLE);
			db.execSQL("create index idx_vventity_acs_id on " + VvEntity.TABLE_NAME + " (" + VvEntity.NAME_ACS_ID + "); ");
			db.execSQL("create index idx_vventity_acs_parent on " + VvEntity.TABLE_NAME + " (" + VvEntity.NAME_ACS_PARENT + "); ");
			db.execSQL("create index idx_vventity_favorites on " + VvEntity.TABLE_NAME + " (" + VvEntity.NAME_FAVORITE + "); ");
			Logger.i("Created tables ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			switch (oldVersion) {
			case 1:
				Logger.w("Upgrading to DB Version 2...");
				db.execSQL("alter table " + VvEntity.TABLE_NAME + " add column " + VvEntity.NAME_FAVORITE + " int DEFAULT 0;");
				db.execSQL("create index idx_vventity_favorites on " + VvEntity.TABLE_NAME + " (" + VvEntity.NAME_FAVORITE + "); ");
				// nobreak

			default:
				Logger.w("Finished DB upgrading!");
				break;
			}
		}
	}

	public interface VvEntity {

		public static final String TABLE_NAME = "entity";

		public static final String CONTENT_ITEM_NAME = "entity";
		public static String CONTENT_URI_STRING = "content://" + VvContentProvider.AUTHORITY + "/" + CONTENT_ITEM_NAME;
		public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

		static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + VvContentProvider.AUTHORITY + "." + CONTENT_ITEM_NAME;

		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + VvContentProvider.AUTHORITY + "." + CONTENT_ITEM_NAME;

		public static final String NAME_ACS_ID = "ACS_ID";
		public static final String NAME_ACS_NUMBER = "ACS_NUMBER";
		public static final String NAME_ACS_CATEGORY = "ACS_CATEGORY";
		public static final String NAME_ACS_TITLE = "ACS_TITLE";
		public static final String NAME_ACS_CREDITPOINTS = "ACS_CREDITPOINTS";
		public static final String NAME_ACS_OTYPE = "ACS_OTYPE";
		// E -> Event Vorlesung
		public static final String NAME_ACS_OBJID = "ACS_OBJID";
		public static final String NAME_ACS_SORT = "ACS_SORT";
		public static final String NAME_ACS_PARENT = "ACS_PARENT";
		public static final String NAME_PERIOD_ID = "PERIOD_ID";
		public static final String NAME_UPDATE_TIMESTAMP = "UPDATE_TIMESTAMP";
		public static final String NAME_FAVORITE = "FAVORITE";

		public static final int INDEX_ACS_ID = 1;
		public static final int INDEX_ACS_NUMBER = 2;
		public static final int INDEX_ACS_CATEGORY = 3;
		public static final int INDEX_ACS_TITLE = 4;
		public static final int INDEX_ACS_CREDITPOINTS = 5;
		public static final int INDEX_ACS_OTYPE = 6;
		public static final int INDEX_ACS_OBJID = 7;
		public static final int INDEX_ACS_SORT = 8;
		public static final int INDEX_ACS_PARENT = 9;
		public static final int INDEX_PERIOD_ID = 10;
		public static final int INDEX_UPDATE_TIMESTAMP = 11;
		public static final int INDEX_FAVORITE = 12;

		public static final String[] colNames = new String[] { NAME_ID, NAME_ACS_ID, NAME_ACS_NUMBER, NAME_ACS_CATEGORY, NAME_ACS_TITLE, NAME_ACS_CREDITPOINTS, NAME_ACS_OTYPE,
				NAME_ACS_OBJID, NAME_ACS_SORT, NAME_ACS_PARENT, NAME_PERIOD_ID, NAME_UPDATE_TIMESTAMP, NAME_FAVORITE };

		public static final String[] PROJECTION_DEFAULT = colNames;
		public static final String[] PROJECTION_LIST = colNames;
		public static final String[] PROJECTION_ACS_ID = new String[] { NAME_ACS_ID };

		public static final String SELECTION_BY_PARENT_PERIOD = VvEntity.NAME_PERIOD_ID + "=? and " + VvEntity.NAME_ACS_PARENT + "=?";
		public static final String SELECTION_BY_ACSID = VvEntity.NAME_ACS_ID + "=?";
		public static final String SELECTION_BY_PARENT_PERIOD_NOT_UPDATE = VvEntity.NAME_PERIOD_ID + "=? and " + VvEntity.NAME_ACS_PARENT + "=? and not "
				+ VvEntity.NAME_UPDATE_TIMESTAMP + "=?";
		public static final String SELECTION_FAVORITES = VvEntity.NAME_FAVORITE + ">0";

		public static final String SORTORDER_DEFAULT = NAME_ACS_SORT + " ASC";

		public static final String SORTORDER_REVERSE = NAME_ACS_SORT + " DESC";

		public static final String ACS_OTYPE_EVENT = "E";

		public static final long VV_FIXED_ENTRY_ACS_ID = -42;
		public static final String SELECTION_VV_FIXED_ENTRY = VvEntity.NAME_ACS_ID + "=" + VV_FIXED_ENTRY_ACS_ID;

	}

}