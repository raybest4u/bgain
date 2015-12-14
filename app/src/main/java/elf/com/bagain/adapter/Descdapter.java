package elf.com.bagain.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.BiliVideoViewActivity;
import elf.com.bagain.R;
import elf.com.bagain.data.BiliComment;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.utils.glide.GlideCircleTransform;

/**
 * User：McCluskey Ray on 2015/11/10 13:52
 * email：lr8734@126.com
 */
public class Descdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BLIBLI_ITEM = 0;
    private static final int TYPE_BLIBLI_HEADER = 1;
    private static final int TYPE_LOADING_MORE = -1;
    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private BliDingItem bliDing;
    private List<BiliComment> items;

    public Descdapter(Activity hostActivity, BliDingItem bliDing){
        this.host = hostActivity;
        this.bliDing = bliDing;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BLIBLI_ITEM:
                return new BiliDingHolder(layoutInflater.inflate(R.layout.blibli_comment_item, parent, false));
            case TYPE_BLIBLI_HEADER:
               return  new HeaderHolder(layoutInflater.inflate(R.layout.vidio_desc, parent, false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));
            default:break;
        }

        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  HeaderHolder){
            bindHeaderViewHolder((HeaderHolder)holder,position);
        }else if(holder instanceof BiliDingHolder){
            if (position < getDataItemCount()
                    && getDataItemCount() > 0) {
                BiliComment item = getItem(position);
                if (item instanceof BiliComment) {
                    bindBiliDing((BiliComment) item, (BiliDingHolder) holder);
                }
            }
        }else if(holder instanceof LoadingMoreHolder){
                bindLoadingViewHolder((LoadingMoreHolder) holder, position);
        }


    }
    @Override
    public int getItemViewType(int position) {
         if(position<1){
             return TYPE_BLIBLI_HEADER;
         }else if (position < getDataItemCount()
                && getDataItemCount() > 0) {
             BiliComment item = getItem(position);
            if (item instanceof BiliComment) {
                return TYPE_BLIBLI_ITEM;
            }
        }else if(position > 1) {
          return TYPE_LOADING_MORE;
         }
        return TYPE_BLIBLI_HEADER;

    }
    public void addAndResort(Collection<? extends BiliComment> newItems) {
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    private void bindBiliDing(final BiliComment bliDing, final BiliDingHolder holder) {
        Glide.with(host)
                .load(bliDing.face)
                .placeholder(R.color.bili_red)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(host))
                .into(holder.pocket);
        holder.title.setText(bliDing.nick);
        holder.comments.setText(bliDing.msg);
        holder.story_date.setText(bliDing.create_at);
    }
    private void bindLoadingViewHolder(LoadingMoreHolder holder, int position) {
        // only show the infinite load progress spinner if there are already items in the
        // grid i.e. it's not the first item & data is being loaded
        holder.progress.setVisibility(
               /* position > 0 && dataLoading.isDataLoading() ?
                View.VISIBLE : */
                View.INVISIBLE);
    }
    private void bindHeaderViewHolder(HeaderHolder holder, int position) {
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(host, BiliVideoViewActivity.class);
                // intent.putExtra("path", item.url);
                XLog.d("-->" + bliDing.aid);
                intent.putExtra("displayName", bliDing.title);
                intent.putExtra("av",""+bliDing.aid);
                String page = "1";
                intent.putExtra("page",page);
                host.startActivity(intent);
                // 动画过渡
                host.overridePendingTransition(R.anim.push_left_in, R.anim.push_no);
            }
        });
        holder.tv_title.setText(bliDing.title);
        holder.tv_desc.setText(Html.fromHtml(bliDing.description));
        holder.tv_play.setText("播放: "+bliDing.play);
        holder.tv_favorites.setText("收藏: "+bliDing.favorites);
        holder.tv_video_review.setText("弹幕: " + bliDing.video_review);
        holder.tv_time.setText(bliDing.duration);
        Glide.with(host)
                .load(bliDing.pic)
                .placeholder(R.color.bili_red)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_pre);
    }
    @Override
    public int getItemCount() {
        // include loading footer
        return getDataItemCount() + 1;
    }
    private BiliComment getItem(int position) {
        return items.get(position);
    }
    public int getDataItemCount() {
        return items.size();
    }
    private void add(BiliComment item) {
        items.add(item);
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class BiliDingHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.story_title)
        TextView title;
        @Bind(R.id.story_comments)
        TextView comments;
        @Bind(R.id.pocket)
        ImageView pocket;
        @Bind(R.id.story_date)
        TextView story_date;
        public BiliDingHolder(View itemView) {
            super(itemView);
           /* pocket = (ImageView) itemView.findViewById(R.id.pocket);
            title = (TextView) itemView.findViewById(R.id.story_title);
            comments = (TextView) itemView.findViewById(R.id.story_comments);*/
           ButterKnife.bind(this, itemView);
        }
    }


    class HeaderHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.bt_play)
        Button play;
        @Bind(R.id.iv_pre)
        ImageView iv_pre;
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_play)
        TextView tv_play;
        @Bind(R.id.tv_favorites)
        TextView tv_favorites;
        @Bind(R.id.tv_video_review)
        TextView tv_video_review;
        @Bind(R.id.tv_desc)
        TextView tv_desc;
        @Bind(R.id.tv_time)
        TextView tv_time;

        public HeaderHolder(View itemView) {
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
