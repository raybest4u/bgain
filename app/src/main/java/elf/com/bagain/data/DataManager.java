package elf.com.bagain.data;

import android.content.Context;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import elf.com.bagain.adapter.FilterAdapter;
import elf.com.bagain.data.Blibli.BlibliDingSearch;

/**
 * User：McCluskey Ray on 2015/11/10 15:36
 * email：lr8734@126.com
 */
public class DataManager extends BaseDataManager implements DataLoadingSubject {

    private AtomicInteger loadingCount;
    private final FilterAdapter filterAdapter;
    private Map<String, Integer> pageIndexes;
    /**
     * @param filterAdapter
     */
    public DataManager(Context context,
                       FilterAdapter filterAdapter) {
        super(context);
        this.filterAdapter = filterAdapter;
        loadingCount = new AtomicInteger(0);
        setupPageIndexes();

    }
    private void setupPageIndexes() {
        List<Source> dateSources = filterAdapter.getFilters();
        pageIndexes = new HashMap<>(dateSources.size());
        for (Source source : dateSources) {
            pageIndexes.put(source.key, 0);
        }
    }
    public void loadAllDataSources() {
        for (Source filter : filterAdapter.getFilters()) {
            loadSource(filter);
        }
    }

    private void loadSource(Source source) {
        if (source.active) {
            loadingCount.incrementAndGet();
            int page = getNextPageIndex(source.key);
            switch (source.key) {
                default:loadBliDing(( Source.DingSearchSource )source, page);
            }
        }
    }
    private void loadBliDing(final Source.DingSearchSource source, final int page) {
        new AsyncTask<Void, Void, List<BliDingItem>>() {
            @Override
            protected List<BliDingItem> doInBackground(Void... params) {
                return BlibliDingSearch.ding(source.query);
            }

            @Override
            protected void onPostExecute(List<BliDingItem> shots) {
                if (shots != null && shots.size() > 0 ) {
                    setPage(shots, page);
                    setDataSource(shots, source.key);
                    onDataLoaded(shots);
                }
                loadingCount.decrementAndGet();
            }
        }.execute();
    }

    private int getNextPageIndex(String dataSource) {
        int nextPage = 1; // default to one – i.e. for newly added sources
        if (pageIndexes.containsKey(dataSource)) {
            nextPage = pageIndexes.get(dataSource) + 1;
        }
        pageIndexes.put(dataSource, nextPage);
        return nextPage;
    }
    @Override
    public void onDataLoaded(List<? extends BliDingItem> data) {

    }

    @Override
    public boolean isDataLoading() {
        return loadingCount.get() > 0;
    }
}
