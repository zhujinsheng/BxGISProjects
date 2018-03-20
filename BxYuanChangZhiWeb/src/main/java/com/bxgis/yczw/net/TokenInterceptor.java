package com.bxgis.yczw.net;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by xiaozhu on 2018/1/4.
 */

public class TokenInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private Context mContext;
    public TokenInterceptor(){}
    public TokenInterceptor(Context context){
        this.mContext =context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        //找到了这个曲线方法,取到请求完成的数据后,根据特定的判断条件去判断token过期
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // 缓存最大.
        Buffer buffer = source.buffer(); //缓存区
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        String bodyString = buffer.clone().readString(charset);
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            boolean  isValid = jsonObject.isNull("__status");
            boolean  isValid1 = jsonObject.isNull("data");
            if(isValid){
                int __status = jsonObject.getInt("__status");
                Log.d("拦截返回内容返回的状态码 = ", String.valueOf(__status));
            }
            int __status = jsonObject.getInt("__status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("拦截返回内容 body---------->",  bodyString);
//        if (response shows expired token){//根据和服务端的约定判断token过期
//            response.body().close();
//        }
        return response;
    }
}
