package com.example.mybus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MyBus.getInstance().register(this);
        TextView test  = findViewById(R.id.test);
        test.setOnClickListener(v->{
            MyBus.post(new TestEventBean("msg: test 1"));
            Log.e("ljc","發送了");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyBus.getInstance().unRegister(this);
    }
}
