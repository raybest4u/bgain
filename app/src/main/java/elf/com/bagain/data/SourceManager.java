package elf.com.bagain.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import elf.com.bagain.R;

/**
 * User：McCluskey Ray on 2015/11/11 11:26
 * email：lr8734@126.com
 */
public class SourceManager {
    public static final String SOURCE_BLIBLI_DING = "SOURCE_BLIBLI_DING";
    public static List<Source> getSources(Context context) {


        return  getDefaulSources(context);
    }
    private static  ArrayList<Source> getDefaulSources(Context context){
        ArrayList<Source> defaultSources = new ArrayList<>(1);
        defaultSources.add(new Source.DingSearchSource(
                context.getString(R.string.app_name),true));
        return defaultSources;
    }
}
