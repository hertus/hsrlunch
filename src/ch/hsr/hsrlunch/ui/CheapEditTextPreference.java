package ch.hsr.hsrlunch.ui;

import ch.hsr.hsrlunch.util.CheapEncoder;
import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class CheapEditTextPreference extends EditTextPreference {
	public CheapEditTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheapEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheapEditTextPreference(Context context) {
		super(context);
	}

	@Override
	public String getText() {
		String value = super.getText();
		if (value == null || value.equals("")) {
			return value;
		}
		return CheapEncoder.cheapDecode(value);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		super.setText(restoreValue ? getPersistedString(null)
				: (String) defaultValue);
	}

	@Override
	public void setText(String text) {
		if (text == null || text.equals("")) {
			super.setText(text);
			return;
		}
		super.setText(CheapEncoder.cheapEncode(text));
	}
}
