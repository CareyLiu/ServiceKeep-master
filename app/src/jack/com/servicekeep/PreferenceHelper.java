package jack.com.servicekeep;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
	
	private static PreferenceHelper mInstance;
	
	private SharedPreferences mPreference;
	private SharedPreferences.Editor mPreferenceEditor;

	public static String PREFERENCE_NAME = "appclient";

	private PreferenceHelper(Context context) {
		mPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		mPreferenceEditor = mPreference.edit();
	}
	
	public static PreferenceHelper getInstance(Context ctx){
		if (mInstance == null) {
			mInstance = new PreferenceHelper(ctx);
		}
		return mInstance;
	}

	// edit int
	public int getInt(String key, int defaultValue) {
		return mPreference.getInt(key, defaultValue);
	}

	public void putInt(String key, int value) {
		mPreferenceEditor.putInt(key, value);
		mPreferenceEditor.commit();
	}

	// edit string
	public String getString(String str, String defaultValue) {
		return mPreference.getString(str, defaultValue);
	}

	public void putString(String key, String value) {
		mPreferenceEditor.putString(key, value);
		mPreferenceEditor.commit();
	}

	// edit boolean
	public boolean getBoolean(String key, boolean defaultValue) {
		return mPreference.getBoolean(key, defaultValue);
	}

	public void putBoolean(String key, boolean value) {
		mPreferenceEditor.putBoolean(key, value);
		mPreferenceEditor.commit();
	}

	// edit float
	public float getFloat(String key, float defaultValue) {
		return mPreference.getFloat(key, defaultValue);
	}

	public void putFloat(String key, float value) {
		mPreferenceEditor.putFloat(key, value);
		mPreferenceEditor.commit();
	}

	// edit long
	public long getLong(String key, long defaultValue) {
		return mPreference.getLong(key, defaultValue);
	}

	public void putLong(String key, long value) {
		mPreferenceEditor.putLong(key, value);
		mPreferenceEditor.commit();
	}

	public void removeKey(String key){
		mPreferenceEditor.remove(key);
		mPreferenceEditor.commit();
	}
}
