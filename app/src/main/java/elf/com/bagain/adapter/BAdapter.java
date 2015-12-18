package elf.com.bagain.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.BliDecActivity;
import elf.com.bagain.DonghuaActivity;
import elf.com.bagain.R;
import elf.com.bagain.data.BannerItem;
import elf.com.bagain.data.BliDingItem;
import elf.com.bagain.data.DataLoadingSubject;
import elf.com.bagain.data.Item;
import elf.com.bagain.utils.ObservableColorMatrix;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.widget.BadgedFourThreeImageView;

/**
 * User：McCluskey Ray on 2015/11/10 13:52
 * email：lr8734@126.com
 */
public class BAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BLIBLI_ITEM = 0;
    private static final int TYPE_BLIBLI_CATEGORY = 1;
    private static final int TYPE_BLIBLI_HEADER = 2;
    private static final int TYPE_LOADING_MORE = -1;
    // we need to hold on to an activity ref for the shared element transitions :/
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private
    @Nullable
    DataLoadingSubject dataLoading;

    private List<Item> items;
    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;
    private int currentItem;

    public BAdapter(Activity hostActivity, DataLoadingSubject dataLoading) {
        this.host = hostActivity;
        this.dataLoading = dataLoading;
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
        dots = new ArrayList<>();
        dotList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BLIBLI_HEADER:
                return new BiliHeaderHolder(layoutInflater.inflate(R.layout.banner, parent, false));
            case TYPE_BLIBLI_CATEGORY:
                return new BiliCategoryHolder(layoutInflater.inflate(R.layout.category_ding, parent, false));
            case TYPE_BLIBLI_ITEM:
                return new BiliDingHolder(layoutInflater.inflate(R.layout.blibli_ding_item, parent, false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(
                        layoutInflater.inflate(R.layout.infinite_loading, parent, false));

            default:
                break;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            bindHeaderViewHolder((BiliHeaderHolder) holder, position);
        } else if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            Item item = getItem(position);
           /* if (item instanceof BliDingItem) {
                bindBiliDing((BliDingItem) item, (BiliDingHolder) holder);
            }*/
            if (item instanceof BliDingItem && item.id != -100) {
                bindBiliDing((BliDingItem) item, (BiliDingHolder) holder);
            } else if (item instanceof BliDingItem && item.id == -100) {
                bindBiliHeader((BliDingItem) item, (BiliCategoryHolder) holder);
            }
        } else {
            bindLoadingViewHolder((LoadingMoreHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        XLog.d("position--"+position);
        if (position == 0) {
            return TYPE_BLIBLI_HEADER;
        }
        if (position < getDataItemCount()
                && getDataItemCount() > 0) {
            Item item = getItem(position);
            if (item instanceof BliDingItem && item.id != -100) {
                return TYPE_BLIBLI_ITEM;
            } else if (item instanceof BliDingItem && item.id == -100) {
                return TYPE_BLIBLI_CATEGORY;
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

    private void bindBiliHeader(final BliDingItem bliDing, final BiliCategoryHolder holder) {
        holder.title.setText(bliDing.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(host, DonghuaActivity.class);
                intent.putExtra("AreaType", bliDing.type);
                intent.putExtra(BliDecActivity.EXTRA_SHOT, bliDing);
                host.startActivity(intent);
            }
        });
    }

    private void bindBiliDing(final BliDingItem bliDing, final BiliDingHolder holder) {
        holder.pocket.setImageResource(R.mipmap.ic_launcher);
        holder.title.setText(bliDing.title);
        holder.comments.setText("" + bliDing.play);
        holder.tv_view.setText("" + bliDing.video_review);
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
               /* host.overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_no);*/

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
   HomePageADAdapter homePageADAdapter;
    private void bindHeaderViewHolder(BiliHeaderHolder holder, int position) {
        dots.add(holder.dot0);
        dots.add(holder.dot1);
        dots.add(holder.dot2);
        dots.add(holder.dot3);
        dots.add(holder.dot4);
        dots.add(holder.dot5);
        homePageADAdapter = new HomePageADAdapter();
        holder.vp.setAdapter(homePageADAdapter);// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        holder.vp.addOnPageChangeListener(new HomePageADChangeListener());
        // addDynamicView();
    }

    private ScheduledExecutorService scheduledExecutorService;

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒切换一次图片显示
       /* scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
                TimeUnit.SECONDS);*/
    }

    public void addBanner(List<? extends BannerItem> newItems) {
        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        adList = newItems;
       // if(host!=null&&!host.isFinishing()) {
            imageViews = new ArrayList<>(newItems.size() + 1);
            for (int i = 0; i < 5; i++) {
                ImageView imageView = new ImageView(host);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(host.getApplicationContext())
                        .load(newItems.get(i).getImg())
                        .placeholder(R.color.background_dark)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                imageViews.add(imageView);
                dots.get(i).setVisibility(View.VISIBLE);
                dotList.add(dots.get(i));
            }
            if(homePageADAdapter!=null)
                homePageADAdapter.notifyDataSetChanged();
      //  }

    }
    /**
     * 返回当前的应用是否处于前台显示状态
     * @param $packageName
     * @return
     */
    private boolean isTopActivity(String $packageName) {
        //_context是一个保存的上下文
        ActivityManager am = (ActivityManager) host.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if(list.size() == 0) return false;

        for(ActivityManager.RunningAppProcessInfo process:list)
        {
            XLog.d( Integer.toString(process.importance));
            XLog.d( process.processName);
            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.processName.equals($packageName))
            {
                return true;
            }
        }
        return false;
    }
    // 轮播banner的数据
    private List<? extends BannerItem> adList;
    private List<ImageView> imageViews;// 滑动的图片集合

    @Override
    public int getItemCount() {
        // include loading footer
        return getDataItemCount() + 1;
    }

    private Item getItem(int position) {
        return items.get(position-1);
    }

    public int getDataItemCount() {
        return items.size()+1;
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
        @Bind(R.id.tv_view)
        TextView tv_view;

        public BiliDingHolder(View itemView) {
            super(itemView);
           /* pocket = (ImageView) itemView.findViewById(R.id.pocket);
            title = (TextView) itemView.findViewById(R.id.story_title);
            comments = (TextView) itemView.findViewById(R.id.story_comments);*/
            ButterKnife.bind(this, itemView);
        }
    }

    class BiliCategoryHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView title;


        public BiliCategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class BiliHeaderHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.v_dot0)
        View dot0;
        @Bind(R.id.v_dot1)
        View dot1;
        @Bind(R.id.v_dot2)
        View dot2;
        @Bind(R.id.v_dot3)
        View dot3;
        @Bind(R.id.v_dot4)
        View dot4;
        @Bind(R.id.v_dot5)
        View dot5;
        @Bind(R.id.vp)
        ViewPager vp;

        public BiliHeaderHolder(View itemView) {
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

    private class HomePageADAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return adList == null ? 0 : adList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (imageViews != null && imageViews.size() > 0) {
                ImageView iv = imageViews.get(position%5);
                ((ViewPager) container).addView(iv);

                // 在这个方法里面设置图片的点击事件
                iv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 处理跳转逻辑
                        BannerItem item = (BannerItem) adList.get(position);
                   /* Intent i = new Intent();
                    i.setClass(getActivity(), BiliWebviewActivity.class);
                    i.putExtra("bannerLink", item.getLink());
                    startActivity(i);
                    // 动画过渡
                    getActivity().overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_no);*/
                        XLog.d("position", "" + position);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(item.getLink()));
                       host.startActivity(intent);
                    }
                });

                return iv;
            } else {
                return null;
            }
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private class HomePageADChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
    }
}
