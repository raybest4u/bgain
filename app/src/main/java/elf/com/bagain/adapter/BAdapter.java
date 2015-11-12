package elf.com.bagain.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.BliDecActivity;
import elf.com.bagain.R;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.data.Item;

/**
 * User：McCluskey Ray on 2015/11/10 13:52
 * email：lr8734@126.com
 */
public class BAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BLIBLI_ITEM = 0;
    private static final int TYPE_BLIBLI_CATEGORY = 1;
    private static final int TYPE_LOADING_MORE = -1;
    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private @Nullable
    DataLoadingSubject dataLoading;

    private List<Item> items;

    public BAdapter(Activity hostActivity,DataLoadingSubject dataLoading){
        this.host = hostActivity;
        this.dataLoading = dataLoading;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BLIBLI_ITEM:
                return new BiliDingHolder(layoutInflater.inflate(R.layout.blibli_ding_item, parent, true));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));
        }

        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            Item item = getItem(position);
            if(item instanceof BliDingItem){
                bindBiliDing((BliDingItem) getItem(position), (BiliDingHolder) holder);
            }
        }else {
            bindLoadingViewHolder((LoadingMoreHolder) holder, position);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            Item item = getItem(position);
            if (item instanceof BliDingItem) {
                return TYPE_BLIBLI_ITEM;
            }
        }
        return TYPE_LOADING_MORE;
    }
    public void addAndResort(Collection<? extends BliDingItem> newItems) {
        boolean add = true;
        this.items.addAll(newItems);
       /* for (BliDingItem newItem : newItems) {
            if (add) {
                add(newItem);
                add = true;
            }
        }
*/
        notifyDataSetChanged();
    }

    private void bindBiliDing(final BliDingItem bliDing, final BiliDingHolder holder) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // iv.setTransitionName(iv.getResources().getString(R.string.transition_shot));
                holder.pocket.setBackgroundColor(
                        ContextCompat.getColor(host, R.color.background_light));
                Intent intent = new Intent();
                intent.setClass(host, BliDingItem.class);
                intent.putExtra(BliDecActivity.EXTRA_SHOT, bliDing);
                /*ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(host,
                                Pair.create(view, host.getString(R.string.transition_shot)),
                                Pair.create(view, host.getString(R.string
                                        .transition_shot_background)));*/
                host.startActivity(intent);//, options.toBundle());
            }
        });

    }
    private void bindLoadingViewHolder(LoadingMoreHolder holder, int position) {
        // only show the infinite load progress spinner if there are already items in the
        // grid i.e. it's not the first item & data is being loaded
        holder.progress.setVisibility(position > 0 && dataLoading.isDataLoading() ?
                View.VISIBLE : View.INVISIBLE);
    }
    @Override
    public int getItemCount() {
        // include loading footer
        return getDataItemCount() + 1;
    }
    private Item getItem(int position) {
        return items.get(position);
    }
    public int getDataItemCount() {
        return items.size();
    }
    private void add(Item item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class BiliDingHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.story_title)
        TextView title;
        @Bind(R.id.story_comments) TextView comments;
        @Bind(R.id.pocket)
        ImageView pocket;

        public BiliDingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    class LoadingMoreHolder extends RecyclerView.ViewHolder {

        ProgressBar progress;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView;
        }

    }
}
