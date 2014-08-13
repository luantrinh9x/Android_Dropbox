package controller;

import java.io.File;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.ItemType;

import com.assignment1.R;
import com.assignment1.TwoSteps;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.*;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import com.dropbox.client2.exception.*;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final static private String APP_KEY = "ufud89b0lf31mnr";
	final static private String APP_SECRET = "m6b0l2g911yfby8";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	// Read text from file
	public static StringBuilder text = new StringBuilder();

	// In the class declaration sectionz
	public static DropboxAPI<AndroidAuthSession> mDBApi;
	private static final int FILE_SELECT_CODE = 0;

	public static ArrayList<String> items = new ArrayList<String>();
	public static ArrayList<String> moves = new ArrayList<String>();
	public static ArrayList<String> history = new ArrayList<String>();
	public static ArrayList<String> moveInside = new ArrayList<String>();
	public static ArrayList<String> movePathArray = new ArrayList<String>();
	//
	public static String downVar;
	public static String downName;
	//
	public static String deleteVar;
	public static String deleteName;
	public static String deleteRev;
	public static String deleteRevName;
	public static boolean deleteRevBool;
	//
	public static String renameTest;
	public static String renameAll;
	public static String renameParent;
	//
	public static String uploadVar;
	public static String uploadName;
	//
	public static String moveCurPath;
	public static String movePrePath;
	public static String moveFileName;
	//
	public static String click;
	public static String createFolderName;
	//
	public static String sharePath;
	public static String shareAddress;
	//
	public static String copyPath;
	public static String copyName;

	//
	private ProgressBar pb;
	ProgressDialog progressBar;

	public boolean showGridView = false;

	public static String[] options = { "Share", "Rename", "Move", "Copy",
			"Delete" };
	public static String[] names = null;

	public static Entry myEntry;
	public static ListView listView;
	public static GridView gridView;
	public static ItemAdapter adapter = null;
	public static ArrayList<String> ids = new ArrayList<String>();
	public static List<Entry> files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// And later in some initialization function:
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);

		// MyActivity below should be your activity class name
		mDBApi.getSession().startAuthentication(MainActivity.this);
		pb = (ProgressBar) findViewById(R.id.pb);
		history.add("/");
	}

	public void noti(String name, String title, String detail) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				android.R.drawable.stat_sys_warning, name,
				System.currentTimeMillis());
		Context context = MainActivity.this;
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, title, detail, pending);
		notification.defaults |= Notification.DEFAULT_ALL;
		nm.notify(0, notification);
	}

	protected void onResume() {
		super.onResume();

		if (mDBApi.getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the
				// session
				mDBApi.getSession().finishAuthentication();
				AccessTokenPair tokens = mDBApi.getSession()
						.getAccessTokenPair();
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				new ShowView(history.get(history.size() - 1)).execute(null,
						null, null);
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}

	public class DownloadContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			File file = new File("/mnt/sdcard/Download/" + downName);
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				DropboxFileInfo info = mDBApi.getFile(downVar, null,
						outputStream, new ProgressListener() {

							@Override
							public void onProgress(long bytes, long total) {
								// TODO Auto-generated method stub
								progressBar.setMax((int) total);
								progressBar.setProgress((int) bytes);

							}
						});
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			progressBar = new ProgressDialog(MainActivity.this);
			progressBar.setCancelable(true);
			progressBar.setMessage("File Downloading...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.dismiss();
			noti("Download Completed", "Download", downName);
			viewFile(downVar);
		}
	}

	public class DeleteContent extends AsyncTask<Void, Void, Void> {

		private String z;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDBApi.delete(deleteVar);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
			File objFile = new File(deleteVar);
			z = objFile.getName();
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			noti("Delete Completed", "Delete", z);
			new ShowView(deleteName).execute(null, null, null);
		}
	}

	public class RenameContent extends AsyncTask<Void, Void, Void> {

		private String a, b;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDBApi.move(renameTest, renameAll);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
			File from = new File(renameTest);
			a = from.getName();
			File to = new File(renameAll);
			b = to.getName();
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			noti("Rename Completed", "Rename", "From " + a + " to " + b);
			new ShowView(renameParent).execute(null, null, null);
		}
	}

	public class UploadContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			File file = new File(uploadVar);
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Entry response = mDBApi.putFile(history.get(history.size() - 1)
						+ "/" + uploadName, inputStream, file.length(), null,
						new ProgressListener() {

							@Override
							public void onProgress(long bytes, long total) {
								// TODO Auto-generated method stub
								progressBar.setMax((int) total);
								progressBar.setProgress((int) bytes);
							}
						});
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPreExecute() {
			progressBar = new ProgressDialog(MainActivity.this);
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
			noti("Upload Completed", "Upload", uploadName);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class MoveContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDBApi.move(moveCurPath, movePrePath + "/" + moveFileName);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			noti("Move Completed", "Move", movePrePath + "/" + moveFileName);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class RestoreContent extends AsyncTask<Void, Void, Void> {

		private String a;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDBApi.restore(deleteVar, deleteRev);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
			File c = new File(deleteVar);
			a = c.getName();
		}

		@Override
		protected void onPostExecute(Void result) {
			deleteRev = null;
			pb.setVisibility(View.GONE);
			noti("Restore Completed", "Restore", a);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class CreateFolder extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			try {
				mDBApi.createFolder(history.get(history.size() - 1) + "/"
						+ createFolderName);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			noti("Create Folder Completed", "Create Folder", createFolderName);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class ShowContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			new ShowView(click).execute(null, null, null);
		}
	}
	
	public class Logout extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mDBApi.getSession().unlink();
			
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
		}
	}

	public class ShareContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				DropboxLink shareLink = mDBApi.share(sharePath);
				shareAddress = (shareLink.url).replaceFirst("https://www",
						"https://dl");
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(shareAddress));
			startActivity(intent);
			pb.setVisibility(View.GONE);
			noti("Share Completed", "Share", shareAddress);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class CopyContent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mDBApi.copy(copyPath, history.get(history.size() - 1) + "/"
						+ copyName);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			pb.setVisibility(View.GONE);
			noti("Copy Completed", "Copy", copyName);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
		}
	}

	public class LoadThumbnail extends AsyncTask<Void, Void, Void> {

		private ImageView iw;
		private String filePath;
		private InputStream ims = null;
		private Drawable d = null;

		public LoadThumbnail(ImageView iw, String filePath) {
			this.iw = iw;
			this.filePath = filePath;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				ims = MainActivity.mDBApi.getThumbnailStream(filePath,
						ThumbSize.ICON_128x128, ThumbFormat.JPEG);
			} catch (DropboxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			d = Drawable.createFromStream(ims, null);
			return null;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Void result) {
			iw.setImageDrawable(d);
			d = null;
			ims = null;
		}
	}

	public class EditTextFile extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			File file = new File("/mnt/sdcard/Download/" + downName);
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				DropboxFileInfo info = mDBApi.getFile(downVar, null,
						outputStream, new ProgressListener() {

							@Override
							public void onProgress(long bytes, long total) {
								// TODO Auto-generated method stub
								progressBar.setMax((int) total);
								progressBar.setProgress((int) bytes);

							}
						});
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}

			} catch (IOException e) {
				// You'll need to add proper error handling here
			}

			return null;
		}

		protected void onPreExecute() {
			progressBar = new ProgressDialog(MainActivity.this);
			progressBar.setCancelable(true);
			progressBar.setMessage("File Downloading...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setMax(100);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.dismiss();
			Intent int1 = new Intent(MainActivity.this,
					com.assignment1.EditTextFile.class);
			startActivity(int1);
		}
	}

	public class ShowView extends AsyncTask<Void, Void, Void> {

		private String testPath;

		public ShowView(String testPath) {
			this.testPath = testPath;
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				myEntry = mDBApi.metadata(testPath, -1, null, true, null);
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<Entry> files = myEntry.contents;

			for (Entry entry : files) {
				items.add(entry.fileName());
			}

			for (int i = 0; i < items.size(); i++) {
				ids.add(Integer.toString(i + 1));
			}

			ItemType.LoadModel(items);

			return null;
		}

		protected void onPreExecute() {
			items.removeAll(items);
			ids.removeAll(ids);
		}

		@Override
		protected void onPostExecute(Void result) {

			if (showGridView == false) {
				listView = (ListView) findViewById(R.id.lv);
				adapter = new ItemAdapter(MainActivity.this, R.layout.row, ids);
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
			} else {
				gridView = (GridView) findViewById(R.id.gv);
				adapter = new ItemAdapter(MainActivity.this, R.layout.grid_row,
						ids);
				adapter.notifyDataSetChanged();
				gridView.setAdapter(adapter);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.uploadAB:
			// startActivity(new Intent(this, CoursesActivity.class));
			final String[] items = { "Create a Folder", "Upload a File" };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Options");
			builder.setSingleChoiceItems(items, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item == 1) {
								showFileChooser();
							} else if (item == 0) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										MainActivity.this);
								builder.setTitle("Enter Folder Name");

								// Set up the input
								final EditText input = new EditText(
										MainActivity.this);
								// Specify the type of input expected; this, for
								// example, sets the input as a password, and
								// will mask the text
								input.setInputType(InputType.TYPE_CLASS_TEXT);
								builder.setView(input);

								// Set up the buttons
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												createFolderName = input
														.getText().toString();
												if (createFolderName
														.equalsIgnoreCase("")) {
													AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
															MainActivity.this);
													myAlertDialog
															.setTitle("Folder Name cannot be blank");
													myAlertDialog.show();
												} else {
													new CreateFolder().execute(
															null, null, null);
												}
											}
										});
								builder.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										});

								builder.show();
							}
							dialog.dismiss();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case R.id.undoAB:
			if (deleteRev == null) {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						MainActivity.this);
				myAlertDialog.setTitle("Warning");
				myAlertDialog.setMessage("Nothing to Restore");
				myAlertDialog.show();
			} else {
				if (deleteRevBool == true) {
					AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
							MainActivity.this);
					myAlertDialog.setTitle("Warning");
					myAlertDialog.setMessage("Cannot restore folder named "
							+ deleteRevName + ". Only allow to restore File !");
					myAlertDialog.show();
					deleteRev = null;
				} else {
					AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
							MainActivity.this);
					myAlertDialog.setTitle("Do you want to restore "
							+ deleteRevName + " ?");
					myAlertDialog.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									// do something when the OK button is
									// clicked
									new RestoreContent().execute(null, null,
											null);
								}
							});
					myAlertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									// do something when the Cancel button is
									// clicked
								}
							});
					myAlertDialog.show();
				}

			}
			break;
		case R.id.pasteAB:
			if (copyName == null) {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						MainActivity.this);
				myAlertDialog.setTitle("Warning");
				myAlertDialog.setMessage("Nothing to Paste");
				myAlertDialog.show();
			} else {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						MainActivity.this);
				myAlertDialog.setTitle("Copy");
				myAlertDialog.setMessage("Do you want to paste " + copyName
						+ " here ?");
				myAlertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// do something when the OK button is clicked
								new CopyContent().execute(null, null, null);
							}
						});
				myAlertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// do something when the Cancel button is
								// clicked
							}
						});
				myAlertDialog.show();
			}
			break;
		case R.id.gridViewOpt:
			showGridView = true;
			setContentView(R.layout.grid_view);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
			break;
		case R.id.listViewOpt:
			showGridView = false;
			setContentView(R.layout.activity_main);
			new ShowView(history.get(history.size() - 1)).execute(null, null,
					null);
			break;
		case R.id.logout:
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
					MainActivity.this);
			myAlertDialog.setTitle("Warning");
			myAlertDialog.setMessage("Do you want to logout ?");
			myAlertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// do something when the OK button is clicked
							mDBApi.getSession().unlink();
							finish();
						}
					});
			myAlertDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// do something when the Cancel button is clicked
						}
					});
			myAlertDialog.show();

			break;
		default:
			// default intent
		}
		return super.onOptionsItemSelected(item);
	}

	public class ItemAdapter extends ArrayAdapter<String> {

		private final Context context;
		private final ArrayList<String> Ids;
		private final int rowResourceId;
		private String testPath = "/";

		public ItemAdapter(Context context, int textViewResourceId,
				ArrayList<String> items) {
			super(context, textViewResourceId, items);
			this.context = context;
			this.Ids = items;
			this.rowResourceId = textViewResourceId;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final View rowView = inflater.inflate(rowResourceId, parent, false);
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.imageView);
			TextView textView = (TextView) rowView.findViewById(R.id.textView);

			int i = 0;

			if (showGridView == false) {
				if (position % 2 == 0) {
					rowView.setBackgroundColor(Color.parseColor("#ffffff"));
				} else {
					rowView.setBackgroundColor(Color.parseColor("#f2f2f2"));
				}
			}

			rowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					files = myEntry.contents;
					testPath = files.get(position).path;

					rowView.setBackgroundColor(Color.parseColor("#e9f6fc"));

					if (files.get(position).isDir == false) {
						downVar = testPath;
						downName = files.get(position).fileName();
						if (downName.substring(downName.length() - 4,
								downName.length()).equalsIgnoreCase(".txt")) {
							new EditTextFile().execute(null, null, null);
						} else {
							AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
									MainActivity.this);
							myAlertDialog.setTitle("Do you want to download ?");
							myAlertDialog.setMessage("This " + downName
									+ " will store in mnt/sdcard/Download");
							myAlertDialog.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface arg0, int arg1) {
											// do something when the OK button
											// is clicked
											rowView.setBackgroundColor(Color.TRANSPARENT);
											new DownloadContent().execute(null,
													null, null);
										}
									});
							myAlertDialog.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface arg0, int arg1) {
											// do something when the Cancel
											// button is clicked
											rowView.setBackgroundColor(Color.TRANSPARENT);
										}
									});
							myAlertDialog.show();
						}
					} else {
						click = testPath;
						new ShowContent().execute(null, null, null);
						history.add(testPath);
					}
				}
			});

			rowView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("Options");
					builder.setItems(options,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									// Do something with the selection
									final String currentPath;
									if (item == 0)// share
									{
										files = myEntry.contents;
										sharePath = files.get(position).path;
										new ShareContent().execute(null, null,
												null);
									} else if (item == 3) {
										files = myEntry.contents;
										copyPath = files.get(position).path;
										copyName = files.get(position)
												.fileName();
										AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
												MainActivity.this);
										myAlertDialog.setTitle("Copy");
										myAlertDialog
												.setMessage("Already Copied "
														+ copyName);

										myAlertDialog.show();
									} else if (item == 4)// delete
									{
										files = myEntry.contents;
										testPath = files.get(position).path;
										currentPath = files.get(position)
												.parentPath();

										AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
												MainActivity.this);
										myAlertDialog
												.setTitle("Do you want to delete "
														+ files.get(position)
																.fileName()
														+ " ?");
										myAlertDialog
												.setPositiveButton(
														"OK",
														new DialogInterface.OnClickListener() {

															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																// do something
																// when the OK
																// button is
																// clicked
																deleteRev = files
																		.get(position).rev;
																deleteRevName = files
																		.get(position)
																		.fileName();
																deleteRevBool = files
																		.get(position).isDir;
																deleteVar = testPath;
																deleteName = currentPath;
																new DeleteContent()
																		.execute(
																				null,
																				null,
																				null);
															}
														});
										myAlertDialog
												.setNegativeButton(
														"Cancel",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																// do something
																// when the
																// Cancel button
																// is clicked
															}
														});
										myAlertDialog.show();
									}
									// move
									else if (item == 2) {
										files = myEntry.contents;
										testPath = files.get(position).path;
										moveCurPath = testPath;
										final String fileName = files.get(
												position).fileName();
										moveFileName = fileName;
										movePathArray.removeAll(movePathArray);
										for (Entry entry : files) {
											if (entry.isDir) {
												movePathArray.add(entry.path);
											}
										}

										if (history.get(history.size() - 1)
												.equalsIgnoreCase("/")) {
											names = movePathArray
													.toArray(new String[movePathArray
															.size()]);
										} else {
											movePathArray.add(history
													.get(history.size() - 2));
											names = movePathArray
													.toArray(new String[movePathArray
															.size()]);
										}

										AlertDialog.Builder builder = new AlertDialog.Builder(
												MainActivity.this);
										builder.setTitle("Select Path");

										builder.setItems(
												names,
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int which) {
														//
														movePrePath = names[which];
														movePathArray
																.removeAll(movePathArray);
														new MoveContent()
																.execute(null,
																		null,
																		null);
														dialog.dismiss();
													}
												});
										builder.setNegativeButton(
												"cancel",
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int which) {
														movePathArray
																.removeAll(movePathArray);
														dialog.dismiss();
													}
												});
										AlertDialog alert = builder.create();
										alert.show();
									} else if (item == 1) {
										files = myEntry.contents;
										testPath = files.get(position).path;
										final String parentPath = files.get(
												position).parentPath();

										AlertDialog.Builder builder = new AlertDialog.Builder(
												MainActivity.this);
										builder.setTitle("Rename");
										// Set up the input
										final EditText input = new EditText(
												MainActivity.this);
										input.setText(files.get(position)
												.fileName());
										// Specify the type of input expected;
										// this, for example, sets the input as
										// a password, and will mask the text
										input.setInputType(InputType.TYPE_CLASS_TEXT);
										builder.setView(input);

										// Set up the buttons
										builder.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														String m_Text = input
																.getText()
																.toString();
														String allPath = parentPath
																+ m_Text;
														renameTest = testPath;
														renameAll = allPath;
														renameParent = parentPath;
														new RenameContent()
																.execute(null,
																		null,
																		null);
													}
												});
										builder.setNegativeButton(
												"Cancel",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.cancel();
													}
												});
										builder.show();
									}
								}
							});
					AlertDialog alert = builder.create();
					alert.show();

					return false;
				}
			});

			int id = Integer.parseInt(Ids.get(position));
			String imageFile = ItemType.GetbyId(id).IconFile;
			textView.setText(ItemType.GetbyId(id).Name);

			// get input stream
			InputStream ims = null;

			if (!imageFile.equalsIgnoreCase("pic.png")) {
				try {
					ims = context.getAssets().open(imageFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Drawable d = Drawable.createFromStream(ims, null);
				imageView.setImageDrawable(d);
				ims = null;
				d = null;
			} else {
				files = myEntry.contents;
				new LoadThumbnail(imageView, files.get(position).path).execute(
						null, null, null);
			}

			Animation animation = AnimationUtils.loadAnimation(context,
					R.anim.push_left_in);
			rowView.startAnimation(animation);
			animation = null;
			System.gc();
			return rowView;
		}
	}

	@Override
	public void onBackPressed() {

		if (history.get(history.size() - 1).equalsIgnoreCase("/")) {
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
					MainActivity.this);
			myAlertDialog.setTitle("Do you want to exit?");
			myAlertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// do something when the OK button is clicked
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(1);
						}
					});
			myAlertDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// do something when the Cancel button is clicked
						}
					});
			myAlertDialog.show();
		} else {
			history.remove(history.size() - 1);
			click = history.get(history.size() - 1);
			new ShowContent().execute(null, null, null);
		}
	}

	public void viewFile(final String fileName) {
		File objFile = new File(fileName);
		final String newName = objFile.getName();
		
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				MainActivity.this);
		myAlertDialog.setTitle("Warning");
		myAlertDialog.setMessage("Do you want to open this " + newName + " ?");
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the OK button is clicked
						String mpath = "mnt/sdcard/Download/" + newName;
						File targetFile = new File(mpath);
						Uri targetUri = Uri.fromFile(targetFile);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(targetUri, "application/*");
						startActivity(intent);
					}
				});
		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		myAlertDialog.show();
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String path = getPath(data.getData());
		uploadVar = path;
		File objFile = new File(path);
		uploadName = objFile.getName();

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				MainActivity.this);
		myAlertDialog.setTitle("Do you want to upload " + uploadName + " ?");
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the OK button is clicked
						new UploadContent().execute(null, null, null);
					}
				});
		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		myAlertDialog.show();

		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Audio.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	
}
