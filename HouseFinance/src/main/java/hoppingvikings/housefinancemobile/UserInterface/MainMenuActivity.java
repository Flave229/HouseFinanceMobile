package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewListActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.MainMenuItem;
import hoppingvikings.housefinancemobile.UserInterface.Lists.MainMenu.MainMenuListAdapter;

public class MainMenuActivity extends AppCompatActivity {

    CoordinatorLayout _layout;
    MainMenuListAdapter _listAdapter;
    RecyclerView _rv;
    ArrayList<MainMenuItem> _mainMenuItems;

    SimpleFragmentPagerAdapter adapter;
    TabLayout tabLayout;
    public FloatingActionButton addBillFab;
    public FloatingActionButton addShoppingItemFab;
    public FloatingActionButton addTodoItemFab;
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

    private Runnable showToDoButton = new Runnable() {
        @Override
        public void run() {
            addTodoItemFab.show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        _handler = new Handler();
        _layout = findViewById(R.id.coordlayout);

        Toolbar appToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        CreateMainMenuListItems();

        _rv =  findViewById(R.id.mainMenuRecyclerView);
        _rv.setHasFixedSize(false);

        _listAdapter = new MainMenuListAdapter(_mainMenuItems, this);
        _rv.setAdapter(_listAdapter);
        _rv.setLayoutManager(new GridLayoutManager(this, 3));
        _rv.setItemViewCacheSize(10);

        _listAdapter.SetMainMenuItemClickedListener(new MainMenuListAdapter.MainMenuItemClickedListener() {
            @Override
            public void onItemClicked(View itemView, int pos) {
                MainMenuItem selectedItem = _listAdapter.GetItem(pos);

                Intent openList = new Intent(MainMenuActivity.this, ViewListActivity.class);
                openList.putExtra("ItemType", selectedItem.menuItemType);
                startActivity(openList);
            }
        });

        //_handler.post(runnable);
        //addBillFab = (FloatingActionButton) findViewById(R.id.addBill);
        //addShoppingItemFab = (FloatingActionButton) findViewById(R.id.addShoppingItem);
        //addTodoItemFab = (FloatingActionButton) findViewById(R.id.addTodoItem);
        //addShoppingItemFab.hide();
        //addTodoItemFab.hide();

//        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
//        adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(3);
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position)
//                {
//                    case 0:
//                        // Bills
//                        addTodoItemFab.hide();
//                        addShoppingItemFab.hide();
//                        _handler.postDelayed(showBillButton, 200);
//                        break;
//                    case 1:
//                        // shopping
//                        //addShoppingItemFab.show();
//                        addBillFab.hide();
//                        addTodoItemFab.hide();
//                        _handler.postDelayed(showShoppingButton, 200);
//                        break;
//                    case 2:
//                        // task list
//                        addShoppingItemFab.hide();
//                        addBillFab.hide();
//                        _handler.postDelayed(showToDoButton, 200);
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);
//
//        for(int i = 0; i < tabLayout.getTabCount(); i++)
//        {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            tab.setCustomView(adapter.getTabView(i));
//        }
    }

    private void CreateMainMenuListItems()
    {
        _mainMenuItems = new ArrayList<>();

        MainMenuItem bills = new MainMenuItem("Bills", R.drawable.baseline_receipt_black_36, ItemType.BILL);
        MainMenuItem shopping = new MainMenuItem("Shopping", R.drawable.baseline_local_grocery_store_black_36, ItemType.SHOPPING);
        MainMenuItem tasks = new MainMenuItem("Tasks", R.drawable.baseline_notification_important_black_36, ItemType.TODO);

        _mainMenuItems.add(bills);
        _mainMenuItems.add(shopping);
        _mainMenuItems.add(tasks);
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
        getMenuInflater().inflate(R.menu.additemmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_end:
                Runtime.getRuntime().exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
