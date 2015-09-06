package com.sanchez.fmf;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.sanchez.fmf.fragment.MainFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_main_activity)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(getResources().getColor(R.color.primary_dark));
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment mainFragment = fm.findFragmentById(R.id.container_main_activity);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.container_main_activity, mainFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
