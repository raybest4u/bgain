package elf.com.bagain.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import elf.com.bagain.R;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.Page;


/**
 * Fragment页面
 *
 * @author wwj_748
 * @date 2014/8/9
 */
public class DonghuaFragment extends Fragment implements AbsListView.OnScrollListener {
    private ListView videoListView;// 视频列表
    private View noBlogView; // 无数据时显示
    //private VideoListAdapter adapter;// 列表适配器
	private List<BliDingItem> templist;

    private boolean isLoad = false; // 是否加载
    private int videoType = 1; // 视频类别
    private Page page; // 页面引用
    private LinearLayout ll_loading;

    private String refreshDate = ""; // 刷新日期
   /* public DonghuaFragment() {
    }
    public DonghuaFragment(int videoType) {
        super();
        this.videoType = videoType;
    }*/
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
        init();
    }

    //
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        	templist = new ArrayList<>();
        //	VideoItem tempItem = new VideoItem();
//		tempItem.setAid("7");
//		tempItem.setTitle("[示例数据]童年动画主题曲");
//		tempItem.setPic("http://i0.hdslb.com/320_180/u_user/53cb3e2f7f3efd6464b82c91ea9a1236.jpg");
//		tempItem.setAuthor("根号⑨");
//		tempItem.setPlay("23333");
//		templist.add(tempItem);
        initComponent();
       /* if (isLoad == false) {
            isLoad = true;*/
            // 加载数据库中的数据
        /*	List<VideoItem> list = templist;
			adapter.setList(list);
			adapter.notifyDataSetChanged();*/

//			videoListView.startRefresh(); // 开始刷新
		/*new MainTask().execute(URLUtil.getRefreshBlogListURL(videoType),
				"refresh");*/
        /*} else {
//			videoListView.NotRefreshAtBegin(); // 不开始刷新
        }*/
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
        //page = new Page();
        //page.setPageStart();
    }

    // 初始化组件
    private void initComponent() {
        ll_loading = (LinearLayout) getView().findViewById(R.id.ll_loading);
        videoListView = (ListView) getView().findViewById(R.id.videoListView);
        //videoListView.setAdapter(adapter);// 设置适配器
        videoListView.setOnScrollListener(this);
//		videoListView.setPullRefreshEnable(this);// 设置可下拉刷新
//		videoListView.setPullLoadEnable(this);// 设置可上拉加载
        // 设置列表项点击事件
        videoListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 获得博客列表项
                //VideoItem item = (VideoItem) adapter.getItem(position - 1);
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                //bundle.putSerializable("videoItemdata", item);
                //i.setClass(getActivity(), VideoInfoActivity.class);
                i.putExtras(bundle);
                startActivity(i);
                // 动画过渡
                getActivity().overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_no);
            }
        });

        noBlogView = getView().findViewById(R.id.noBlogLayout);
    }

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
                                //URLUtil.getVideoListURL(videoType),
                                "load");

                mListViewPreLast = lastItem;

            }
        }
    }

    private class MainTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {


			 return 0;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // 通知列表数据更新
		/*	adapter.notifyDataSetChanged();
			switch (result) {
			case Constants.DEF_RESULT_CODE.ERROR: // 错误
				Toast.makeText(getActivity(), "网络信号不佳", Toast.LENGTH_LONG).show();
				break;
			case Constants.DEF_RESULT_CODE.NO_DATA: // 无数据
				// Toast.makeText(getActivity(), "无更多加载内容", Toast.LENGTH_LONG)
				// .show();
//				videoListView.stopLoadMore();
				// noBlogView.setVisibility(View.VISIBLE); // 显示无博客
				break;
			case Constants.DEF_RESULT_CODE.REFRESH: // 刷新
//				videoListView.stopRefresh(getDate());

                ll_loading.setVisibility(View.GONE);
				if (adapter.getCount() == 0) {
					noBlogView.setVisibility(View.VISIBLE); // 显示无博客
				}
				break;
			case Constants.DEF_RESULT_CODE.LOAD:
//				videoListView.stopLoadMore();
				page.addPage();
				if (adapter.getCount() == 0) {
					noBlogView.setVisibility(View.VISIBLE); // 显示无博客
				}
				break;
			default:
				break;
			}
			super.onPostExecute(result);
		}*/

        }


        public String getDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm",
                    Locale.CHINA);
            return sdf.format(new java.util.Date());
        }

    }

}