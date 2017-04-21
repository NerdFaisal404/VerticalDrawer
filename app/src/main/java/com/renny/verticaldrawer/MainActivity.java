package com.renny.verticaldrawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    VerticalDrawerLayout mVerticalDrawerLayout;
    RelativeLayout drawerContent;
    DrawerScrollLayout drawerScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVerticalDrawerLayout = (VerticalDrawerLayout) findViewById(R.id.drawer_scroll_ly);
        drawerContent= (RelativeLayout) findViewById(R.id.drawer_scroll_rel);
        drawerScroll= (DrawerScrollLayout) findViewById(R.id.drawer_scroll);
        mVerticalDrawerLayout.setCanScroll(true);
        drawerScroll.setVisibility(View.VISIBLE);
        mVerticalDrawerLayout.setOpenChangeListener(new VerticalDrawerLayout.openChangeListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    drawerContent.setVisibility(View.VISIBLE);
                } else {
                    drawerContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onShowHeightChanging(int showHeight) {
                drawerContent.setVisibility(View.VISIBLE);
                drawerScroll.setShowHeight(showHeight);
            }
        });
    }
}
