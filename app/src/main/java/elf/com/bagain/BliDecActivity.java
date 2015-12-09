package elf.com.bagain;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.utils.ImmersiveUtil;
import elf.com.bagain.utils.SimpleTransitionListener;
import ooo.oxo.library.widget.PullBackLayout;

public class BliDecActivity  extends AppCompatActivity implements PullBackLayout.Callback {
    public final static String EXTRA_SHOT = "shot";
    @Bind(R.id.puller)
    PullBackLayout puller;
    @Bind(R.id.rl_root)
    View root;
    private ColorDrawable background;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bli_dec);
        ButterKnife.bind(this);
        puller.setCallback(this);
        index =  getIntent().getIntExtra("index", 0);
        background = new ColorDrawable(Color.parseColor("#db77ab"));
        root.setBackground(background);
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

    }
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
        ImmersiveUtil.exit(root);
    }

    private void hideSystemUi() {
        ImmersiveUtil.enter(root);
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
}
