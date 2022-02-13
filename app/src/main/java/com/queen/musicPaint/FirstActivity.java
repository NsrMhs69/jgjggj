package com.queen.musicPaint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class FirstActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(FirstActivity.this, Paint_Activity.class);
                in.putExtra("brushSize", 10);
                in.putExtra("pencolor", 0xff000000);
                in.putExtra("bg", 0);
                startActivity(in);
                FirstActivity.this.finish();
            }
        }, 3000);
    }


}
