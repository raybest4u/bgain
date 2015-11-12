package elf.com.bagain.data.Blibli;

import android.support.annotation.WorkerThread;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.utils.XLog;

/**
 * User：McCluskey Ray on 2015/11/11 11:17
 * email：lr8734@126.com
 */
public class BlibliDingSearch {


    @WorkerThread
    public static List<BliDingItem> ding(String query) {
        List<BliDingItem> dings = new ArrayList<>();
        try {
            JSONObject bangumijson =  new JSONObject(downloadPage("http://www.bilibili.com/index/ding.json"));

            JSONObject bangumiarray=bangumijson.getJSONObject("bangumi");
            for (int i=0;i<bangumiarray.length();i++) {
                BliDingItem item = new BliDingItem(
                        bangumiarray.getJSONObject(i + "").getInt("aid"),
                        bangumiarray.getJSONObject(i+"").getString("title").toString(),
                        bangumiarray.getJSONObject(i+"").getString("pic").toString(),
                        bangumiarray.getJSONObject(i+"").getInt("typeid"),
                        bangumiarray.getJSONObject(i+"").getString("subtitle").toString(),
                        bangumiarray.getJSONObject(i+"").getInt("play"),
                        bangumiarray.getJSONObject(i+"").getInt("review"),
                        bangumiarray.getJSONObject(i+"").getInt("video_review"),
                        bangumiarray.getJSONObject(i+"").getInt("favorites"),
                        bangumiarray.getJSONObject(i+"").getInt("mid"),
                        bangumiarray.getJSONObject(i+"").getString("author"),
                        bangumiarray.getJSONObject(i+"").getString("description"),
                        bangumiarray.getJSONObject(i+"").getString("create"),
                        bangumiarray.getJSONObject(i+"").getInt("pubdate"),
                        bangumiarray.getJSONObject(i+"").getInt("credit"),
                        bangumiarray.getJSONObject(i+"").getInt("coins"),
                        bangumiarray.getJSONObject(i+"").getString("duration")
                );
                dings.add(item);
            }



        }catch (Exception e){
            XLog.e("-->",e);
        }

        return dings;
    }

    private static String downloadPage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            XLog.d(response.body().toString());
            return response.body().string();
        } catch (IOException ioe) {
            return null;
        }
    }
}
