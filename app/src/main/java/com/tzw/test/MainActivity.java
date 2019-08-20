package com.tzw.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import lib.tzw.annotation.BindId;
import lib.tzw.annotation_api.IdInjector;

public class MainActivity extends AppCompatActivity {
    @BindId(R.id.btn)
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IdInjector.injectBind(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Hello!!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
