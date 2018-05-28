package mz.co.example.nhane.postar;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import mz.co.example.nhane.postar.tab.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    long time = 0;
    boolean backPressed = false;
    private Toolbar toolbar;
    private MyPagerAdapter pagerAdapter;
    private ViewPager vPager;
    private SlidingTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Menu Principal");

        setSupportActionBar(toolbar);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vPager = findViewById(R.id.pager);
        vPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setViewPager(vPager);

        if (getSupportActionBar() != null) {

        }
    }

    @Override
    public void onBackPressed() {
        if (time != 0) {
            if ((time + 20000) > System.currentTimeMillis()) {
                Toast.makeText(this, (time + 2000) + " //// " + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
                if (backPressed) {
                    backPressed = false;
                    time = 0;
                    super.onBackPressed();
                } else backPressed = true;
            }
        } else {
            time = System.currentTimeMillis();
            backPressed = true;
            Toast.makeText(this, "Clique novamente para sair", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.sign_out:
                Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences;
                preferences = getSharedPreferences("CREDENCIAL", MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.remove("NAME");
                edit.remove("PASSWORD");
                edit.remove("EMAIL");
                edit.commit();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ContactFragment contactFragment = ContactFragment.getFragmentInstance(position);
            FeedFragment feedFragment = FeedFragment.getFragmentInstance(position);

            if (position == 1) {
                return contactFragment;
            } else return feedFragment;


        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return "Contacto";
            } else return "Feed";
        }
    }


}
