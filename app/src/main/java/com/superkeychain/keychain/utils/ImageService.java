package com.superkeychain.keychain.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by taofeng on 4/29/16.
 */
public class ImageService {

    private String host = Action.HOST;

    private  String protocol = "http://";

    private Context context;

    AsyncImageLoader loader;

    public String relURLtoAbs(String url){
        return protocol+host+url;
    }

    private TaskFinishedListener listener;

    public ImageService(Context context, TaskFinishedListener listener){
        this.context = context;
        this.listener = listener;
        this.loader = new AsyncImageLoader(context);
    }

    public void get(String url) {
        String imgUrl = relURLtoAbs(url);
        //将图片缓存至外部文件中
        loader.setCache2File(true); //false
        //设置外部缓存文件夹
        loader.setCachedDir(context.getCacheDir().getAbsolutePath());

        //下载图片，第二个参数是否缓存至内存中
        loader.downloadImage(imgUrl, true/*false*/, new AsyncImageLoader.ImageCallback() {
            @Override
            public void onImageLoaded(Bitmap bitmap, String imageUrl) {
                if (bitmap != null) {
                    listener.doFinished(bitmap);
                } else {

                }
            }
        });
    }

    public interface TaskFinishedListener{
        void doFinished(Bitmap data);
    }

}
