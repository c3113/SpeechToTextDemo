package com.hilary.speech

import android.app.Application
import com.huawei.hms.mlsdk.common.MLApplication

/**
 * @description:
 * @author ChenKai
 * @date 2022/12/26
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MLApplication.initialize(applicationContext);// 多进程调用。
        MLApplication.getInstance().apiKey = "DAEDAOZ6q5ok+94u25S1EUv6LdfwQ/8ZX5w/iL9bUeLi+QscdJMu0Zw+9SSGegjq7IK0z5HeA3EEUYMAZgwQ1HxBYqAeyuHg/ih21w=="
        MLApplication.getInstance().setUserRegion(MLApplication.REGION_DR_CHINA)
    }
}