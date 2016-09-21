package com.example.administrator.arc_menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.arc_menu.widget.ArcMenu;

public class MainActivity extends AppCompatActivity {
    private ArcMenu arcmenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arcmenu = (ArcMenu) findViewById(R.id.arcmenu);
        arcmenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this,"点击的是："+position,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
