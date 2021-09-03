package com.daqsoft.module_main.service

import java.io.File

/**
 * @package name：com.daqsoft.module_main.service
 * @date 25/1/2021 下午 5:13
 * @author zp
 * @describe
 */
interface UpdateProgressListener {
     fun onCommence()
     fun progress(progress: Long, total: Long)
     fun onSuccess(file:File)
     fun onError(e: Throwable?)
     fun onCompleted()
}

