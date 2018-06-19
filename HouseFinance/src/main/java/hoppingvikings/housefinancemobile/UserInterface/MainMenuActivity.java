package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.NotificationWrapper;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewListActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.MainMenuItem;
import hoppingvikings.housefinancemobile.UserInterface.Lists.MainMenu.MainMenuListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.MainMenu.MarginDecoration;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class MainMenuActivity extends AppCompatActivity implements CommunicationCallback {

    CoordinatorLayout _layout;
    MainMenuListAdapter _listAdapter;
    RecyclerView _rv;
    ArrayList<MainMenuItem> _mainMenuItems;

    GoogleSignInClient _signInClient;
    NotificationWrapper _notificationWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        _layout = findViewById(R.id.coordLayout);

        Toolbar appToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        CreateMainMenuListItems();

        _rv =  findViewById(R.id.mainMenuRecyclerView);
        _rv.setHasFixedSize(false);

        _listAdapter = new MainMenuListAdapter(_mainMenuItems, this);
        _rv.setAdapter(_listAdapter);
        _rv.setLayoutManager(new GridLayoutManager(this, 3));
        _rv.setItemViewCacheSize(10);

        _notificationWrapper = new NotificationWrapper();

        _listAdapter.SetMainMenuItemClickedListener(new MainMenuListAdapter.MainMenuItemClickedListener() {
            @Override
            public void onItemClicked(View itemView, int pos) {
                MainMenuItem selectedItem = _listAdapter.GetItem(pos);

                Intent openList = new Intent(MainMenuActivity.this, ViewListActivity.class);
                openList.putExtra("ItemType", selectedItem.menuItemType);
                openList.putExtra("NotificationWrapper", _notificationWrapper);
                startActivity(openList);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        _signInClient = GoogleSignIn.getClient(this, gso);

    }

    private void CreateMainMenuListItems()
    {
        try {
            JSONObject house = new JSONObject(FileIOHandler.Instance().ReadFileAsString("CurrentHousehold"));
            if(house.has("id"))
            {
                _mainMenuItems = new ArrayList<>();

                MainMenuItem bills = new MainMenuItem("Bills", R.drawable.baseline_receipt_black_36, ItemType.BILL.name());
                MainMenuItem shopping = new MainMenuItem("Shopping", R.drawable.baseline_local_grocery_store_black_36, ItemType.SHOPPING.name());
                MainMenuItem tasks = new MainMenuItem("Tasks", R.drawable.baseline_notification_important_black_36, ItemType.TODO.name());

                _mainMenuItems.add(bills);
                _mainMenuItems.add(shopping);
                _mainMenuItems.add(tasks);
            }
            else
            {
                _mainMenuItems = new ArrayList<>();
                WebHandler.Instance().GetHousehold(this, this);
            }
        } catch (JSONException je)
        {
            _mainMenuItems = new ArrayList<>();
            WebHandler.Instance().GetHousehold(this, this);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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

            case R.id.sign_out:
                SignOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SignOut()
    {
        _signInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        WebHandler.Instance().SetSessionID("");
                        Intent signInScreen = new Intent(MainMenuActivity.this, SignInActivity.class);
                        startActivity(signInScreen);

                        finish();
                    }
                });
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o) {
        String returnedID = o.toString();
        if(!returnedID.equals(""))
        {
            MainMenuItem bills = new MainMenuItem("Bills", R.drawable.baseline_receipt_black_36, ItemType.BILL.name());
            MainMenuItem shopping = new MainMenuItem("Shopping", R.drawable.baseline_local_grocery_store_black_36, ItemType.SHOPPING.name());
            MainMenuItem tasks = new MainMenuItem("Tasks", R.drawable.baseline_notification_important_black_36, ItemType.TODO.name());

            _mainMenuItems.add(bills);
            _mainMenuItems.add(shopping);
            _mainMenuItems.add(tasks);
            _listAdapter.AddAll(_mainMenuItems);
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message) {
        Snackbar.make(_layout, "Failed to obtain the household ID", Snackbar.LENGTH_LONG).show();
    }
}
