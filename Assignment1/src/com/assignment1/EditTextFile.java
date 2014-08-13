package com.assignment1;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import controller.MainActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class EditTextFile extends Activity {

	public static String a = "";
	ProgressDialog progressBar;
	public static EditText etf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_text_file);
		
		etf = (EditText) findViewById(R.id.editText1);
		Button cancel = (Button) findViewById(R.id.cancel);
		Button ok = (Button) findViewById(R.id.ok);
		
		etf.setText(MainActivity.text.toString());
		MainActivity.text.delete(0, MainActivity.text.length());
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    finish();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new WriteFile().execute(null, null, null);
			}
		});
	}
	
	public class FinishEditTextFile extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			
			File file = new File("/mnt/sdcard/Download/" + MainActivity.downName);
			FileInputStream inputStream = null;
			
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Entry response = MainActivity.mDBApi.putFile(MainActivity.history.get(MainActivity.history.size() - 1)+ "/" + MainActivity.downName, inputStream, file.length(), null, new ProgressListener() {
					
					@Override
					public void onProgress(long bytes, long total) {
						// TODO Auto-generated method stub
						progressBar.setMax((int)total);
						progressBar.setProgress((int)bytes);
					}
				});
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onPreExecute() {
			progressBar = new ProgressDialog(EditTextFile.this);
			progressBar.setCancelable(true);
			progressBar.setMessage("File Uploading...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			progressBar.dismiss();
			finish();
		}
	}
	
	
	public class DeleteContent extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				MainActivity.mDBApi.delete(MainActivity.downVar);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
		}
		
		@Override
		protected void onPostExecute(Void result) {
		}
	}
	
	public class WriteFile extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			a  = etf.getText().toString();
			try {
				File file = new File("/mnt/sdcard/Download/" + MainActivity.downName);
	 
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(a);
				bw.close();
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			new DeleteContent().execute(null, null, null);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			new FinishEditTextFile().execute(null, null, null);
		}
	}

}
