package com.grarak.graswitcher.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.grarak.graswitcher.LinkActivity;
import com.grarak.graswitcher.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.Toast;

public class Utils {
	
	protected static Process superUser;
	protected static DataOutputStream dos;
	private static ProgressDialog mProgressDialog;
	public static String mHtmlstring;
	private static int selected = 0;
	private static int buffKey = 0;
	
	@SuppressLint("NewApi") public static void alertchoose(String title, String negativ,
			String positiv, final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		final CharSequence[] choiceList = { "First Rom",
				"Second Rom" };

		builder.setSingleChoiceItems(choiceList, selected,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						buffKey = which;
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						((Activity) context).finish();
					}
				})
				.setPositiveButton(positiv,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (choiceList[buffKey].toString().contains("First")) {
									flashkernel("first", context);
								} else {
									flashkernel("second", context);
								}
								selected = buffKey;
								((Activity) context).finish();
							}
						})
				.setNegativeButton(negativ,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((Activity) context).finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@SuppressLint("NewApi")
	public static void alerttwo(String title, String message, final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				((Activity) context).finish();
			}
		})
		.setMessage(message)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) context).finish();
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(context, LinkActivity.class);
				context.startActivity(i);
				((Activity) context).finish();
			}
		}).show();
	}
	
	@SuppressLint("NewApi")
	public static void alertone(String title, String message, final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
		.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				((Activity) context).finish();
			}
		})
		.setMessage(message)
		.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) context).finish();
			}
		}).show();
	}
	
	private static void flashkernel(String os, Context context) {
		try {
			superUser = new ProcessBuilder("su", "-c", "/system/xbin/ash")
					.start();
			dos = new DataOutputStream(superUser.getOutputStream());
			dos.writeBytes("\n" + "rm -rf /1stdata/firstboot" + "\n");
			dos.writeBytes("\n" + "rm -rf /cache/firstboot" + "\n");
			dos.writeBytes("\n" + "dd if=/sdcard/graswitcher/" + os + 
					".img of=/dev/block/platform/dw_mmc.0/by-name/boot" + "\n");
			dos.writeBytes("\n" + "reboot" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void toast(String message, Context context) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	public static void displayprogress(String message, final Context context) {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(message);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						((Activity) context).finish();
					}
				});
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();
	}

	public static void hideprogress() {
		mProgressDialog.hide();
	}
	
	public static void getconnection(String url) {

		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { url });
	}

	private static class DownloadWebPageTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			mHtmlstring = Html.fromHtml(result).toString();
		}
	}
	
	public static void deleteFiles(String path) {

		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -rf " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
			} catch (IOException e) {
			}
		}
	}
}
