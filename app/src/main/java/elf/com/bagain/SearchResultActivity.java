package elf.com.bagain;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.adapter.BAdapter;
import elf.com.bagain.adapter.SearchBAdapter;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.Blibli.BlibliDingSearch;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.utils.ImmersiveUtil;
import elf.com.bagain.utils.SimpleTransitionListener;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.view.swipeback.SwipeBackActivity;
import elf.com.bagain.widget.recycleview.InfiniteScrollListener;
import ooo.oxo.library.widget.PullBackLayout;

public class SearchResultActivity extends SwipeBackActivity implements PullBackLayout.Callback {
    @Bind(R.id.rv_list)
    RecyclerView grid;
    @Bind(R.id.ll_root)
     PullBackLayout ll_root;


    private ColorDrawable background;
    private GridLayoutManager layoutManager;
    private SearchBAdapter bAdapter;
    private boolean isLoading = false;
    private int page = 1;
    private String   keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        background = new ColorDrawable(Color.parseColor("#99db77ab"));
        ll_root.setCallback(this);
        ll_root.setBackground(background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().addListener(new SimpleTransitionListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onTransitionEnd(Transition transition) {
                    getWindow().getEnterTransition().removeListener(this);
                    fadeIn();
                }
            });
        } else {
            fadeIn();
        }
        bAdapter = new SearchBAdapter(this);
        grid.setAdapter(bAdapter);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == bAdapter.getDataItemCount() ? 2 : 1;
            }
        });

        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);
        grid.addOnScrollListener(new InfiniteScrollListener(layoutManager, new DataLoadingSubject() {
            @Override
            public boolean isDataLoading() {
                return isLoading;
            }
        }) {
            @Override
            public void onLoadMore() {
                // bAdapter.
                //dataManager.loadAllDataSources();

                ++page;
                if(page<=BlibliDingSearch.pages)
                new VideoInfoTask().execute(keyword, "loadMore");
            }

            @Override
            public void onRefresh() {

            }
        });
           keyword =  getIntent().getStringExtra("keyword");
        new VideoInfoTask().execute(keyword,"refresh");
    }
    private void showSystemUi() {
        ImmersiveUtil.exit(ll_root);
    }

    private void hideSystemUi() {
        ImmersiveUtil.enter(ll_root);
    }
    @Override
    public void onPullStart() {
        fadeOut();
        showSystemUi();
    }

    @Override
    public void onPull(float progress) {
        progress = Math.min(1f, progress * 3f);
        background.setAlpha((int) (0xff * (1f - progress)));
    }

    @Override
    public void onPullCancel() {
        fadeIn();
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }
    void fadeIn() {

        showSystemUi();
    }

    void fadeOut() {
        hideSystemUi();
    }
    @Override
    public void supportFinishAfterTransition() {
        Intent data = new Intent();
        // data.putExtra("index",index);
        setResult(RESULT_OK, data);

        showSystemUi();

        super.supportFinishAfterTransition();
    }


    List<BliDingItem> resultList;
    private class VideoInfoTask extends AsyncTask<String, Void, Integer> {
        String label;

        @Override
        protected Integer doInBackground(String... arg0) {
            isLoading = true;
            resultList =  BlibliDingSearch.search(arg0[0],page);
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stubs
            super.onPostExecute(result);
            if(resultList!=null)
                bAdapter.addAndResort(resultList);

            isLoading = false;
        }
    }


}
