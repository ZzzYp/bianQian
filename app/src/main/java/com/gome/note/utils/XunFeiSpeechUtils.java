package com.gome.note.utils;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.gome.note.R;
import com.gome.note.entity.RecordPool;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Eric on 2018/3/26 10:35
 */

public class XunFeiSpeechUtils {
    private static final String TAG = "Eric";
    private Context mContext;
    private String mFilePath;
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private SpeechHelperListener mSpeechHelperListener = null;

    public XunFeiSpeechUtils() {


    }


    public XunFeiSpeechUtils(Context context, String filePath, SpeechHelperListener listener) {


    }


    public void startTranslation(Context context, String filePath, SpeechHelperListener listener) {
        this.mContext = context;
        this.mFilePath = filePath;
        this.mSpeechHelperListener = listener;
        mIat = SpeechRecognizer.createRecognizer(mContext.getApplicationContext(), mInitListener);
        getAudioTranslation();

    }

    public void getAudioTranslation() {
        //TODO:
        // 设置参数
        setParam();
        mIatResults.clear();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Toast.makeText(mContext, mContext.getString(R.string.recognition_failed_and_error_code_is + ret), Toast.LENGTH_SHORT).show();
        } else {
            byte[] audioData = FucUtil.readAudioFile1(mContext, mFilePath);

            if (null != audioData) {
                // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），
                // 位长16bit，单声道的wav或者pcm
                // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
                // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别。
                // 音频切分方法：FucUtil.splitBuffer(byte[] buffer,int length,int spsize);
                mIat.writeAudio(audioData, 0, audioData.length);
                mIat.stopListening();
            } else {
                mIat.cancel();
                Toast.makeText(mContext, mContext.getString(R.string.read_audio_stream_failed), Toast.LENGTH_SHORT).show();
            }
        }
//        return mContext.getResources().getString(R.string.translation_error);
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //Toast.makeText(mContext, ("开始说话"), Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "onBeginOfSpeech：");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            //Toast.makeText(mContext, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "error：" + error);
            mSpeechHelperListener.getTranslateResults("", true, mFilePath);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //Toast.makeText(mContext, ("结束说话"), Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "onEndOfSpeech：");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.d(TAG, results.getResultString());
            printResult(results);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            // Toast.makeText(mContext, ("当前正在说话，音量大小：" + volume), Toast.LENGTH_SHORT).show();
            LogUtils.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
            LogUtils.d(TAG, "session id =" + eventType);
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        boolean isLast = false;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            isLast = resultJson.optBoolean("ls", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }


        LogUtils.e(TAG, "  time :   " + System.currentTimeMillis());

        if (isLast) {
            mSpeechHelperListener.getTranslateResults(resultBuffer.toString(), false, mFilePath);
        }

    }

    private void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        //            mIat.setParameter(SpeechConstant.ACCENT, lag);
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "8000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");

        // mIat.setParameter(SpeechConstant.NET_TIMEOUT, "wav");
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, ("初始化失败，错误码：" + code), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public interface SpeechHelperListener {
        void getTranslateResults(String result, boolean isError, String path);
    }

    public SpeechHelperListener getSpeechHelperListener() {
        return mSpeechHelperListener;
    }

    public void setSpeechHelperListener(SpeechHelperListener mSpeechHelperListener) {
        this.mSpeechHelperListener = mSpeechHelperListener;
    }


    public void releaseSource() {
        mIat.cancel();
        mIat.destroy();
        mSpeechHelperListener = null;
		mRecognizerListener = null;
        mInitListener = null;
    }

}
