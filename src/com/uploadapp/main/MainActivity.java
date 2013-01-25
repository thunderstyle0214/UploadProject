package com.uploadapp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {
	private Button uploadbtn = null;
	private TextView responseTextView = null;
	public String file_path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/UploadApp/";
	private final int MSG_SUCCESS = 0;
	private final int MSG_FAIL = 1;
	private String responseValue = "";
	private final String UPLOADURL = "http://dst.dsoundtech.com/android-upload";
	private String filename = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutId(this, R.layout.activity_main);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
				baseProgress.cancel();
				switch (msg.what) {
				case MSG_SUCCESS: {
					responseTextView.setText(responseValue);
				}
					break;
				case MSG_FAIL: {
					responseTextView.setText("Upload Error");
				}
					break;

				}
			}

		};
		initView();
		setListener();
		initData();
	}

	public void initView() {
		uploadbtn = (Button) findViewById(R.id.uploadbtn);
		responseTextView = (TextView) findViewById(R.id.responseTextView);
	}

	public void initData() {
		File file = new File(file_path);
		if (!file.exists())
			file.mkdir();
		copyAssets();
		if (!checkInternetConnection()) {
			showMessage(
					"Upload Error",
					"The connection to Upload App still cannot be established,please try again later",
					"OK");
			return;
		}
	}

	public boolean checkInternetConnection() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public void setListener() {
		uploadbtn.setOnClickListener(this);
	}

	public void onClick(View view) {
		int viewId = view.getId();

		switch (viewId) {
		case R.id.uploadbtn: {
			responseTextView.setText("");
			uploadfunc();
		}
			break;

		}
	}

	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		String file = "test.txt";

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(file);
			filename = file_path + file;
			out = new FileOutputStream(file_path + file);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (IOException e) {
			Log.e("tag", "Failed to copy asset file: " + file, e);
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public void uploadfunc() {
		baseProgress.show();
		new Thread() {
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(UPLOADURL);
				File file = new File(filename);
				ContentBody cBody = new FileBody(file);
				try {
					MultipartEntity multipartContent = new MultipartEntity();
					multipartContent.addPart("file", cBody);

					httpPost.setEntity(multipartContent);
					HttpResponse response = httpClient.execute(httpPost);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent(), "UTF-8"));
					String sResponse;
					StringBuffer s = new StringBuffer();
					while ((sResponse = reader.readLine()) != null) {
						s = s.append(sResponse);
					}
					responseValue = s.toString();
					if (responseValue != null && responseValue.length() > 0) {

						mHandler.sendEmptyMessage(MSG_SUCCESS);
					} else
						mHandler.sendEmptyMessage(MSG_FAIL);
				} catch (Exception e) {
					mHandler.sendEmptyMessage(MSG_FAIL);
				}
			}
		}.start();
	}

}
