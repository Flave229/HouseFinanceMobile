package hoppingvikings.housefinancemobile.UserInterface;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.BackgroundService;

public class MainActivity extends AppCompatActivity {

    SimpleFragmentPagerAdapter adapter;
    TabLayout tabLayout;
    private Handler _handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(GlobalObjects._service != null)
            {
                //GlobalObjects._service.contactWebsite();
            }
            else
            {
                if(_handler != null) {
                    //_handler.post(runnable);
                }
            }
        }
    };

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _handler = new Handler();
        GlobalObjects._service = new BackgroundService();
        //_handler.post(runnable);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        for(int i = 0; i < tabLayout.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        _handler.removeCallbacksAndMessages(runnable);
        _handler = null;
    }

    @Override
    public void onBackPressed() {
        // End the app process after pressing back
        finish();
        Runtime.getRuntime().exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.additemmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*switch (item.getItemId())
        {
            case R.id.action_add:
                Intent addBill = new Intent(this, AddNewBillActivity.class);
                startActivity(addBill);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }*/

        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : mngr.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

}
