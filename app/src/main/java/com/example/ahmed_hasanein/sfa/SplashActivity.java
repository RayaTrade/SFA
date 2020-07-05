package com.example.ahmed_hasanein.sfa;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashActivity.this)
                .withFullScreen()
                .withTargetActivity(LoginActivity.class)
                .withSplashTimeOut(1000)
                .withBackgroundColor(Color.parseColor("#0e66be"))
                .withAfterLogoText("SFA System")
                .withFooterText("Copyright © "+Calendar.getInstance().get(Calendar.YEAR)+" by Raya Trade")
                .withLogo(R.drawable.rayawhite);

        config.getAfterLogoTextView().setTextSize(16);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View view = config.create();

        setContentView(view);
    }
}
