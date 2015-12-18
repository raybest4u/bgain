package elf.com.bagain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import elf.com.bagain.adapter.BAdapter;
import elf.com.bagain.adapter.FilterAdapter;
import elf.com.bagain.data.BannerItem;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.DataManager;
import elf.com.bagain.data.SourceManager;
import elf.com.bagain.utils.AnimUtils;
import elf.com.bagain.utils.PreferenceUtils;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.widget.recycleview.InfiniteScrollListener;

public class MainActivity extends AppCompatActivity {
   /* @Bind(R.id.toolbar)
    Toolbar toolbar;*/
   @Bind(R.id.drawer)
   DrawerLayout drawer;
    @Bind(R.id.stories_grid)
    RecyclerView grid;
    private GridLayoutManager layoutManager;
    @Bind(android.R.id.empty)
    ProgressBar loading;
    @Bind(R.id.search_view)
    MaterialSearchView searchView;

    @Bind(R.id.toolbar_container)
    View container;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @BindInt(R.integer.num_columns) int columns;




    // data
    private DataManager dataManager;
    private BAdapter bAdapter;
    FilterAdapter filterAdapter;

    private String keywords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            animateToolbar();
        }

        filterAdapter = new FilterAdapter(this, SourceManager.getSources(this));
        dataManager = new DataManager(this,filterAdapter){
            @Override
            public void onDataLoaded(List<? extends BliDingItem> data) {
                bAdapter.addAndResort(data);
                checkEmptyState();

            }
            @Override
            public void onBannerLoaded(List<? extends BannerItem> data) {
                if(data!=null&data.size()>0)
                bAdapter.addBanner(data);
            }

        };
        bAdapter = new BAdapter(this,dataManager);
        grid.setAdapter(bAdapter);
        layoutManager = new GridLayoutManager(this, columns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                XLog.d("==>", "" + position);
                return position == 0 || (position - 1) % 5 == 0 ? columns : 1;
            }
        });
        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);
        grid.addOnScrollListener(new InfiniteScrollListener(layoutManager, dataManager) {
            @Override
            public void onLoadMore() {
                // bAdapter.
                //dataManager.loadAllDataSources();
            }

            @Override
            public void onRefresh() {

            }
        });
        dataManager.loadAllDataSources();
       // test();
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
          keywords = PreferenceUtils.getString("keywords","周星驰,AVI");
        keywords = keywords.length()>1024?"周星驰,AVI":keywords;
        searchView.setSuggestions(keywords.split(","));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                XLog.d("search-->", "--" + query);
                if (!TextUtils.isEmpty(query)) {
                    String newKeywords = keywords+","+query;
                    PreferenceUtils.put("keywords",newKeywords);
                    searchView.setSuggestions(newKeywords.split(","));
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", query);
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        checkConnectivity();
    }

    private void checkConnectivity() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            loading.setVisibility(View.GONE);
            ViewStub stub = (ViewStub) findViewById(R.id.stub_no_connection);
            ImageView iv = (ImageView) stub.inflate();
            final Drawable avd = getResources().getDrawable(R.mipmap.ic_btn_av_cancel_disabled);
                   // getDrawable(R.mipmap.ic_btn_av_cancel_disabled);
            iv.setImageDrawable(avd);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DonghuaActivity.class);
                intent.putExtra("AreaType", 8);
                MainActivity.this.startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void animateToolbar() {
        // this is gross but toolbar doesn't expose it's children to animate them :(
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900);
                    //.setInterpolator(AnimUtils.getMaterialInterpolator(this));
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200); // filter
            popAnim(actions.getChildAt(1), 700, 200); // overflow
        }
    }

    private void popAnim(View v, int startDelay, int duration) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator
                            .overshoot));
        }
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
    private void test(){
       /* Box box = new Box();
        Cat schrodingerCat = new Cat();
        box.hiddenCat = schrodingerCat;
        Docker.container = box;*/
    }
    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ABPlayerApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }
    private void checkEmptyState() {
        if (bAdapter.getDataItemCount() == 0) {
            loading.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            setNoFiltersEmptyTextVisibility(View.GONE);
        }
    }
   /* @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        supportPostponeEnterTransition();

        Bundle reenterState = new Bundle(data.getExtras());

        grid.smoothScrollToPosition(reenterState.getInt("index", 0));
        grid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                grid.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });
    }*/
    private TextView noFiltersEmptyText;
    private void setNoFiltersEmptyTextVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noFiltersEmptyText == null) {
                // create the no filters empty text
                ViewStub stub = (ViewStub) findViewById(R.id.stub_no_filters);
                noFiltersEmptyText = (TextView) stub.inflate();
                String emptyText = getString(R.string.no_filters_selected);
                int filterPlaceholderStart = emptyText.indexOf('\u08B4');
                int altMethodStart = filterPlaceholderStart + 3;
                SpannableStringBuilder ssb = new SpannableStringBuilder(emptyText);
                // show an image of the filter icon
                ssb.setSpan(new ImageSpan(this, R.drawable.bli_ding_item_background,
                                ImageSpan.ALIGN_BASELINE),
                        filterPlaceholderStart,
                        filterPlaceholderStart + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // make the alt method (swipe from right) less prominent and italic
                ssb.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(this, R.color.bili_red)),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                noFiltersEmptyText.setText(ssb);
                noFiltersEmptyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(GravityCompat.END);
                    }
                });
            }
            noFiltersEmptyText.setVisibility(visibility);
        } else if (noFiltersEmptyText != null) {
            noFiltersEmptyText.setVisibility(visibility);
        }

    }
}
/*
class Cat {
}
class Box {
    Cat hiddenCat;
}
class Docker {
    static Box container;
}*/
