package com.gome.note.utils;

import android.content.Context;

import com.gome.note.R;
import com.gome.note.entity.SpeechResultInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eric on 2018/3/8 11:22
 */

public class BaiduSpeechHelper {

    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";
    //put your own params here
    private static final String apiKey = "nQWZb0foF0HIhVNWxmYVCrBU";
    private static final String secretKey = "5229f91f8c0f9b4d4c2995452d221dc9";
    private String mFilePath;
    private Context mContext;
    public BaiduSpeechHelperListener mBaiduSpeechHelperListener = null;

    public BaiduSpeechHelper(Context context, String filePath, BaiduSpeechHelperListener listener) {
        this.mContext = context;
        this.mFilePath = filePath;
        mBaiduSpeechHelperListener = listener;
    }

    public String getAudioTranslation() {

        try {
            getToken();
            return method2("11111");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mContext.getResources().getString(R.string.translation_error);
    }

    private void getToken() throws Exception {
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +
                "&client_id=" + apiKey + "&client_secret=" + secretKey;


        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");


    }

    //raw 格式 post请求
    String method2(String deviceId) throws Exception {
        File pcmFile = new File(mFilePath);
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL
                + "?cuid=" + deviceId + "&token=" + token).openConnection();

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "audio/pcm; rate=8000");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.write(loadFile(pcmFile));
        wr.flush();
        wr.close();

        String jsonResult = printResponse(conn);
        //analysis json
        String result = analysisJson(jsonResult);
        mBaiduSpeechHelperListener.getTranslateResults(result);

        return printResponse(conn);
    }

    private String analysisJson(String result) {

        SpeechResultInfo speechResultInfo = (SpeechResultInfo) JsonUtil.jsonToBean(result, SpeechResultInfo.class);
        String[] stringResult = speechResultInfo.getResult();
        StringBuffer resultBuffer = new StringBuffer();
        if (null != stringResult) {
            for (int i = 0; i < stringResult.length; i++) {
                resultBuffer.append(stringResult[i]);
            }
        }
        return resultBuffer.toString();

    }

    private String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public interface BaiduSpeechHelperListener {
        void getTranslateResults(String result);
    }

    public BaiduSpeechHelperListener getBaiduSpeechHelperListener() {
        return mBaiduSpeechHelperListener;
    }

    public void setBaiduSpeechHelperListener(BaiduSpeechHelperListener mBaiduSpeechHelperListener) {
        this.mBaiduSpeechHelperListener = mBaiduSpeechHelperListener;
    }
}
