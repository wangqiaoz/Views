package com.wang.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    private HeightChose mHeightChose;
    private LinearLayout mDemo;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }


    private void initView() {
        mHeightChose = (HeightChose) findViewById(R.id.height_chose);
        mDemo = (LinearLayout) findViewById(R.id.demo);

        mHeightChose.setOnSelectValueChange(new HeightChose.OnSelectValueChange() {
            @Override
            public void onSelect(String value) {
                mText.setText(value);
            }
        });
        mText = (TextView) findViewById(R.id.text);
    }
}
