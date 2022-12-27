package com.hilary.speech

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.hilary.speech.composable.ActionsComposable
import com.hilary.speech.ui.theme.SpeechToTextDemoTheme


class MainActivity : ComponentActivity() {
    lateinit var speechToTextHelper: SpeechToTextHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpeechToTextDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ActionsComposable({
                        if (it == 1) {
                            startRecord()
                        } else {
                            startRecord2()
                        }

                    }, {
                        stopRecord()
                    })
                }
            }
        }
        speechToTextHelper = SpeechToTextHelper(this)
        speechToTextHelper.initRecognizer{ recognizing, recognizingResult, result ->
            println("##############start###############")
            println("####recognizing : $recognizing")
            println("####recognizingResult : $recognizingResult")
            println("####result : $result")
            println("#################end############")
        }
    }

//    private fun initRecognizer() {
//        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(this)
//        mSpeechRecognizer.setAsrListener(SpeechRecognitionListener())
//    }

    private fun startRecord() {
        speechToTextHelper.startRecognizing()
//        // 新建Intent，用于配置语音识别参数。
//        val mSpeechRecognizerIntent = Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH)
//        // 通过Intent进行语音识别参数设置。
//        mSpeechRecognizerIntent
//        // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar":阿拉伯语；"th=TH"：泰语；"ms-MY"：马来语；"fil-PH"：菲律宾语；"tr-TR"：土耳其语。
//            .putExtra(MLAsrConstants.LANGUAGE, "zh-CN") // 设置识别文本返回模式为边识别边出字，若不设置，默认为边识别边出字。支持设置：
//            // MLAsrConstants.FEATURE_WORDFLUX：通过onRecognizingResults接口，识别同时返回文字；
//            // MLAsrConstants.FEATURE_ALLINONE：识别完成后通过onResults接口返回文字。
//            .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX) // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
////            .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)
//        // 启动语音识别。
//        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent)
    }

    private fun stopRecord() {
        println("#####stopRecord####")
//        mSpeechRecognizer.destroy()
        speechToTextHelper.destroy()
    }

    private fun  startRecord2() {
        println("#####startRecord2####")
        speechToTextHelper.startHWUIRecognizing(this)
//         通过intent进行识别设置。
//        val intent = Intent(this, MLAsrCaptureActivity::class.java)
//            // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar": 阿拉伯语；"ru-RU":俄语；“th_TH”：泰语；“ms_MY”：马来语；“fil_PH”：菲律宾语；"tr-TR"：土耳其语。
//            .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
//            // 设置拾音界面是否显示识别结果，MLAsrCaptureConstants.FEATURE_ALLINONE为不显示，MLAsrCaptureConstants.FEATURE_WORDFLUX为显示。
//            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
//            // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
//            .putExtra(MLAsrConstants.SCENES,
//                MLAsrConstants.SCENES_SHOPPING)
//
//        // REQUEST_CODE_ASR表示当前Activity和拾音界面Activity之间的请求码，通过该码可以在当前Activity中获取拾音界面的处理结果。
//        startActivityForResult(intent, REQUEST_CODE_ASR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        speechToTextHelper.onActivityResult({ message ->
            println("####result : $message")
        }, requestCode, resultCode, data)
    }

}

