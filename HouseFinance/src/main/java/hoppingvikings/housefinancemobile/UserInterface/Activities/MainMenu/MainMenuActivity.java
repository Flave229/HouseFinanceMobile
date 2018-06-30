package hoppingvikings.housefinancemobile.UserInterface.Activities.MainMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdEndpoint;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.NotificationWrapper;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewHouseholdActivity;
import hoppingvikings.housefinancemobile.UserInterface.Activities.ViewListActivity;
import hoppingvikings.housefinancemobile.UserInterface.Items.MainMenuItem;
import hoppingvikings.housefinancemobile.UserInterface.Lists.MainMenu.MainMenuListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class MainMenuActivity extends AppCompatActivity implements CommunicationCallback
{
    private NotificationWrapper _notificationWrapper;
    private SessionPersister _session;
    private HouseholdEndpoint _householdEndpoint;

    private CoordinatorLayout _layout;
    private MainMenuListAdapter _listAdapter;
    private RecyclerView _rv;
    private ArrayList<MainMenuItem> _mainMenuItems;

    private GoogleSignInClient _signInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        _notificationWrapper = HouseFinanceClass.GetNotificationWrapperComponent().GetNotificationWrapper();
        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
        _householdEndpoint = HouseFinanceClass.GetHouseholdComponent().GetHouseholdEndpoint();

        _layout = findViewById(R.id.coordLayout);

        Toolbar appToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(appToolbar);

        _mainMenuItems = new ArrayList<>();

        CreateMainMenuListItems();

        _rv =  findViewById(R.id.mainMenuRecyclerView);
        _rv.setHasFixedSize(false);

        _listAdapter = new MainMenuListAdapter(_mainMenuItems, this);
        _rv.setAdapter(_listAdapter);
        _rv.setLayoutManager(new GridLayoutManager(this, 3));
        _rv.setItemViewCacheSize(10);

        WebHandler.Instance().SetSessionPersister(_session);

        _listAdapter.SetMainMenuItemClickedListener(new MainMenuListAdapter.MainMenuItemClickedListener() {
            @Override
            public void onItemClicked(View itemView, int pos) {
                MainMenuItem selectedItem = _listAdapter.GetItem(pos);

                if(selectedItem.menuItemType.equals("HOUSEHOLD"))
                {
                    Intent openHouseholdPage = new Intent(MainMenuActivity.this, ViewHouseholdActivity.class);
                    boolean hasHouse = false;
                    try {
                        JSONObject house = new JSONObject(FileIOHandler.Instance().ReadFileAsString("CurrentHousehold"));
                        hasHouse = house.has("id");
                    } catch (JSONException je) {

                    }

                    openHouseholdPage.putExtra("HasHousehold", hasHouse);
                    startActivityForResult(openHouseholdPage, 0);
                    return;
                }

                Intent openList = new Intent(MainMenuActivity.this, ViewListActivity.class);
                openList.putExtra("ItemType", selectedItem.menuItemType);
                openList.putExtra("NotificationWrapper", _notificationWrapper);
                openList.putExtra("SessionPersister", _session);
                startActivity(openList);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        _signInClient = GoogleSignIn.getClient(this, gso);

    }

    private void CreateMainMenuListItems()
    {
        try
        {
            JSONObject house = new JSONObject(FileIOHandler.Instance().ReadFileAsString("CurrentHousehold"));
            if(house.has("id"))
            {
                MainMenuItem bills = new MainMenuItem("Bills", R.drawable.baseline_receipt_black_36, ItemType.BILL.name());
                MainMenuItem shopping = new MainMenuItem("Shopping", R.drawable.baseline_local_grocery_store_black_36, ItemType.SHOPPING.name());
                MainMenuItem tasks = new MainMenuItem("Tasks", R.drawable.baseline_notification_important_black_36, ItemType.TODO.name());
                MainMenuItem household = new MainMenuItem("Household", R.drawable.baseline_home_black_36, ItemType.HOUSEHOLD.name());

                _mainMenuItems.add(bills);
                _mainMenuItems.add(shopping);
                _mainMenuItems.add(tasks);
                _mainMenuItems.add(household);
            }
            else
            {
                _householdEndpoint.Get(this, this);
            }
        }
        catch (JSONException je)
        {
            _householdEndpoint.Get(this, this);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        // End the app process after pressing back
        finish();
        //Runtime.getRuntime().exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.additemmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        WebHandler.Instance().SetSessionID("");
                        _session.SetSessionID("");
                        FileIOHandler.Instance().WriteToFile("CurrentHousehold", new JSONObject().toString());
                        Intent signInScreen = new Intent(MainMenuActivity.this, SignInActivity.class);
                        startActivity(signInScreen);

                        finish();
                    }
                });
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        CreateMainMenuListItems();
        _listAdapter.AddAll(_mainMenuItems);
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        if(message.equals(ApiErrorCodes.USER_NOT_IN_HOUSEHOLD.name()))
        {
            MainMenuItem house = new MainMenuItem("Household", R.drawable.baseline_home_black_36, ItemType.HOUSEHOLD.name());
            _mainMenuItems.add(house);
            _listAdapter.AddAll(_mainMenuItems);
        }
        else
        {
            Snackbar.make(_layout, "Failed to obtain the household ID", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case 20:
                SignOut();
                break;

            case RESULT_OK:
                _mainMenuItems.clear();
                CreateMainMenuListItems();
                _listAdapter.AddAll(_mainMenuItems);
                break;

            case RESULT_CANCELED:

                break;
        }
    }
}