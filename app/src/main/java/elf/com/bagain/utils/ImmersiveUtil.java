package elf.com.bagain.utils;

import android.view.View;

/**
 * Created by Administrator on 2015/11/19.
 */
public class ImmersiveUtil {

    private static final int SYSTEM_UI_IMMERSIVE = View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    public static void enter(View view) {
        view.setSystemUiVisibility(view.getSystemUiVisibility() | SYSTEM_UI_IMMERSIVE);
    }

    public static void exit(View view) {
        view.setSystemUiVisibility(view.getSystemUiVisibility() & (~SYSTEM_UI_IMMERSIVE));
    }

}
