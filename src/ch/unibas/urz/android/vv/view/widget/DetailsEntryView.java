package ch.unibas.urz.android.vv.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.unibas.urz.android.vv.R;
import ch.unibas.urz.android.vv.helper.Settings;

public class DetailsEntryView extends LinearLayout {

	private static final int CHARS_PER_LINE = 70;
	private final View v;
	private boolean expanded = false;

	public DetailsEntryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		v = inflate(context, R.layout.details_entry, this);
	}

	public DetailsEntryView(Context context) {
		super(context);
		v = inflate(context, R.layout.details_entry, this);
	}

	public DetailsEntryView(Context context, int labelText, String text) {
		this(context);
		setLabel(labelText);
		setText(text);
	}

	public void setLabel(int labelText) {
		TextView tvLabel = (TextView) v.findViewById(R.id.tvLabel);
		tvLabel.setText(labelText);
	}

	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		TextView tvText = (TextView) v.findViewById(R.id.tvText);
		tvText.setText(text);
		int lines = 1;
		int chrs = text.length();
		final int maxLines = Settings.getInstance().getDetailsMaxLines();
		if (chrs <= maxLines * CHARS_PER_LINE) {
			int idx = 0;
			while ((idx = text.indexOf("\n", idx + 1)) > -1) {
				lines++;
			}
		}
		final TextView tvButtonMoreLess = (TextView) v.findViewById(R.id.tvButtonMoreLess);
		if (chrs > maxLines * CHARS_PER_LINE || lines > maxLines) {
			tvText.setMaxLines(maxLines);
			tvButtonMoreLess.setVisibility(View.VISIBLE);
			tvButtonMoreLess.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View tvButton) {
					TextView tv = (TextView) v.findViewById(R.id.tvText);
					if (expanded) {
						tv.setMaxLines(maxLines);
						expanded = false;
						((TextView) tvButton).setText(R.string.labelMore);
					} else {
						tv.setMaxLines(Integer.MAX_VALUE);
						expanded = true;
						((TextView) tvButton).setText(R.string.labelLess);
					}
					v.requestLayout();
				}
			});
		} else {
			tvButtonMoreLess.setVisibility(View.GONE);
		}
	}

}

// http://stackoverflow.com/questions/5927977/textview-expand-animation-like-in-android-market
// private static int measureViewHeight( View view2Expand, View view2Measure ) {
// try {
// Method m = view2Measure.getClass().getDeclaredMethod("onMeasure", int.class,
// int.class);
// m.setAccessible(true);
// m.invoke(view2Measure,
// MeasureSpec.makeMeasureSpec(view2Expand.getWidth(), MeasureSpec.AT_MOST),
// MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
// } catch (Exception e) {
// return -1;
// }
//
// int measuredHeight = view2Measure.getMeasuredHeight();
// return measuredHeight;
// }
//
// static public void expandOrCollapse( View view2Expand, View view2Measure,
// int collapsedHeight ) {
// if (view2Expand.getHeight() < collapsedHeight)
// return;
//
// int measuredHeight = measureViewHeight(view2Expand, view2Measure, context);
//
// if (measuredHeight < collapsedHeight)
// measuredHeight = collapsedHeight;
//
// final int startHeight = view2Expand.getHeight();
// final int finishHeight = startHeight <= collapsedHeight ?
// measuredHeight : collapsedHeight;
//
// view2Expand.startAnimation(new ExpandAnimation(view2Expand, startHeight,
// finishHeight));
// }
//
// class ExpandAnimation extends Animation {
// private final View _view;
// private final int _startHeight;
// private final int _finishHeight;
//
// public ExpandAnimation( View view, int startHeight, int finishHeight ) {
// _view = view;
// _startHeight = startHeight;
// _finishHeight = finishHeight;
// setDuration(220);
// }
//
// @Override
// protected void applyTransformation( float interpolatedTime, Transformation t
// ) {
// final int newHeight = (int)((_finishHeight - _startHeight) * interpolatedTime
// + _startHeight);
// _view.getLayoutParams().height = newHeight;
// _view.requestLayout();
// }
//
// @Override
// public void initialize( int width, int height, int parentWidth, int
// parentHeight ) {
// super.initialize(width, height, parentWidth, parentHeight);
// }
//
// @Override
// public boolean willChangeBounds( ) {
// return true;
// }
// };