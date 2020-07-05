package com.example.ahmed_hasanein.sfa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromDealerOrder;
import static com.example.ahmed_hasanein.sfa.DashboardActivity.OpenfromOrderPage;

public class DealerDashboardActivity extends AppCompatActivity {
    CardView customercardview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_dashboard);
        customercardview = (CardView) findViewById(R.id.customercardview);
        customercardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DealerDashboardActivity.this, CustomerActivity.class);
                i.putExtra("DealerOrder",true);
                OpenfromDealerOrder = true;
                OpenfromOrderPage = false;
                startActivity(i);
            }
        });
    }
}
