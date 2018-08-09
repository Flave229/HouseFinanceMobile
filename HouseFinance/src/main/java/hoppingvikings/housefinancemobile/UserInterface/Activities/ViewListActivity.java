package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.ApiErrorCodes;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.BillEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.ToDoEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.User.LogInEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.NotificationWrapper;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.Services.SaltVault.Bills.BillRepository;
import hoppingvikings.housefinancemobile.Services.SaltVault.Shopping.ShoppingRepository;
import hoppingvikings.housefinancemobile.Services.SaltVault.ToDo.TodoRepository;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.BillListObjectPeople;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.UserInterface.Items.TodoListObject;
import hoppingvikings.housefinancemobile.UserInterface.Lists.BillList.BillListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.ShoppingList.ShoppingListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.Lists.TodoList.TodoListAdapter;
import hoppingvikings.housefinancemobile.UserInterface.PeoplePopup;
import hoppingvikings.housefinancemobile.UserInterface.SignInActivity;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.SessionPersister;

public class ViewListActivity extends AppCompatActivity
        implements CommunicationCallback,
        BillListAdapter.ViewAllPeopleClicked,
        ShoppingListAdapter.DeleteCallback, ShoppingListAdapter.EditPressedCallback,
        TodoListAdapter.DeleteCallback, TodoListAdapter.EditPressedCallback
{
    private SessionPersister _session;
    private LogInEndpoint _logInEndpoint;
    private BillEndpoint _billEndpoint;
    private ShoppingEndpoint _shoppingEndpoint;
    private ToDoEndpoint _toDoEndpoint;

    CoordinatorLayout _layout;
    RecyclerView _rv;
    SwipeRefreshLayout _refreshLayout;
    Toolbar _toolbar;

    BillListAdapter _billAdapter;
    ShoppingListAdapter _shoppingAdapter;
    TodoListAdapter _todoAdapter;

    FloatingActionButton _fab;
    PeoplePopup _peopleListPopup;

    String _currentType;

    ArrayList<BillListObject> _bills;
    ArrayList<ShoppingListObject> _shopping;
    ArrayList<TodoListObject> _tasks;

    Handler _handler;

    boolean _obtainingSession = false;

    private Runnable ConnectToApi = new Runnable()
    {
        @Override
        public void run()
        {
            switch (_currentType)
            {
                case "BILL":
                    _billEndpoint.Get(ViewListActivity.this, ViewListActivity.this);
                    break;

                case "SHOPPING":
                    _shoppingEndpoint.Get(ViewListActivity.this, ViewListActivity.this);
                    break;

                case "TODO":
                    _toDoEndpoint.Get(ViewListActivity.this, ViewListActivity.this);
                    break;
            }
        }
    };

    private Runnable updateList = new Runnable()
    {
        @Override
        public void run()
        {
            switch (_currentType)
            {
                case "BILL":
                    if(BillRepository.Instance().Get() != null)
                    {
                        if(_bills == null)
                            _bills = new ArrayList<>();

                        _bills.clear();
                        _bills.addAll(BillRepository.Instance().Get());
                        _billAdapter.AddAll(_bills);

                        if(_billAdapter.getItemCount() != BillRepository.Instance().Get().size())
                            _handler.post(ConnectToApi);
                        else
                            _refreshLayout.setRefreshing(false);
                    }
                    else
                    {
                        _handler.post(ConnectToApi);
                    }
                    break;

                case "SHOPPING":
                    if(ShoppingRepository.Instance().Get() != null)
                    {
                        if(_shopping == null)
                            _shopping = new ArrayList<>();

                        _shopping.clear();
                        _shopping.addAll(ShoppingRepository.Instance().Get());
                        _shoppingAdapter.addAll(_shopping);

                        if(_shoppingAdapter.getItemCount() != ShoppingRepository.Instance().Get().size())
                            _handler.post(ConnectToApi);
                        else
                            _refreshLayout.setRefreshing(false);
                    }
                    else
                    {
                        _handler.post(ConnectToApi);
                    }
                    break;

                case "TODO":
                    if(TodoRepository.Instance().Get() != null)
                    {
                        if(_tasks == null)
                            _tasks = new ArrayList<>();

                        _tasks.clear();
                        _tasks.addAll(TodoRepository.Instance().Get());
                        _todoAdapter.AddItems(_tasks);

                        if(_todoAdapter.getItemCount() != TodoRepository.Instance().Get().size())
                            _handler.post(ConnectToApi);
                        else
                            _refreshLayout.setRefreshing(false);
                    }
                    else
                    {
                        _handler.post(ConnectToApi);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlist);

        NotificationWrapper notificationWrapper = HouseFinanceClass.GetNotificationWrapperComponent().GetNotificationWrapper();
        _session = HouseFinanceClass.GetSessionPersisterComponent().GetSessionPersister();
        _logInEndpoint = HouseFinanceClass.GetUserComponent().GetLogInEndpoint();
        _billEndpoint = HouseFinanceClass.GetBillComponent().GetBillEndpoint();
        _shoppingEndpoint = HouseFinanceClass.GetShoppingComponent().GetShoppingEndpoint();
        _toDoEndpoint = HouseFinanceClass.GetToDoComponent().GetToDoEndpoint();

        _toolbar = findViewById(R.id.appToolbar);
        _layout = findViewById(R.id.coordLayout);
        _fab = findViewById(R.id.addItem);
        _rv = findViewById(R.id.recycler_view);
        _rv.setHasFixedSize(false);
        _refreshLayout = findViewById(R.id.swipeRefresh);
        _handler = new Handler();

        if(getIntent() != null)
        {
            _currentType = getIntent().getStringExtra("ItemType");

            switch (_currentType)
            {
                case "BILL":
                    _toolbar.setTitle("Bills List");
                    _bills = new ArrayList<>();

                    if(BillRepository.Instance().Get().size() > 0)
                        _bills.addAll(BillRepository.Instance().Get());

                    _billAdapter = new BillListAdapter(_bills, this);
                    _rv.setAdapter(_billAdapter);
                    _rv.setLayoutManager(new LinearLayoutManager(this));
                    _rv.setItemViewCacheSize(20);

                    _billAdapter.setOnBillClickListener(new BillListAdapter.BillClickedListener() {
                        @Override
                        public void onBillClick(View itemView, int pos) {
                            BillListObject bill = _billAdapter.GetItem(pos);

                            Intent viewBillDetails = new Intent(ViewListActivity.this, ViewBillDetailsActivity.class);
                            viewBillDetails.putExtra("bill_id", bill.id);
                            startActivityForResult(viewBillDetails, 0);
                        }
                    });

                    _billAdapter.SetViewAllCallback(this);

                    _fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addBill = new Intent(ViewListActivity.this, AddNewBillActivity.class);
                            addBill.putExtra("SessionPersister", _session);
                            startActivityForResult(addBill, 0);
                        }
                    });

                    _peopleListPopup = new PeoplePopup(this, (ViewGroup)findViewById(android.R.id.content));

                    break;

                case "SHOPPING":
                    _toolbar.setTitle("Shopping List");
                    _shopping = new ArrayList<>();

                    if(ShoppingRepository.Instance().Get().size() > 0)
                        _shopping.addAll(ShoppingRepository.Instance().Get());

                    _shoppingAdapter = new ShoppingListAdapter(this, notificationWrapper, _shoppingEndpoint, _shopping);
                    _rv.setAdapter(_shoppingAdapter);
                    _rv.setLayoutManager(new LinearLayoutManager(this));
                    _rv.setItemViewCacheSize(20);

                    _shoppingAdapter.setOnShoppingItemClickListener(new ShoppingListAdapter.ShoppingItemClickedListener() {
                        @Override
                        public void onShoppingItemClick(View itemView, int pos) {
                            ShoppingListObject item = _shoppingAdapter.GetItem(pos);

                            if(item.ItemExpanded)
                                item.ItemExpanded = false;
                            else
                                item.ItemExpanded = true;

                            _shoppingAdapter.notifyItemChanged(pos);
                        }
                    });

                    _shoppingAdapter.SetDeleteCallback(this);
                    _shoppingAdapter.SetEditPressedCallback(this);

                    _fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addItem = new Intent(ViewListActivity.this, AddNewShoppingItemActivity.class);
                            startActivityForResult(addItem, 0);
                        }
                    });
                    break;

                case "TODO":
                    _toolbar.setTitle("Todo List");
                    _tasks = new ArrayList<>();

                    if(TodoRepository.Instance().Get().size() > 0)
                        _tasks.addAll(TodoRepository.Instance().Get());

                    _todoAdapter = new TodoListAdapter(this, notificationWrapper, _toDoEndpoint, _tasks);
                    _rv.setAdapter(_todoAdapter);
                    _rv.setLayoutManager(new LinearLayoutManager(this));
                    _rv.setItemViewCacheSize(20);

                    _todoAdapter.setOnTodoClickedListener(new TodoListAdapter.TodoItemClickedListener() {
                        @Override
                        public void onTodoClicked(View itemView, int pos) {
                            TodoListObject task = _todoAdapter.GetItem(pos);

                            if(task.ItemExpanded)
                                task.ItemExpanded = false;
                            else
                                task.ItemExpanded = true;

                            _todoAdapter.notifyItemChanged(pos);
                        }
                    });

                    _todoAdapter.SetDeleteCallback(this);
                    _todoAdapter.SetEditPressedCallback(this);

                    _fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent addTask = new Intent(ViewListActivity.this, AddNewToDoActivity.class);
                            startActivityForResult(addTask, 0);
                        }
                    });
                    break;

                default:

                    break;
            }

            setSupportActionBar(_toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            _refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    _handler.post(ConnectToApi);
                }
            });

            _refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            _refreshLayout.setRefreshing(true);
            _handler.post(ConnectToApi);
        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode)
        {
            case RESULT_OK:
                _handler.removeCallbacksAndMessages(null);
                _refreshLayout.setRefreshing(true);
                _handler.postDelayed(ConnectToApi, 200);
                break;

            case RESULT_CANCELED:

                break;

            case 100:
                _refreshLayout.setRefreshing(true);
                _handler.post(ConnectToApi);
                break;
        }
    }

    @Override
    public void onViewAllPressed(ArrayList<BillListObjectPeople> allPeople)
    {
        _peopleListPopup.Show(allPeople);
    }

    @Override
    public void onItemDeleted()
    {
        _refreshLayout.setRefreshing(true);
        _handler.post(ConnectToApi);
    }

    @Override
    public void onEditPressed(int itemid)
    {
        Intent edititem;
        switch (_currentType)
        {
            case "SHOPPING":
                edititem = new Intent(this, EditShoppingItemActivity.class);
                edititem.putExtra("id", itemid);
                startActivityForResult(edititem, 0);
                break;

            case "TODO":
                edititem = new Intent(this, EditTodoItemActivity.class);
                edititem.putExtra("id", itemid);
                startActivityForResult(edititem, 0);
                break;
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        if(_obtainingSession)
        {
            _handler.post(ConnectToApi);
            _obtainingSession = false;
            return;
        }
        _handler.post(updateList);
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        try {
            ApiErrorCodes errorCode = ApiErrorCodes.get(Integer.parseInt(message));

            if(errorCode == ApiErrorCodes.SESSION_EXPIRED || errorCode == ApiErrorCodes.SESSION_INVALID)
            {
                String sessionMessage = "Your session has expired";
                Snackbar.make(_layout, sessionMessage, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent signInRefresh = new Intent(ViewListActivity.this, SignInActivity.class);
                                signInRefresh.putExtra("Refresh", true);
                                startActivityForResult(signInRefresh, 0);
                            }
                        })
                        .show();
            }
        } catch (Exception e)
        {
            // Not an API error
            Snackbar.make(_layout, message, Snackbar.LENGTH_LONG).show();
        }

        _handler.removeCallbacksAndMessages(null);
        _refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.billentrytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(_session.HasSessionID() == false)
        {
            _obtainingSession = true;
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if(account != null)
            {
                JSONObject tokenJson = new JSONObject();
                try
                {
                    tokenJson.put("Token", account.getIdToken());
                }
                catch (JSONException e)
                {

                }
                _logInEndpoint.Post(this, this, tokenJson);
            }
            else
            {
                Intent signIn = new Intent(this, SignInActivity.class);
                signIn.putExtra("IrregularStart", true);
                startActivity(signIn);
            }
        }
    }
}
