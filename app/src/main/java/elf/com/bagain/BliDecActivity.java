package elf.com.bagain;

import android.app.Activity;
import android.os.Bundle;

public class BliDecActivity extends Activity {
    public final static String EXTRA_SHOT = "shot";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bli_dec);
    }
}
