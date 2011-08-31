package ch.unibas.urz.android.vv.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.unibas.urz.android.vv.R;

public class MessageAdapter extends BaseAdapter {

	private static final String ITEM = "";
	private final LayoutInflater inflater;
	private final int msgRes;
	private final boolean showProgress;
	
	public MessageAdapter(Activity act, int msgRes, boolean showProgress) {
		super();
		this.msgRes = msgRes;
		this.showProgress = showProgress;
		inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return ITEM;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = (convertView != null) ? convertView : createView(parent);
		TextView tvMsg = (TextView) view.findViewById(R.id.tvMsg);
		tvMsg.setText(msgRes);
		ProgressBar pbMsg = (ProgressBar) view.findViewById(R.id.pbMsg);
		pbMsg.setVisibility(showProgress ? View.VISIBLE : View.GONE);
		return view;
	}

	private View createView(ViewGroup parent) {
		return inflater.inflate(R.layout.message_item, parent, false);
	}
}
