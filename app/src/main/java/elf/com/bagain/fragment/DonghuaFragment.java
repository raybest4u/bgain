package elf.com.bagain.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import elf.com.bagain.R;
import elf.com.bagain.adapter.Categorydapter;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.Blibli.BlibliDingSearch;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.utils.URLUtil;
import elf.com.bagain.widget.recycleview.InfiniteScrollListener;


/**
 * Fragment页面
 *
 * @author wwj_748
 * @date 2014/8/9
 */
public class DonghuaFragment extends Fragment implements AbsListView.OnScrollListener {
    private RecyclerView videoListView;// 视频列表
    private View noBlogView; // 无数据时显示
    private Categorydapter adapter;// 列表适配器
	private List<BliDingItem> templist;

    private boolean isLoad = false; // 是否加载
    private int videoType = 1; // 视频类别
    private LinearLayout ll_loading;

   public static DonghuaFragment newInstance(int videoType) {
       DonghuaFragment fragment = new DonghuaFragment();
       Bundle args = new Bundle();
       args.putInt("videoType", videoType);
       fragment.setArguments(args);
       return fragment;
   }

    public DonghuaFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoType = getArguments().getInt("videoType");
        init();
    }

    //
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        	templist = new ArrayList<>();
        initComponent();
        ll_loading.setVisibility(View.VISIBLE);
        new MainTask().execute(URLUtil.getRefreshBlogListURL(videoType),
                "refresh");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("NewsFrag", "onCreateView");
        return inflater.inflate(R.layout.fragment_donghua, null);
    }

    // 初始化
    private void init() {

        //adapter = new VideoListAdapter(getActivity());
       /* page = new Page();
        page.setPageStart();*/
    }

    // 初始化组件
    private void initComponent() {
        ll_loading = (LinearLayout) getView().findViewById(R.id.ll_loading);
        videoListView = (RecyclerView) getView().findViewById(R.id.videoListView);
        noBlogView = getView().findViewById(R.id.noBlogLayout);
        adapter = new Categorydapter(getActivity());
        videoListView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        videoListView.setLayoutManager(layoutManager);
        videoListView.setHasFixedSize(true);
        videoListView.addOnScrollListener(new InfiniteScrollListener(layoutManager, getComments) {
            @Override
            public void onLoadMore() {
                // bAdapter.
                //dataManager.loadAllDataSources();
                ll_loading.setVisibility(View.VISIBLE);
                ++page;
                new MainTask().execute(URLUtil.getRefreshBlogListURL(videoType)+"?page="+page);
            }
        });
    }
    int page = 1;
    private boolean isloadding = false;
    DataLoadingSubject getComments = new DataLoadingSubject() {
        @Override
        public boolean isDataLoading() {
            return isloadding;
        }
    };
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 处理listView加载更多
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    private int mListViewPreLast = 0;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;

        if (lastItem == totalItemCount) {
            if (mListViewPreLast != lastItem) { /**防止多次调用*/


                new MainTask()
                        .execute(
                                URLUtil.getVideoListURL(videoType),
                                "load");

                mListViewPreLast = lastItem;

            }
        }
    }

    private List<BliDingItem> items;
    private class MainTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            items =    BlibliDingSearch.getCategory(params[0]);
			 return 0;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // 通知列表数据更新
            adapter.addAndResort(items);
			adapter.notifyDataSetChanged();
            ll_loading.setVisibility(View.GONE);
			super.onPostExecute(result);

        }


        public String getDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm",
                    Locale.CHINA);
            return sdf.format(new java.util.Date());
        }

    }

}