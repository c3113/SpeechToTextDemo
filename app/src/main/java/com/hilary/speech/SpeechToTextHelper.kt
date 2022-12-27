package com.hilary.speech

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer

/**
 * @description:    语音转文字
 *                  对接华为服务：https://developer.huawei.com/consumer/cn/doc/development/hiai-Guides/ml-asr-0000001050066212
 *                  1.初始化： initRecognizer()
 *                  2.开始录音：startRecognizing()
 *                  3.结束录音：destroy()
 * @author ChenKai
 * @date 2022/12/27
 */
class SpeechToTextHelper(private val context: Context) {
    private val TAG = "SpeechToTextHelper"
    lateinit var mSpeechRecognizer: MLAsrRecognizer
    private val REQUEST_CODE_ASR = 100
    private var recognizing: ((Boolean, String?, String?) -> Unit)? = null
    /**
     * 初始化
     * recognizing回调可能会调用多次，测试结果：声音识别成功也返回了无效的服务错误提示
     *  @param recognizing Boolean: 是否开始记录，开始录音返回(true,null, null)
     *                     String?: 录音结果实时返回(true, "xxx", null)
     *                     String?: 录音结果最终返回(false, null, "xxx")
     *                     异常反回：(false, null, null)
     */
    fun initRecognizer(recognizing: ((recognizing : Boolean, recognizingResult: String?, result: String?) -> Unit)) {
        Log.d(TAG, "###initRecognizer###")
        this.recognizing = recognizing
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(context)
        mSpeechRecognizer.setAsrListener(SpeechRecognitionListener())

    }

    /**
     * 开始录音
     * 一定时间未输入语音会自动终止，只需要重新调用此方法就可以再次开启录音功能
     */
    fun startRecognizing() {
        Log.d(TAG, "###startRecognizing###")
        // 新建Intent，用于配置语音识别参数。
        val mSpeechRecognizerIntent = Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH)
        // 通过Intent进行语音识别参数设置。
        mSpeechRecognizerIntent
            // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar":阿拉伯语；"th=TH"：泰语；"ms-MY"：马来语；"fil-PH"：菲律宾语；"tr-TR"：土耳其语。
            .putExtra(MLAsrConstants.LANGUAGE, "zh-CN") // 设置识别文本返回模式为边识别边出字，若不设置，默认为边识别边出字。支持设置：
            // MLAsrConstants.FEATURE_WORDFLUX：通过onRecognizingResults接口，识别同时返回文字；
            // MLAsrConstants.FEATURE_ALLINONE：识别完成后通过onResults接口返回文字。
            .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX) // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
//            .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)
        // 启动语音识别。
        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent)
    }

    /**
     * 释放资源
     * 不再使用时调用此方法
     */
    fun destroy() {
        Log.d(TAG, "###destroy###")
        mSpeechRecognizer.destroy()
    }

    // 回调实现MLAsrListener接口，实现接口中的方法。
    internal inner class SpeechRecognitionListener : MLAsrListener {
        //获取实时声音输出KEY
        private val RESULT_RECOGNIZING_KEY = "results_recognizing"
        //获取声音输出结果KEY
        private val RESULT_RECOGNIZED_KEY = "results_recognized"
        override fun onStartListening() {
            // 录音器开始接收声音。
            recognizing?.let { it(true, null, null) }
            Log.d(TAG, "###onStartListening###")
        }

        override fun onStartingOfSpeech() {
            // 用户开始讲话，即语音识别器检测到用户开始讲话。
            Log.d(TAG, "###onStartingOfSpeech###")
        }

        override fun onVoiceDataReceived(data: ByteArray, energy: Float, bundle: Bundle) {
            // 返回给用户原始的PCM音频流和音频能量，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        }

        override fun onRecognizingResults(partialResults: Bundle) {
            // 从MLAsrRecognizer接收到持续语音识别的文本，该接口并非运行在主线程中，返回结果需要在子线程中处理。
            val result = getOnResult(partialResults, RESULT_RECOGNIZING_KEY)
            Log.d(TAG, "###onRecognizingResults : $result")
            recognizing?.let { it(true, result, null) }
        }

        override fun onResults(results: Bundle) {
            // 语音识别的文本数据，该接口并非运行在主线程中，返回结果需要在子线程中处理。
            val result = getOnResult(results, RESULT_RECOGNIZED_KEY)
            Log.d(TAG, "###onResults : $result")
            recognizing?.let { it(false, null, result) }
        }

        override fun onError(error: Int, errorMessage: String) {
            // 识别发生错误后调用该接口，该接口并非运行在主线程中，返回结果需要在子线程中处理。
            Log.d(TAG, "###onError : $error --> $errorMessage")
            recognizing?.let { it(false, null, null) }
        }

        override fun onState(state: Int, params: Bundle) {
            // 通知应用状态发生改变，该接口并非运行在主线程中，返回结果需要在子线程中处理。
        }
    }

    /**
     * 解析返回结果
     */
    private fun getOnResult(partialResults: Bundle, key: String): String? {
        return partialResults.getString(key)
    }

    fun startHWUIRecognizing(activity: Activity) {
        val intent = Intent(activity, MLAsrCaptureActivity::class.java)
            // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar": 阿拉伯语；"ru-RU":俄语；“th_TH”：泰语；“ms_MY”：马来语；“fil_PH”：菲律宾语；"tr-TR"：土耳其语。
            .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
            // 设置拾音界面是否显示识别结果，MLAsrCaptureConstants.FEATURE_ALLINONE为不显示，MLAsrCaptureConstants.FEATURE_WORDFLUX为显示。
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
            // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
            .putExtra(MLAsrConstants.SCENES,
                MLAsrConstants.SCENES_SHOPPING)

        // REQUEST_CODE_ASR表示当前Activity和拾音界面Activity之间的请求码，通过该码可以在当前Activity中获取拾音界面的处理结果。
        activity.startActivityForResult(intent, REQUEST_CODE_ASR)
    }

    /**
     * 解析华为UI返回结果
     * 返回null为发生异常，正常数据为非null数据
     */
    fun onActivityResult(callback: (String?) -> Unit, requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        var text = ""
        // REQUEST_CODE_ASR是第3步中定义的当前Activity和拾音界面Activity之间的请求码。
        if (requestCode == REQUEST_CODE_ASR) {
            when (resultCode) {
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    // 获取语音识别得到的文本信息。
                    if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        // 识别得到的文本信息处理。
                        println("####result : $text###")
                        callback(text)
                    }
                }
                MLAsrCaptureConstants.ASR_FAILURE -> {              // 识别失败处理。
                    if (data != null) {
                        val bundle = data.extras
                        // 判断是否包含错误码。
                        if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                            // 对错误码进行处理。
                        }
                        // 判断是否包含错误信息。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                            val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                            // 对错误信息进行处理。
                        }
                        //判断是否包含子错误码。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            val subErrorCode =
                                bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)
                            // 对子错误码进行处理。
                        }
                    }
                    callback(null)
                }
                else -> {
                    callback(null)
                }
            }
            return true
        } else {
            return false
        }
    }
}