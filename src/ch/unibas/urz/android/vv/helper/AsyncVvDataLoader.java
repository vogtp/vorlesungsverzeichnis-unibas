package ch.unibas.urz.android.vv.helper;

import android.content.Context;
import android.os.AsyncTask;
import ch.unibas.urz.android.vv.access.JsonVvLoader;

public class AsyncVvDataLoader extends AsyncTask<Long, Object, Object> {

	private final LoaderCallback loaderCallback;

	public interface LoaderCallback {
		public Context getContext();

		public void loadingFinished();
	}

	public AsyncVvDataLoader(LoaderCallback loaderCallback) {
		super();
		this.loaderCallback = loaderCallback;
	}

	@Override
	protected Object doInBackground(Long... parent) {
		// FIXME check last update
		Context ctx = loaderCallback.getContext();
		JsonVvLoader.loadEntries(ctx, parent[0], parent[1]);
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		loaderCallback.loadingFinished();
		super.onPostExecute(result);
	}

}
