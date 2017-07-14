package hoppingvikings.housefinancemobile.UserInterface;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import hoppingvikings.housefinancemobile.BackgroundService;
import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class MainActivity extends AppCompatActivity {

    SimpleFragmentPagerAdapter adapter;
    TabLayout tabLayout;
    public FloatingActionButton addBillFab;
    public FloatingActionButton addShoppingItemFab;
    private Handler _handler;

    private Runnable showBillButton = new Runnable() {
        @Override
        public void run() {
           addBillFab.show();
        }
    };

    private Runnable showShoppingButton = new Runnable() {
        @Override
        public void run() {
            addShoppingItemFab.show();
        }
    };

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _handler = new Handler();
        GlobalObjects.webHandler = new WebHandler();
        //_handler.post(runnable);
        addBillFab = (FloatingActionButton) findViewById(R.id.addBill);
        addShoppingItemFab = (FloatingActionButton) findViewById(R.id.addShoppingItem);
        addShoppingItemFab.hide();

        Toolbar appToolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        // Bills
                        //addBillFab.show();
                        addShoppingItemFab.hide();
                        _handler.postDelayed(showBillButton, 200);
                        break;

                    case 1:
                        // shopping
                        //addShoppingItemFab.show();
                        addBillFab.hide();
                        _handler.postDelayed(showShoppingButton, 200);
                        break;

                    case 2:
                        // stats

                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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
        //_handler.removeCallbacksAndMessages(runnable);
        _handler = null;
    }

    @Override
    public void onBackPressed() {
        // End the app process after pressing back
        finish();
        //Runtime.getRuntime().exit(0);
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
