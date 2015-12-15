package elf.com.bagain;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.Window;

import elf.com.bagain.adapter.BankumiTabAdapter;
import elf.com.bagain.adapter.DianyingTabAdapter;
import elf.com.bagain.adapter.DonghuaTabAdapter;
import elf.com.bagain.adapter.KejiTabAdapter;
import elf.com.bagain.adapter.MusicTabAdapter;
import elf.com.bagain.adapter.RankAdapter;
import elf.com.bagain.adapter.YouxiTabAdapter;
import elf.com.bagain.adapter.YuleTabAdapter;
import elf.com.bagain.utils.ImmersiveUtil;
import elf.com.bagain.utils.SimpleTransitionListener;
import elf.com.bagain.view.swipeback.SwipeBackActivity;
import github.chenupt.springindicator.SpringIndicator;
import ooo.oxo.library.widget.PullBackLayout;


/**
 * 动画界面
 * 
 * @author wwj_748
 * @date 2014/8/9
 */
public class DonghuaActivity extends SwipeBackActivity implements PullBackLayout.Callback {
	public int mAreaType;
	private PullBackLayout ll_root;
    private ColorDrawable background;
	//private TextView titleText;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donghua_tab);
        ll_root = (PullBackLayout)findViewById(R.id.ll_root);
        background = new ColorDrawable(Color.parseColor("#99db77ab"));
        ll_root.setCallback(this);
        ll_root.setBackground(background);
		FragmentPagerAdapter adapter = null;
		mAreaType = getIntent().getIntExtra("AreaType", 1);
		Log.d("QAQ","----->"+mAreaType);
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
		switch (mAreaType){
		case 1:
			//titleText.setText("番剧");
			adapter = new BankumiTabAdapter(getSupportFragmentManager());
			break;
		case 2:
			//titleText.setText("动画");
			adapter = new DonghuaTabAdapter(getSupportFragmentManager());
			break;
		case 3:
			//titleText.setText("音乐");
			adapter = new MusicTabAdapter(getSupportFragmentManager());
			break;
		case 4:
			//titleText.setText("游戏");
			adapter = new YouxiTabAdapter(getSupportFragmentManager());
			break;
		case 5:
			//titleText.setText("科学·技术");
			adapter = new KejiTabAdapter(getSupportFragmentManager());
			break;
		case 6:
			//titleText.setText("娱乐");
			adapter = new YuleTabAdapter(getSupportFragmentManager());
			break;
		case 7:
			//titleText.setText("电影");
			adapter = new DianyingTabAdapter(getSupportFragmentManager());
			break;
		case 8:
			//titleText.setText("排行榜");
			adapter = new RankAdapter(getSupportFragmentManager());
			break;
		default:
			//titleText.setText("电影");
			adapter = new RankAdapter(getSupportFragmentManager());
			break;
		}

		// 视图切换器
		ViewPager pager = (ViewPager) findViewById(R.id.pager);

		/*PagerModelManager manager = new PagerModelManager();
		manager.addCommonFragment(GuideFragment.class, getBgRes(), getTitles());
		ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
		viewPager.setAdapter(adapter);
		viewPager.fixScrollSpeed();*/

		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);

		// 页面指示器
		SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
		springIndicator.setViewPager(pager);


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
}
