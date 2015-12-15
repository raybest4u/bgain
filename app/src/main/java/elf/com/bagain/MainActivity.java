package elf.com.bagain;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import elf.com.bagain.adapter.BAdapter;
import elf.com.bagain.adapter.FilterAdapter;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.DataManager;
import elf.com.bagain.data.SourceManager;
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


    @BindInt(R.integer.num_columns) int columns;

    // data
    private DataManager dataManager;
    private BAdapter bAdapter;
    FilterAdapter filterAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        filterAdapter = new FilterAdapter(this, SourceManager.getSources(this));
        dataManager = new DataManager(this,filterAdapter){
            @Override
            public void onDataLoaded(List<? extends BliDingItem> data) {
                bAdapter.addAndResort(data);
                checkEmptyState();

            }
        };
        bAdapter = new BAdapter(this,dataManager);
        grid.setAdapter(bAdapter);
        layoutManager = new GridLayoutManager(this, columns);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position%5==0? columns : 1;
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
        });
        dataManager.loadAllDataSources();
       // test();


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
