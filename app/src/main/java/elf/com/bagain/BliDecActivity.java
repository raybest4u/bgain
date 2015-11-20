package elf.com.bagain;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;

import butterknife.Bind;
import butterknife.ButterKnife;
import elf.com.bagain.utils.ImmersiveUtil;
import elf.com.bagain.utils.SimpleTransitionListener;
import ooo.oxo.library.widget.PullBackLayout;

public class BliDecActivity extends AppCompatActivity implements PullBackLayout.Callback {
    public final static String EXTRA_SHOT = "shot";
    @Bind(R.id.puller)
    PullBackLayout puller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bli_dec);
        ButterKnife.bind(this);
        puller.setCallback(this);


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



    private void showSystemUi() {
        ImmersiveUtil.exit(puller);
    }

    private void hideSystemUi() {
        ImmersiveUtil.enter(puller);
    }
    @Override
    public void onPullStart() {
        // fade out Action Bar ...
        // show Status Bar ...
    }

    @Override
    public void onPull(float progress) {
        // set the opacity of the window's background
    }

    @Override
    public void onPullCancel() {
        // fade in Action Bar
    }

    @Override
    public void onPullComplete() {
        supportFinishAfterTransition();
    }

}
