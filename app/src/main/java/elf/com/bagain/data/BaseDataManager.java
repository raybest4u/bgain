package elf.com.bagain.data;

import android.content.Context;

import java.util.List;

/**
 * User：McCluskey Ray on 2015/11/10 15:38
 * email：lr8734@126.com
 */
public abstract class BaseDataManager {
    public BaseDataManager(Context context) {

    }
    public abstract void onDataLoaded(List<? extends BliDingItem> data);

    protected static void setPage(List<? extends Item> items, int page) {
        for (Item item : items) {
            item.page = page;
        }
    }
    protected static void setDataSource(List<? extends Item> items, String dataSource) {
        for (Item item : items) {
            item.dataSource = dataSource;
        }
    }
}
