package com.example.volleytest;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author yanzi E-mail: yanzi1225627@163.com
 * @version 创建时间: 2015-08-09 下午4:32
 */
public class MultipartRequest extends Request<String>{

    private MultipartEntity entity = new MultipartEntity();

    private  Response.Listener<String> mListener;

    private List<File> mFileParts;
    private String mFilePartName;
    private Map<String, String> mParams;

    /**
     * 单个文件＋参数 上传
     * @param url
     * @param listener
     * @param errorListener
     * @param filePartName
     * @param file
     * @param params
     */
    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                            String filePartName, File file, Map<String, String> params){
        super(Method.POST, url, errorListener);
        mFileParts = new ArrayList<File>();
        if(file != null && file.exists()){
            mFileParts.add(file);
        }else{
            VolleyLog.e("MultipartRequest---file not found");
        }
        mFilePartName = filePartName;
        mListener = listener;
        mParams = params;
        buildMultipartEntity();

    }

    /**
     * 多个文件＋参数上传
     * @param url
     * @param listener
     * @param errorListener
     * @param filePartName
     * @param files
     * @param params
     */
    public MultipartRequest(String url,Response.Listener<String> listener,Response.ErrorListener errorListener
                            , String filePartName,List<File> files, Map<String, String> params) {
        super(Method.POST, url, errorListener);
        mFilePartName = filePartName;
        mListener = listener;
        mFileParts = files;
        mParams = params;
        buildMultipartEntity();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    private void buildMultipartEntity() {
        if (mFileParts != null && mFileParts.size() > 0) {
            for (File file : mFileParts) {
                entity.addPart(mFilePartName, new FileBody(file));
            }
            long l = entity.getContentLength();
            Log.i("YanZi-volley", mFileParts.size() + "个，长度：" + l);
        }

        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(
                            entry.getKey(),
                            new StringBody(entry.getValue(), Charset
                                    .forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }
}
