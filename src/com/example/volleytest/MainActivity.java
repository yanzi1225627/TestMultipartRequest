package com.example.volleytest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity {

	Button bt;
	private static RequestQueue mSingleQueue;
	private static String TAG = "test";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		mSingleQueue = Volley.newRequestQueue(this, new MultiPartStack());
		mSingleQueue = Volley.newRequestQueue(getApplicationContext());

		bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doUploadTest();
			}
		});
	}
	
	private void doUploadTest(){
		String path = "/mnt/sdcard/0/test.jpg";
		String url = "http://app.sod90.com/city52/upload/app_upload";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "19");
		params.put("type", "shop");
		File f1 = new File(path);
		File f2 = new File(path);
		
		if(!f1.exists()){
			Toast.makeText(getApplicationContext(), "图片不存在，测试无效", Toast.LENGTH_SHORT).show();
			return;
		}
		List<File> f = new ArrayList<File>();
		f.add(f1);
		f.add(f2);
		MultipartRequest request = new MultipartRequest(url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Toast.makeText(getApplicationContext(), "uploadSuccess,response = " + response, Toast.LENGTH_SHORT).show();
				Log.i("YanZi", "success,response = " + response);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "uploadError,response = " + error.getMessage(), Toast.LENGTH_SHORT).show();
				Log.i("YanZi", "error,response = " + error.getMessage());				
			}
		}, "f_file[]", f1, params); //注意这个key必须是f_file[],后面的[]不能少
		mSingleQueue.add(request);
	}
	
	/*********************下面的代码是别人的测试代码，仅供参考,鄙人不推荐*******************/
	private void doUploadTest2(){
		Map<String, File> files = new HashMap<String, File>();
		files.put("f_file", new File(
				"/storage/emulated/0/DCIM/Camera/1.jpg"));

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "19");
		params.put("type", "shop");

		String uri = "http://app.sod90.com/city52/upload/app_upload";
		addPutUploadFileRequest(
				uri,
				files, params, mResonseListenerString, mErrorListener, null);
	}

	Listener<JSONObject> mResonseListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {
			Log.i(TAG, " on response json" + response.toString());
		}
	};

	Listener<String> mResonseListenerString = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i(TAG, " on response String" + response.toString());
		}
	};

	ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			if (error != null) {
				if (error.networkResponse != null)
					Log.i(TAG, " error " + new String(error.networkResponse.data));
			}
		}
	};

	public static void addPutUploadFileRequest(final String url,
			final Map<String, File> files, final Map<String, String> params,
			final Listener<String> responseListener, final ErrorListener errorListener,
			final Object tag) {
		if (null == url || null == responseListener) {
			return;
		}

		MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
				Request.Method.PUT, url, responseListener, errorListener) {

			@Override
			public Map<String, File> getFileUploads() {
				return files;
			}

			@Override
			public Map<String, String> getStringUploads() {
				return params;
			}
			
		};

		Log.i(TAG, " volley put : uploadFile " + url);

		mSingleQueue.add(multiPartRequest);
	}

}
