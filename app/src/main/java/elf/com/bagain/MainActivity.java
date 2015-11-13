package elf.com.bagain;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

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

public class MainActivity extends Activity {
   /* @Bind(R.id.toolbar)
    Toolbar toolbar;*/
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
                return position == bAdapter.getDataItemCount() ? columns : 1;
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
    }
    private void checkEmptyState() {
        //TODO:显示空状态
    }
}
