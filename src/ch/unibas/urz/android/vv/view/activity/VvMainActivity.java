package ch.unibas.urz.android.vv.view.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import ch.unibas.urz.android.vv.R;
import ch.unibas.urz.android.vv.access.AsyncVvDataLoader;
import ch.unibas.urz.android.vv.access.AsyncVvDataLoader.LoaderCallback;
import ch.unibas.urz.android.vv.provider.db.DB;
import ch.unibas.urz.android.vv.provider.db.DB.VvEntity;

import com.markupartist.android.widget.ActionBar;

public class VvMainActivity extends ListActivity implements LoaderCallback {
	private AsyncVvDataLoader dataloader;
	private final Long[] loaderValues = new Long[2];
	private final String[] selection = new String[2];
	private Cursor cursor;
	private Cursor parentCursor;
	private ActionBar actionBar;
	private SimpleCursorAdapter cursorAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.vventity_list);

		actionBar = (ActionBar) findViewById(R.id.actionBar1);
		actionBar.setTitle(R.string.app_title);
		
		actionBar.addAction(new ActionBar.IntentAction(this, new Intent(this, VvMainActivity.class), ch.unibas.urz.android.theme.R.drawable.home));

		// FIXME getdefault period id
		setPeriodId(-1);
		setParentId(0);

		Uri uri = getIntent().getData();
		if (uri != null) {
			long dbid = ContentUris.parseId(uri);
			parentCursor = managedQuery(VvEntity.CONTENT_URI, VvEntity.PROJECTION_DEFAULT, DB.SELECTION_BY_ID, new String[] { Long.toString(dbid) }, null);
			if (parentCursor != null && parentCursor.moveToFirst()) {
				long parentId = parentCursor.getLong(VvEntity.INDEX_ACS_ID);
				if (parentId == VvEntity.VV_FIXED_ENTRY_ACS_ID) {
					parentId = 0;
				}
				setParentId(parentId);
				setPeriodId(parentCursor.getLong(VvEntity.INDEX_PERIOD_ID));

				if (VvEntity.ACS_OTYPE_EVENT.equalsIgnoreCase(parentCursor.getString(VvEntity.INDEX_ACS_OTYPE))) {
					Toast.makeText(this, "Details not yet implemented", Toast.LENGTH_LONG).show();
					finish();
				}

				((TextView) findViewById(R.id.tvInfo)).setText(parentCursor.getString(VvEntity.INDEX_ACS_TITLE));
			} else {
				parentCursor = null;
			}
		}

		if (parentCursor == null) {
			cursor = managedQuery(VvEntity.CONTENT_URI, VvEntity.PROJECTION_DEFAULT, VvEntity.SELECTION_FAVORITES, null, VvEntity.SORTORDER_DEFAULT);
			((TextView) findViewById(R.id.tvInfo)).setText(R.string.msg_favorites);
		} else {
			cursor = managedQuery(VvEntity.CONTENT_URI, VvEntity.PROJECTION_DEFAULT, VvEntity.SELECTION_BY_PARENT_PERIOD, selection, VvEntity.SORTORDER_DEFAULT);
			dataloader = new AsyncVvDataLoader(this);
			loadData();
		}
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.vventity_item, cursor, new String[] { VvEntity.NAME_ACS_TITLE, VvEntity.NAME_ACS_NUMBER,
				VvEntity.NAME_ACS_CREDITPOINTS, VvEntity.NAME_FAVORITE }, new int[] { R.id.tvTitle, R.id.tvId, R.id.tvCredits, R.id.cbFavorite });

		cursorAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (columnIndex == VvEntity.INDEX_ACS_CREDITPOINTS) {
					int cp = cursor.getInt(VvEntity.INDEX_ACS_CREDITPOINTS);
					if (cp > 0) {
						StringBuilder sb = new StringBuilder();
						sb.append(getString(R.string.label_ects_points)).append(": ").append(cp);
						((TextView) view).setText(sb.toString());
					} else {
						((TextView) view).setText("");
					}
					return true;
				} else if (columnIndex == VvEntity.INDEX_FAVORITE) {
					final long acsId = cursor.getLong(VvEntity.INDEX_ACS_ID);
					final String title = cursor.getString(VvEntity.INDEX_ACS_TITLE);
					if (acsId != VvEntity.VV_FIXED_ENTRY_ACS_ID) {
						view.setVisibility(View.VISIBLE);
						CheckBox cbFavorite = (CheckBox) view;
						final boolean selected = cursor.getInt(VvEntity.INDEX_FAVORITE) > 0;
						cbFavorite.setChecked(selected);
						cbFavorite.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ContentValues values = new ContentValues();
								values.put(VvEntity.NAME_FAVORITE, !selected);
								getContentResolver().update(VvEntity.CONTENT_URI, values, VvEntity.SELECTION_BY_ACSID, new String[] { Long.toString(acsId) });
								// FIXME cache
							}
						});
					} else {
						view.setVisibility(View.INVISIBLE);
					}
					return true;
				}
				return false;
			}
		});
		getListView().setAdapter(cursorAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void setPeriodId(long periodId) {
		loaderValues[0] = periodId;
		selection[0] = Long.toString(periodId);
	}

	private void setParentId(long parentId) {
		loaderValues[1] = parentId;
		selection[1] = Long.toString(parentId);
	}

	private void loadData() {
		actionBar.setProgressBarVisibility(View.VISIBLE);
		dataloader.execute(loaderValues);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Uri uri = ContentUris.withAppendedId(VvEntity.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void loadingFinished() {
		cursor.requery();
		actionBar.setProgressBarVisibility(View.GONE);
	}
}