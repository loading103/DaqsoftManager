package com.daqsoft.mvvmfoundation.http.interceptor;

import com.daqsoft.mvvmfoundation.http.download.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author zp
 * @package name：com.daqsoft.mvvmfoundation.http.interceptor
 * @date 25/1/2021 下午 2:55
 * @describe
 */
public class ProgressInterceptor  implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body()))
                .build();
    }
}
