package ch.hsr.hsrlunch.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomMenuView extends ListView {

	public interface OnScrollChangedListener {
		void onScrollChanged();
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public CustomMenuView(Context context) {
		super(context);
		this.setOnScrollChangedListener(new CustomMenuView.OnScrollChangedListener() {

			@Override
			public void onScrollChanged() {
				invalidate();
			}

		});
	}

	public CustomMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (mOnScrollChangedListener != null)
			mOnScrollChangedListener.onScrollChanged();
	}

	public void setOnScrollChangedListener(OnScrollChangedListener listener) {
		mOnScrollChangedListener = listener;
	}
}