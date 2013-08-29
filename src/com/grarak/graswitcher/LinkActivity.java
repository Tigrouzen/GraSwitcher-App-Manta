package com.grarak.graswitcher;

import com.grarak.graswitcher.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class LinkActivity extends Activity {
	
	private static String HTML;
	private static Context context;
	private static final String Downloadlink = "https://raw.github.com/Grarak/otaupdates/master/graswitchermanta";
	public static String mDownloadLink;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Utils.displayprogress("Checking connection", this);
		context = this;
		
		Utils.getconnection(Downloadlink);

		DisplayString task = new DisplayString();
		task.execute();
	}
	
	private static class DisplayString extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			return Utils.mHtmlstring;
		}

		@Override
		protected void onPostExecute(String result) {
			HTML = Utils.mHtmlstring.toString();
			if (HTML.isEmpty()) {
				Utils.toast("No internet connection", context);
			} else {
				if (HTML.contains("ffline")) {
					Utils.toast("Server is down", context);
				} else {
					mDownloadLink = HTML;
					Intent i = new Intent(context, PackageDownloader.class);
					context.startActivity(i);
					((Activity) context).finish();
				}
			}
			Utils.hideprogress();
		}
	}
}
