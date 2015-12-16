package elf.com.bagain.data.Blibli;

import android.support.annotation.WorkerThread;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elf.com.bagain.data.BannerItem;
import elf.com.bagain.data.BiliComment;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.utils.HttpUtil;
import elf.com.bagain.utils.XLog;

/**
 * User：McCluskey Ray on 2015/11/11 11:17
 * email：lr8734@126.com
 */
public class BlibliDingSearch {

    public static List<BannerItem> loadBanner(){
        ArrayList<BannerItem> listtemp = new ArrayList<>();
        JSONObject bannerjson;
        try {
            bannerjson = new JSONObject(HttpUtil.getHtmlString("http://www.bilibili.com/index/slideshow.json"));
            JSONArray array = bannerjson.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {

                BannerItem item = new BannerItem();
                item.setImg(array.getJSONObject(i).getString("img").toString());
                item.setTitle(array.getJSONObject(i).getString("title").toString());
                item.setLink(array.getJSONObject(i).getString("link").toString());
                item.setAd(false);
                listtemp.add(item);
            }
        }catch (Exception e){
            XLog.e(e);
        }
        return listtemp;
    }

    @WorkerThread
    public static List<BliDingItem> ding(String query) {
        List<BliDingItem> dings = new ArrayList<>();
        try {
            JSONObject bangumijson =  new JSONObject(downloadPage("http://www.bilibili.com/index/ding.json"));
            XLog.d("--->" + bangumijson.toString());
            BliDingItem header = new BliDingItem("动画",1);
            dings.add(header);
            JSONObject bliarray=bangumijson.getJSONObject("douga");
            pushToArray(bliarray,dings);
            header = new BliDingItem("音乐",3);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("music");
            pushToArray(bliarray, dings);
            header = new BliDingItem("游戏",4);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("game");
            pushToArray(bliarray, dings);
            header = new BliDingItem("娱乐",5);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("ent");
            pushToArray(bliarray, dings);
            header = new BliDingItem("电视剧",11);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("teleplay");
            pushToArray(bliarray, dings);
            header = new BliDingItem("番组计划",13);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("bangumi");
            pushToArray(bliarray, dings);
            header = new BliDingItem("电影",23);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("movie");
            pushToArray(bliarray, dings);
            header = new BliDingItem("科学·技术",36);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("technology");
            pushToArray(bliarray, dings);
            header = new BliDingItem("鬼畜",119);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("kichiku");
            pushToArray(bliarray, dings);
            header = new BliDingItem("舞蹈",129);
            dings.add(header);
            bliarray=bangumijson.getJSONObject("dance");
            pushToArray(bliarray,dings);


        }catch (Exception e){
            XLog.e("-->",e);
        }

        return dings;
    }
    @WorkerThread
    public static List<BliDingItem> search(String query,int page) {
        List<BliDingItem> dings = new ArrayList<>();
        try {
            String url = "http://www.bilibili.com/search?action=autolist&main_ver=v2&pagesize=20&keyword="+query+"&page="+page+"&order=&tids=1&type=";
            JSONObject jsonObject =  new JSONObject(downloadPage(url));
            JSONArray bliarray=jsonObject.getJSONObject("res").getJSONArray("result");
            XLog.d("search with url -->",url);
            pages = jsonObject.getJSONObject("res").getInt("total");
            for (int i=0,length = bliarray.length();i<length;i++) {
                BliDingItem item = new BliDingItem(
                        bliarray.getJSONObject(i ).getInt("id"),
                        bliarray.getJSONObject(i).getString("title"),
                        bliarray.getJSONObject(i).getString("pic"),
                        11,
                        bliarray.getJSONObject(i).getString("tag"),
                        bliarray.getJSONObject(i).getString("play"),
                        0,
                        bliarray.getJSONObject(i).getInt("video_review"),
                        bliarray.getJSONObject(i).getInt("favorites"),
                        bliarray.getJSONObject(i).getInt("mid"),
                        bliarray.getJSONObject(i).getString("author"),
                        bliarray.getJSONObject(i).getString("description"),
                        "",
                        bliarray.getJSONObject(i).getInt("pubdate"),
                        0,
                        0,
                        ""
                );
                dings.add(item);
            }

        }catch (Exception e){
            XLog.e("-->",e);
        }
        return dings;
    }

