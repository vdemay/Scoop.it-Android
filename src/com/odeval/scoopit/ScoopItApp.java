package com.odeval.scoopit;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScoopItApp extends Application {
	public static ScoopItApp INSTANCE;
	private static float scale;
	
	@Override
	public void onCreate() {
		super.onCreate();
		DisplayMetrics dm = new DisplayMetrics();
	    (((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getMetrics(dm);
		scale = dm.density;
		INSTANCE = this;
	}

	public static int scaleValue(int value) {
		return (int) ((float)value * scale);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	
}
