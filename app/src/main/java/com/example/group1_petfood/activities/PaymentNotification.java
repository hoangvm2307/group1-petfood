package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group1_petfood.R;

public class PaymentNotification extends AppCompatActivity {

    TextView tvNotify;
    TextView tvTotal;
    Button btnReturn;
    ImageView imgPaymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        tvNotify = findViewById(R.id.tvNotify);
        tvTotal= findViewById(R.id.tvTotal);
        btnReturn = findViewById(R.id.btnReturn);
        imgPaymentStatus = findViewById(R.id.imgPaymentStatus);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        tvNotify.setText(result);
        tvTotal.setText(intent.getStringExtra("total"));

        String totalText = intent.getStringExtra("total");
        if (totalText != null) {
            tvTotal.setText(totalText);
        } else {
            tvTotal.setText("");
        }

        assert result != null;
        if (result.equals("Thanh toán thành công")) {
            imgPaymentStatus.setImageResource(R.drawable.ic_success); // Green check icon
        } else if (result.equals("Thanh toán đã được hủy")) {
            imgPaymentStatus.setImageResource(R.drawable.ic_cancel); // Cancel icon
        } else {
            imgPaymentStatus.setImageResource(R.drawable.ic_error); // Error icon
        }


        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReturn = new Intent(PaymentNotification.this, MainActivity.class);
                startActivity(intentReturn);
            }
        });
    }
}