    @WorkerThread
    public static List<BliDingItem> getCategory(String query) {
        List<BliDingItem> dings = new ArrayList<>();
        try {
            XLog.d("get date with url -->",query);
            JSONObject donghuajson = new JSONObject(HttpUtil.getHtmlString(query));
            JSONArray bliarray=donghuajson.getJSONArray("list");
            for (int i=0;i<bliarray.length();i++) {
                BliDingItem item = new BliDingItem(
                        bliarray.getJSONObject(i ).getInt("aid"),
                        bliarray.getJSONObject(i).getString("title"),
                        bliarray.getJSONObject(i).getString("pic"),
                        bliarray.getJSONObject(i).getInt("typeid"),
                        bliarray.getJSONObject(i).getString("subtitle"),
                        bliarray.getJSONObject(i).getString("play"),
                        bliarray.getJSONObject(i).getInt("review"),
                        bliarray.getJSONObject(i).getInt("video_review"),
                        bliarray.getJSONObject(i).getInt("favorites"),
                        bliarray.getJSONObject(i).getInt("mid"),
                        bliarray.getJSONObject(i).getString("author"),
                        bliarray.getJSONObject(i).getString("description"),
                        bliarray.getJSONObject(i).getString("create"),
                        bliarray.getJSONObject(i).getInt("pubdate"),
                        bliarray.getJSONObject(i).getInt("credit"),
                        bliarray.getJSONObject(i).getInt("coins"),
                        bliarray.getJSONObject(i).getString("duration")
                );
                dings.add(item);
                //Log.d("TAG--->", "--->"+item.getTitle());
            }
        }catch (Exception e){
            XLog.e("-->",e);
        }
        return dings;
    }
    @WorkerThread
    public static List<BliDingItem> getCategory2(String query) {
        List<BliDingItem> dings = new ArrayList<>();
        try {
            XLog.d("get date with url -->",query);
            JSONObject json = new JSONObject(HttpUtil.getHtmlString(query));
            JSONObject donghuajson = json.getJSONObject("rank");
            JSONArray bliarray=donghuajson.getJSONArray("list");
            for (int i=0;i<bliarray.length();i++) {
                BliDingItem item = new BliDingItem(
                        bliarray.getJSONObject(i ).getInt("aid"),
                        bliarray.getJSONObject(i).getString("title"),
                        bliarray.getJSONObject(i).getString("pic"),
                        0,
                        "",
                        bliarray.getJSONObject(i).getString("play"),
                        0,
                        bliarray.getJSONObject(i).getInt("video_review"),
                        0,
                        bliarray.getJSONObject(i).getInt("mid"),
                        bliarray.getJSONObject(i).getString("author"),
                        bliarray.getJSONObject(i).getString("description"),
                        bliarray.getJSONObject(i).getString("create"),
                        0,
                        0,
                        bliarray.getJSONObject(i).getInt("coins"),
                        "x:xx"
                );
                dings.add(item);
                //Log.d("TAG--->", "--->"+item.getTitle());
            }
        }catch (Exception e){
            XLog.e("-->",e);
        }
        return dings;
    }
    private static void pushToArray(JSONObject bliarray,List<BliDingItem> dings)throws Exception{
        int length = bliarray.length()>5?4:bliarray.length();
        for (int i=0;i<length;i++) {
            BliDingItem item = new BliDingItem(
                    bliarray.getJSONObject(i + "").getInt("aid"),
                    bliarray.getJSONObject(i+"").getString("title"),
                    bliarray.getJSONObject(i+"").getString("pic"),
                    bliarray.getJSONObject(i+"").getInt("typeid"),
                    bliarray.getJSONObject(i+"").getString("subtitle"),
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
    private static void pushToArray2BiliComment(JSONObject bliarray,List<BiliComment> dings)throws Exception{
        for (int i=0;i<bliarray.length();i++) {
            BiliComment biliComment = new BiliComment();
            biliComment.mid = bliarray.getJSONObject(i+ "").getString("mid");
            biliComment.fbid = bliarray.getJSONObject(i+ "").getString("fbid");
            biliComment.device = bliarray.getJSONObject(i+ "").getString("device");
            biliComment.create_at = bliarray.getJSONObject(i+ "").getString("create_at");
            biliComment.face = bliarray.getJSONObject(i+ "").getString("face");
            biliComment.nick = bliarray.getJSONObject(i+ "").getString("nick");
            biliComment.sex = bliarray.getJSONObject(i+ "").getString("sex");
            biliComment.msg = bliarray.getJSONObject(i+ "").getString("msg");
            dings.add(biliComment);
        }
    }
    public static  int pages = 1;
    public static List<BiliComment> getBiliComment(int aid, int page){
        List<BiliComment> comments = new ArrayList<>();
        try {
            JSONObject commentsJson =  new JSONObject(downloadPage("http://api.bilibili.com/feedback?type=jsonp&ver=3&mode=aid&pagesize=20&page="+page+"&aid=" + aid));
            XLog.d("comments--->"+commentsJson.toString());
            pages = commentsJson.getInt("pages");
            JSONObject bliarray=commentsJson.getJSONObject("hotList");
            pushToArray2BiliComment(bliarray,comments);
            bliarray=commentsJson.getJSONObject("list");
            pushToArray2BiliComment(bliarray,comments);
        }catch (Exception e){
            XLog.e(e);
        }
        return comments;
    }
    public static List<BliDingItem> getTags(int aid){
        List<BliDingItem> items = new ArrayList<>();
        String url = "http://comment.bilibili.com/playtag,"+aid;
        try {
            //JSONObject commentsJson =  new JSONObject(downloadPage(url));
            JSONArray jsonArray = new JSONArray(downloadPage(url));
            XLog.d("playtag--->"+jsonArray.toString());
            JSONArray tempArray;
            BliDingItem tempItem;
            for (int i=0,j=jsonArray.length();i<j;++i){
                tempArray = jsonArray.getJSONArray(i);
                if(tempArray!=null){
                    XLog.d("--->"+tempArray.toString());
                    tempItem = new BliDingItem(tempArray.getString(2));
                    tempItem.play = tempArray.getString(3);
                    tempItem.aid = tempArray.getInt(1);
                    tempItem.pic = tempArray.getString(0);
                    tempItem.description = tempArray.getString(12);
                    tempItem.duration = tempArray.getString(7);
                    tempItem.favorites = tempArray.getInt(4);
                    tempItem.video_review = tempArray.getInt(6);
                    items.add(tempItem);
                }
            }
        }catch (Exception e){
            XLog.e(e);
        }
        return items;
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
