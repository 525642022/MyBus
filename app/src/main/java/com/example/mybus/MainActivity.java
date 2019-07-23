package com.example.mybus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyBus.getInstance().register(this);
        TextView test1  = findViewById(R.id.test1);
        test1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyBus.getInstance().unRegister(this);
    }

    @Subscriber(threadMode = ThreadMode.MAIN)
    public void btn1Event(TestEventBean bean) {
        Log.e("ljc", "btn1Event()接收消息线程id = " + Thread.currentThread().getId());
    }
}
