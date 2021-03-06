package com.scoopit.android.curation;

import java.text.DateFormat;
import java.util.Locale;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.scoopit.android.curation.helper.NetworkingCache;
import com.scoopit.android.curation.image.ImageLoader;


@ReportsCrashes(formKey = "dGQ4bXBIbk0wSzliRm1XNENOS05TN0E6MQ") 
public class ScoopItApp extends Application {
	
	public static ScoopItApp INSTANCE;
	
	private static float scale;
	
	public ImageLoader imageLoader;
	public NetworkingCache cache;
	
	public DateFormat dateTimeFormatShortShort = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
	
	public DateFormat dateTimeFormatMediumShort = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
	
	@Override
	public void onCreate() {
	    // The following line triggers the initialization of ACRA
        ACRA.init(this);

		super.onCreate();
		DisplayMetrics dm = new DisplayMetrics();
	    (((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getMetrics(dm);
		scale = dm.density;
		INSTANCE = this;
		imageLoader = new ImageLoader(this);
		cache = new NetworkingCache(this);
	}

	public static int scaleValue(int value) {
		return (int) ((float)value * scale);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	
}
