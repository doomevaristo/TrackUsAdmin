package com.marcosevaristo.trackusadmin.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.adapters.ViewPagerAdapter;
import com.marcosevaristo.trackusadmin.fragments.AbaLinhas;
import com.marcosevaristo.trackusadmin.model.Municipio;


public class ConsultaLinhasActivity extends AppCompatActivity{

    private ViewPagerAdapter viewPagerAdapter;
    private Municipio municipio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        setContentView(R.layout.activity_consulta_linhas);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            municipio = (Municipio) bundle.get("municipio");
        }
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
        AbaLinhas abaLinhas = new AbaLinhas();
        Bundle arguments = new Bundle();
        arguments.putSerializable("municipio", municipio);
        abaLinhas.setArguments(arguments);

        viewPagerAdapter.addFragment(abaLinhas, getString(R.string.abaLinhas));
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}