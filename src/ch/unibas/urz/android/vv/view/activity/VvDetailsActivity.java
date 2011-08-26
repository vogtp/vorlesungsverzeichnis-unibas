package ch.unibas.urz.android.vv.view.activity;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.unibas.urz.android.vv.R;
import ch.unibas.urz.android.vv.access.AsyncVvDataLoader;
import ch.unibas.urz.android.vv.access.AsyncVvDataLoader.LoaderCallback;
import ch.unibas.urz.android.vv.helper.Formater;
import ch.unibas.urz.android.vv.helper.Logger;
import ch.unibas.urz.android.vv.helper.Settings;
import ch.unibas.urz.android.vv.provider.db.DB.VvDetails;
import ch.unibas.urz.android.vv.view.widget.DetailsEntryView;

import com.markupartist.android.widget.ActionBar;

public class VvDetailsActivity extends Activity implements LoaderCallback {

	private Cursor detailsCursor;
	private final AsyncVvDataLoader dataloader = new AsyncVvDataLoader(this);
	private long periodId;
	private long acsObjId;
	private ActionBar actionBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.details);
		actionBar = (ActionBar) findViewById(R.id.actionBar1);
		actionBar.setTitle(R.string.app_title);

		Intent intent = getIntent();
		Uri uri = intent.getData();
		if (uri == null) {
			Toast.makeText(this, "No data reference..", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// long dbid = ContentUris.parseId(uri);
		periodId = intent.getLongExtra(VvMainActivity.EXTRA_PERIOD_ID, -1);
		acsObjId = intent.getLongExtra(VvMainActivity.EXTRA_ACS_OBJID, -1);
		dataloader.setDetail(true);
		dataloader.setPeriodId(periodId);
		dataloader.setId(acsObjId);
		detailsCursor = managedQuery(VvDetails.CONTENT_URI, VvDetails.PROJECTION_DEFAULT, VvDetails.SELECTION_BY_PERIOD_ACSID,
				new String[] { Long.toString(periodId), Long.toString(acsObjId) }, null);
		if (detailsCursor != null && detailsCursor.moveToFirst()) {
			displayData();
		}
		loadData();
	}


	private void displayData() {
		if (!detailsCursor.moveToFirst()) {
			Logger.i("No data to display");
			return;
		}
		TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		TextView tvType = (TextView) findViewById(R.id.tvType);
		LinearLayout llMain = (LinearLayout) findViewById(R.id.llMain);
		if (llMain.getChildCount() > 0) {
			llMain.removeAllViews();
		}
		tvInfo.setText(detailsCursor.getString(VvDetails.INDEX_TITEL));
		tvTitle.setText(detailsCursor.getString(VvDetails.INDEX_TITEL));
		StringBuilder sb = new StringBuilder();
		sb.append(detailsCursor.getString(VvDetails.INDEX_VNR));
		sb.append(" ");
		sb.append(detailsCursor.getString(VvDetails.INDEX_TYPE));
		tvType.setText(sb.toString());

		addDetailView(llMain, R.string.detailsDozierende, Formater.formatLecturer(detailsCursor.getString(VvDetails.INDEX_LECTURER)));
		addDetailView(llMain, R.string.detailsZeit, Formater.formatTimePlace(detailsCursor.getString(VvDetails.INDEX_TIME_PLACE)));
		addViewDate(llMain, R.string.detailsBeginndatum, VvDetails.INDEX_STARTDATE);
		addViewDate(llMain, R.string.detailsEnddatum, VvDetails.INDEX_ENDDATE);
		addDetailView(llMain, R.string.detailsTeilnahmebedingungen, VvDetails.INDEX_TVORAUSSETZUNG); // ???
		addDetailView(llMain, R.string.detailsAnmeldung, VvDetails.INDEX_WBELEGEN);
		// ???
		addDetailView(llMain, R.string.detailsIntervall, VvDetails.INDEX_INTERVAL);
		addDetailView(llMain, R.string.detailsAngebotsmuster, VvDetails.INDEX_AMUSTER);
		addDetailView(llMain, R.string.detailsOrganisationseinheit, VvDetails.INDEX_OEINHEIT);
		addDetailView(llMain, R.string.detailsModule, Formater.formatModule(detailsCursor.getString(VvDetails.INDEX_MODULE)));
		addDetailView(llMain, R.string.detailsLernziele, VvDetails.INDEX_LERNZIELE);
		addDetailView(llMain, R.string.detailsInhalt, VvDetails.INDEX_INHALT);
		addDetailView(llMain, R.string.detailsLiteratur, VvDetails.INDEX_LITERATUR);
		addViewLink(llMain, R.string.detailsWeblink, VvDetails.INDEX_LINK, VvDetails.INDEX_LINKDESC);
		addDetailView(llMain, R.string.detailsLeistungsüberprüfung, VvDetails.INDEX_LPRUEF);
		addDetailView(llMain, R.string.detailsSkala, VvDetails.INDEX_SKALA);
		addDetailView(llMain, R.string.detailsWiederholungspruefung, VvDetails.INDEX_WIEDERHOLUNGPRUEF);
		addDetailView(llMain, R.string.detailsAnAbmeldung, VvDetails.INDEX_ANABMELDUNG);
		addDetailView(llMain, R.string.detailsHinweise, VvDetails.INDEX_HNOTE);
		addDetailView(llMain, R.string.detailsFak, VvDetails.INDEX_FAKULTAT);
		addDetailView(llMain, R.string.detailsWiederholtesBelegen, VvDetails.INDEX_WBELEGEN);
		addDetailView(llMain, R.string.detailsEinsatzDigitalerMedien, VvDetails.INDEX_PRAESENZ);
		addDetailView(llMain, R.string.detailsUnterrichtssprache, VvDetails.INDEX_USPRACHE);
		addDetailView(llMain, R.string.detailsBemerkungen, VvDetails.INDEX_BEMERKUNG);
	}

	private void addViewLink(LinearLayout llMain, int label, int indexLink, int indexLinkdesc) {
		// // TODO Auto-generated method stub
		// String text = detailsCursor.getString(indexLinkdesc) + " -> " +
		// detailsCursor.getString(indexLink);
		// llMain.addView(new DetailsEntryView(this, llMain, label, text));
	}

	private void addViewDate(LinearLayout llMain, int label, int field) {
		String string = Settings.getInstance().getDateFormat().format(new Date(detailsCursor.getLong(field)));
		addDetailView(llMain, label, string);
	}

	private void addDetailView(LinearLayout llMain, int label, int field) {
		addDetailView(llMain, label, detailsCursor.getString(field));
		
	}

	private void addDetailView(LinearLayout llMain, int label, String field) {
		llMain.addView(new DetailsEntryView(this, label, field));
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void loadingFinished() {
		detailsCursor.requery();
		displayData();
		actionBar.setProgressBarVisibility(View.GONE);
	}

	private void loadData() {
		actionBar.setProgressBarVisibility(View.VISIBLE);
		dataloader.execute((Object[]) null);
	}
}
