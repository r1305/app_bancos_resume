package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jackandphantom.circularprogressbar.CircleProgressbar;

public class MainActivity extends AppCompatActivity {

    Context ctx;
    Class activity;
    TextView version_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx=MainActivity.this;
        version_name=findViewById(R.id.tv_version);
        version_name.setText("V."+BuildConfig.VERSION_NAME);
        version_name.setTextColor(Color.BLACK);
        version_name.setTypeface(version_name.getTypeface(),Typeface.BOLD);


        CircleProgressbar circleProgressbar = findViewById(R.id.main_progress_bar);
        circleProgressbar.setForegroundProgressColor(Color.RED);
        circleProgressbar.setBackgroundProgressWidth(15);
        circleProgressbar.setForegroundProgressWidth(20);
        circleProgressbar.enabledTouch(false);
        circleProgressbar.setRoundedCorner(true);
        circleProgressbar.setClockwise(true);
        circleProgressbar.setMaxProgress(100);
        int animationDuration = 8000; // 2500ms = 2,5s
        circleProgressbar.setProgressWithAnimation(-100, animationDuration); // Default duration = 1500ms

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String user_id=new Credentials(ctx).getUserId();
                activity=(user_id.equals("0") || user_id==null)?LoginActivity.class:FirstActivity.class;
                redirect(activity);
            }
        }, 3000);
    }

    public void redirect(Class activity_class){
        Intent i=new Intent(this,activity_class);
        startActivity(i);
        this.finish();
    }
}
