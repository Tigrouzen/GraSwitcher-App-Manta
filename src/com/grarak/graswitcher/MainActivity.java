package com.grarak.graswitcher;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import com.grarak.graswitcher.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class MainActivity extends Activity {
	
	protected static Process superUser;
	protected static DataOutputStream dos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			superUser = new ProcessBuilder("su", "-c", "/system/xbin/ash").start();
			dos = new DataOutputStream(superUser.getOutputStream());
			dos.writeBytes("\n" + "echo 1 > /data/property/persist.service.adb.enable" + "\n");
			dos.writeBytes("\n" + "mkdir -p /sdcard/graswitcher" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File img1 = new File("/sdcard/graswitcher/first.img");
		File img2 = new File("/sdcard/graswitcher/second.img");
		File zip = new File ("/sdcard/graswitcher/download.zip");
		if (img1.exists() && img2.exists()) {
			imgexist(this);
		} else {
			if (zip.exists()) {
				extract(this, img1, img2);
			} else {
				startDownload(this);
			}
		}
	}
	
	private static void extract(Context context, File img1, File img2) {
		try {
			Utils.displayprogress("Extract files", context);
			superUser = new ProcessBuilder("su", "-c", "/system/xbin/ash").start();
			dos = new DataOutputStream(superUser.getOutputStream());
			dos.writeBytes("\n" + "unzip /sdcard/graswitcher/download.zip -d /sdcard/graswitcher/" + "\n");
			Utils.hideprogress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (img1.exists() && img2.exists()) {
			imgexist(context);
		} else {
			extract(context, img1, img2);
		}
	}
	
	private static void imgexist(Context context) {
		Utils.alertchoose("Select Rom", "Cancel", "Yes", context);
	}
	
	private static void startDownload (Context context) {
		Utils.alerttwo("Download", "Do you want to download GraSwitcher files?", context);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
