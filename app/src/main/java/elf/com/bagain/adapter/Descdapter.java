package elf.com.bagain.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import elf.com.bagain.BliDecActivity;
import elf.com.bagain.R;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.data.Item;
import elf.com.bagain.utils.ObservableColorMatrix;
import elf.com.bagain.widget.BadgedFourThreeImageView;

/**
 * User：McCluskey Ray on 2015/11/10 13:52
 * email：lr8734@126.com
 */
public class Descdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BLIBLI_ITEM = 0;
    private static final int TYPE_BLIBLI_CATEGORY = 1;
    private static final int TYPE_LOADING_MORE = -1;
    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private @Nullable
    DataLoadingSubject dataLoading;

    private List<Item> items;

    public Descdapter(Activity hostActivity, DataLoadingSubject dataLoading){
        this.host = hostActivity;
        this.dataLoading = dataLoading;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BLIBLI_ITEM:
                return new BiliDingHolder(layoutInflater.inflate(R.layout.blibli_ding_item, parent, false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));
            default:break;
        }

        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            Item item = getItem(position);
            if(item instanceof BliDingItem){
                bindBiliDing((BliDingItem)item, (BiliDingHolder) holder);
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
        holder.pocket.setImageResource(R.mipmap.ic_launcher);
        holder.title.setText(bliDing.title);
        holder.comments.setText(bliDing.author);
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
                                    // just animating the color matrix does not invalidate the
                                    // drawable so need this update listener.  Also have to create a
                                    // new CMCF as the matrix is immutable :(
                                    if (iv.getDrawable() != null) {
                                        iv.getDrawable().setColorFilter(new
                                                ColorMatrixColorFilter(cm));
                                    }
                                }
                            });
                            saturation.setDuration(2000);
                          /*  saturation.setInterpolator(AnimationUtils.loadInterpolator(host,
                                    android.R.interpolator.fast_out_slow_in));*/
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
       /*Picasso.with(holder.itemView.getContext()).load(bliDing.pic)
                .placeholder(R.color.background_dark)
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int size = Math.min(source.getWidth(), source.getHeight());
                        int x = (source.getWidth() - size) / 2;
                        int y = (source.getHeight() - size) / 2;

                        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
                        if (result != source) {
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "square()";
                    }
                }).into(holder.pocket);*/
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

                host.startActivity(intent);//, options.toBundle());
                // 动画过渡
              host.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_no);

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
        @Bind(R.id.story_comments)
        TextView comments;
        @Bind(R.id.pocket)
        ImageView pocket;

        public BiliDingHolder(View itemView) {
            super(itemView);
           /* pocket = (ImageView) itemView.findViewById(R.id.pocket);
            title = (TextView) itemView.findViewById(R.id.story_title);
            comments = (TextView) itemView.findViewById(R.id.story_comments);*/
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
