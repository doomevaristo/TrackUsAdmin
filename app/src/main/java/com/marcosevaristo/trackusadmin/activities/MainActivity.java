package com.marcosevaristo.trackusadmin.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.adapters.ViewPagerAdapter;
import com.marcosevaristo.trackusadmin.fragments.AbaMunicipios;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

public class MainActivity extends AppCompatActivity{

    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        setContentView(R.layout.activity_main);
        setupTabLayout();
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.abaMunicipios));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new AbaMunicipios(), getString(R.string.abaMunicipios));
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}
