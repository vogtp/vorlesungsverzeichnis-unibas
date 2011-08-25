package ch.unibas.urz.android.vv.access;

import android.content.Context;
import android.os.AsyncTask;

public class AsyncVvDataLoader extends AsyncTask<Object, Object, Object> {

	private final LoaderCallback loaderCallback;
	long periodId = 0;
	long id = -1;
	boolean detail = false;

	public interface LoaderCallback {
		public Context getContext();

		public void loadingFinished();
	}

	public AsyncVvDataLoader(LoaderCallback loaderCallback) {
		super();
		this.loaderCallback = loaderCallback;
	}

	@Override
	protected Object doInBackground(Object... notUsed) {
		// FIXME check last update
		Context ctx = loaderCallback.getContext();
		if (isDetail()) {
			JsonVvLoader.loadDetails(ctx, getPeriodId(), getId());
		} else {
			JsonVvLoader.loadEntries(ctx, getPeriodId(), getId());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		loaderCallback.loadingFinished();
		super.onPostExecute(result);
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isDetail() {
		return detail;
	}

	public void setDetail(boolean detail) {
		this.detail = detail;
	}
}
