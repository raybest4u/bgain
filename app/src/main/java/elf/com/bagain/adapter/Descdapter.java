package elf.com.bagain.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.BiliVideoViewActivity;
import elf.com.bagain.BliDecActivity;
import elf.com.bagain.R;
import elf.com.bagain.data.BiliComment;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.utils.ObservableColorMatrix;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.utils.glide.GlideCircleTransform;
import elf.com.bagain.widget.BadgedFourThreeImageView;

/**
 * User：McCluskey Ray on 2015/11/10 13:52
 * email：lr8734@126.com
 */
public class Descdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BLIBLI_ITEM = 0;
    private static final int TYPE_BLIBLI_HEADER = 1;
    private static final int TYPE_BLIBLI_TAG = 2;
    private static final int TYPE_LOADING_MORE = -1;
    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private BliDingItem bliDing;
    private List<BiliComment> items;
    private List<BliDingItem> tags;
    public Descdapter(Activity hostActivity, BliDingItem bliDing){
        this.host = hostActivity;
        this.bliDing = bliDing;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
        tags = new ArrayList<>();
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
            case TYPE_BLIBLI_TAG:
                return  new TagsHolder(layoutInflater.inflate(R.layout.desc_tags, parent, false));
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
                    bindBiliDing(item, (BiliDingHolder) holder);
                }
            }
        }else if(holder instanceof  TagsHolder){
            bindTagsHolder((TagsHolder) holder, position);
        }else if(holder instanceof LoadingMoreHolder){
                bindLoadingViewHolder((LoadingMoreHolder) holder, position);
        }


    }
    @Override
    public int getItemViewType(int position) {
         if(position==0){
             return TYPE_BLIBLI_HEADER;
         }else if(position == 1){
             return  TYPE_BLIBLI_TAG;
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
    public void addTags(Collection<? extends BliDingItem> newTags) {
       // this.items.addAll(newItems);
        this.tags.addAll(newTags);
        notifyDataSetChanged();
       // notifyDataSetChanged();
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLog.d("comment click");
            }
        });
    }
    private void bindLoadingViewHolder(LoadingMoreHolder holder, int position) {
        // only show the infinite load progress spinner if there are already items in the
        // grid i.e. it's not the first item & data is being loaded
        holder.progress.setVisibility(
               /* position > 0 && dataLoading.isDataLoading() ?
                View.VISIBLE : */
                View.INVISIBLE);
    }
    private void bindTagsHolder(TagsHolder holder,int position){
        BiliDingHolder2 holder2;
        if(tags.size()==0){
            holder.itemView.setVisibility(View.GONE);
        }
        for (BliDingItem item: tags){
            holder2 =   new BiliDingHolder2(layoutInflater.inflate(R.layout.blibli_ding_item_tag, holder.scrollView, false));
            bindBiliDing(item,holder2);
            holder.scrollView.addView(holder2.itemView);
        }
    }

    class BiliDingHolder2 extends RecyclerView.ViewHolder {

        @Bind(R.id.story_title)
        TextView title;
        @Bind(R.id.story_comments)
        TextView comments;
        @Bind(R.id.pocket)
        ImageView pocket;
        @Bind(R.id.tv_view)
        TextView tv_view;
        public BiliDingHolder2(View itemView) {
            super(itemView);
           /* pocket = (ImageView) itemView.findViewById(R.id.pocket);
            title = (TextView) itemView.findViewById(R.id.story_title);
            comments = (TextView) itemView.findViewById(R.id.story_comments);*/
            ButterKnife.bind(this, itemView);
        }
    }
    private void bindBiliDing(final BliDingItem bliDing, final BiliDingHolder2 holder) {
        holder.pocket.setImageResource(R.mipmap.ic_launcher);
        holder.title.setText(bliDing.title);
        holder.comments.setText(""+bliDing.play);
        holder.tv_view.setText(""+bliDing.video_review);
        final BadgedFourThreeImageView iv = (BadgedFourThreeImageView) holder.pocket;
        Glide.with(host)
                .load(bliDing.pic)
                .listener(new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean
                                                           isFromMemoryCache, boolean
                                                           isFirstResource) {
                        if (!bliDing.hasFadedIn) {
                            iv.setHasTransientState(true);
                            final ObservableColorMatrix cm = new ObservableColorMatrix();
                            ObjectAnimator saturation = ObjectAnimator.ofFloat(cm,
                                    ObservableColorMatrix.SATURATION, 0f, 1f);
                            saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                                    () {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    if (iv.getDrawable() != null) {
                                        iv.getDrawable().setColorFilter(new
                                                ColorMatrixColorFilter(cm));
                                    }
                                }
                            });
                            saturation.setDuration(2000);
                            saturation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    iv.setHasTransientState(false);
                                }
                            });
                            saturation.start();
                            bliDing.hasFadedIn = true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable>
                            target, boolean isFirstResource) {
                        return false;
                    }
                })
                        // needed to prevent seeing through view as it fades in
                .placeholder(R.color.background_dark)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // iv.setTransitionName(iv.getResources().getString(R.string.transition_shot));
                holder.pocket.setBackgroundColor(
                        ContextCompat.getColor(host, R.color.background_light));
                Intent intent = new Intent();
                intent.setClass(host, BliDecActivity.class);
                intent.putExtra("index", holder.getAdapterPosition());
                intent.putExtra(BliDecActivity.EXTRA_SHOT, bliDing);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                host, view, String.format("%s.image", bliDing.pic));

                host.startActivity(intent);
               /* host.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_no);*/
                host.finish();
            }
        });
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
        return items.get(position>2?position-2:0);
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

    class TagsHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.linear)
        LinearLayout scrollView;

        public TagsHolder(View itemView) {
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
