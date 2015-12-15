package elf.com.bagain;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;

import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.adapter.Descdapter;
import elf.com.bagain.data.BiliComment;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.Blibli.BlibliDingSearch;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.utils.ImmersiveUtil;
import elf.com.bagain.utils.SimpleTransitionListener;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.view.swipeback.SwipeBackActivity;
import elf.com.bagain.widget.recycleview.InfiniteScrollListener;
import ooo.oxo.library.widget.PullBackLayout;

public class BliDecActivity  extends SwipeBackActivity implements PullBackLayout.Callback {
    public final static String EXTRA_SHOT = "shot";
    @Bind(R.id.puller)
    PullBackLayout puller;
    @Bind(R.id.stories_grid)
    RecyclerView grid;


    private ColorDrawable background;
    private int index;
    private BliDingItem bliDing;
    private Descdapter   bAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bli_dec);
        ButterKnife.bind(this);
        puller.setCallback(this);
        index =  getIntent().getIntExtra("index", 0);
        bliDing = getIntent().getParcelableExtra(EXTRA_SHOT);
        Gson gson = new Gson();
        XLog.d(gson.toJson(bliDing));
        background = new ColorDrawable(Color.parseColor("#db77ab"));
        puller.setBackground(background);
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
        bAdapter = new Descdapter(this,bliDing);
        grid.setAdapter(bAdapter);
        LinearLayoutManager  layoutManager = new LinearLayoutManager(this);
        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);
        grid.addOnScrollListener(new InfiniteScrollListener(layoutManager, getComments) {
            @Override
            public void onLoadMore() {
                // bAdapter.
                //dataManager.loadAllDataSources();
                ++page;
                if(page<=BlibliDingSearch.pages)
                new VideoInfoTask().execute();
            }
        });
        new VideoInfoTask().execute();
    }
    private boolean isloadding = false;
    DataLoadingSubject getComments = new DataLoadingSubject() {
        @Override
        public boolean isDataLoading() {
            return isloadding;
        }
    };
    void fadeIn() {

        showSystemUi();
    }

    void fadeOut() {
        hideSystemUi();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ABPlayerApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private void showSystemUi() {
        ImmersiveUtil.exit(puller);
    }

    private void hideSystemUi() {
        ImmersiveUtil.enter(puller);
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
    @Override
    public void supportFinishAfterTransition() {
        Intent data = new Intent();
        data.putExtra("index",index);
        setResult(RESULT_OK, data);

        showSystemUi();

        super.supportFinishAfterTransition();
    }

    private List<BiliComment> comments;
    private List<BliDingItem> tags;
    private int page = 1;
    private boolean hadAddTags = false;
    private class VideoInfoTask extends AsyncTask<String, Void, Integer> {
        String label;

        @Override
        protected Integer doInBackground(String... arg0) {
            isloadding = true;
            comments =  BlibliDingSearch.getBiliComment(bliDing.aid, page);
            if(!hadAddTags)
            tags = BlibliDingSearch.getTags(bliDing.aid);

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stubs
            super.onPostExecute(result);
            if(comments!=null)
            bAdapter.addAndResort(comments);
            if(!hadAddTags&&tags!=null){
                hadAddTags = true;
                bAdapter.addTags(tags);
            }
            isloadding = false;
        }
    }
}
