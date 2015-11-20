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
            XLog.d("--->"+bangumijson.toString());
            JSONObject bliarray=bangumijson.getJSONObject("douga");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("music");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("game");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("ent");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("teleplay");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("bangumi");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("movie");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("technology");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("kichiku");
            pushToArray(bliarray,dings);
            bliarray=bangumijson.getJSONObject("dance");
            pushToArray(bliarray,dings);


        }catch (Exception e){
            XLog.e("-->",e);
        }

        return dings;
    }
    private static void pushToArray(JSONObject bliarray,List<BliDingItem> dings)throws Exception{
        for (int i=0;i<bliarray.length();i++) {
            BliDingItem item = new BliDingItem(
                    bliarray.getJSONObject(i + "").getInt("aid"),
                    bliarray.getJSONObject(i+"").getString("title").toString(),
                    bliarray.getJSONObject(i+"").getString("pic").toString(),
                    bliarray.getJSONObject(i+"").getInt("typeid"),
                    bliarray.getJSONObject(i+"").getString("subtitle").toString(),
                    bliarray.getJSONObject(i+"").getString("play"),
                    bliarray.getJSONObject(i+"").getInt("review"),
                    bliarray.getJSONObject(i+"").getInt("video_review"),
                    bliarray.getJSONObject(i+"").getInt("favorites"),
                    bliarray.getJSONObject(i+"").getInt("mid"),
                    bliarray.getJSONObject(i+"").getString("author"),
                    bliarray.getJSONObject(i+"").getString("description"),
                    bliarray.getJSONObject(i+"").getString("create"),
                    bliarray.getJSONObject(i+"").getInt("pubdate"),
                    bliarray.getJSONObject(i+"").getInt("credit"),
                    bliarray.getJSONObject(i+"").getInt("coins"),
                    bliarray.getJSONObject(i+"").getString("duration")
            );
            dings.add(item);
        }
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
