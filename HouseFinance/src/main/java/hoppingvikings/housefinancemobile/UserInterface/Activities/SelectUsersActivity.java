package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import hoppingvikings.housefinancemobile.Endpoints.SaltVault.User.UserEndpoint;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.Person;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Lists.UserSelectList.IUserClickedListener;
import hoppingvikings.housefinancemobile.UserInterface.Lists.UserSelectList.UserSelectAdapter;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class SelectUsersActivity extends AppCompatActivity implements CommunicationCallback<ArrayList<Person>>
{
    private UserEndpoint _userEndpoint;

    private ArrayList<Person> _users;
    private boolean _multipleSelect;
    private HashMap<Integer, Integer> _selectedUserIds;
    private ArrayList<String> _selectedUserNames;
    private int _selectedUserId;
    private String _selectedUserName;
    private Handler _handler;

    private CoordinatorLayout _layout;
    private RecyclerView _rv;
    private UserSelectAdapter _adapter;
    private TextView _failedToGetUsers;

    private Runnable requestUsers = new Runnable()
    {
        @Override
        public void run()
        {
            _userEndpoint.Get(SelectUsersActivity.this, SelectUsersActivity.this);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userselect);

        _userEndpoint = HouseFinanceClass.GetUserComponent().GetUserEndpoint();

        _layout = (CoordinatorLayout) findViewById(R.id.coordlayout);
        _rv = (RecyclerView) findViewById(R.id.usersList);
        _failedToGetUsers = (TextView) findViewById(R.id.userGetFailed);
        _failedToGetUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _handler.post(requestUsers);
                _failedToGetUsers.setVisibility(View.GONE);
            }
        });
        _failedToGetUsers.setVisibility(View.GONE);
        _handler = new Handler();
        _handler.postDelayed(requestUsers, 200);
        _users = new ArrayList<>();
        _selectedUserIds = new HashMap<>();
        _selectedUserNames = new ArrayList<>();
        _selectedUserId = -1;
        _selectedUserName = "";

        Intent extras = getIntent();
        if(extras != null)
        {
            _multipleSelect = extras.hasExtra("multiple_user_selection") && extras.getBooleanExtra("multiple_user_selection", false);
            if(extras.hasExtra("currently_selected_ids"))
            {
                ArrayList<Integer> ids = extras.getIntegerArrayListExtra("currently_selected_ids");
                for (int id : ids) {
                    _selectedUserIds.put(id, id);
                }
            }

            if(extras.hasExtra("currently_selected_id"))
            {
                _selectedUserId = extras.getIntExtra("currently_selected_id", -1);
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);

        if(_multipleSelect)
            toolbar.setTitle("Select people for this item");
        else
            toolbar.setTitle("Select person for this item");

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _adapter = new UserSelectAdapter(_users, this);
        _adapter.setOnUserClickedListener(new IUserClickedListener() {
            @Override
            public void onUserClicked(View itemView, int pos) {
                Person selectedUser = _adapter.GetUser(pos);
                if(!selectedUser.selected)
                {
                    if(_multipleSelect)
                    {
                        _selectedUserIds.put(selectedUser.ID, selectedUser.ID);
                        _selectedUserNames.add(selectedUser.FirstName);
                        selectedUser.selected = true;
                        _adapter.notifyItemChanged(pos);
                    }
                    else
                    {
                        for (Person user: _users) {
                            if(user.selected)
                                user.selected = false;
                        }
                        _selectedUserId = selectedUser.ID;
                        _selectedUserName = selectedUser.FirstName;
                        selectedUser.selected = true;
                        _adapter.notifyItemRangeChanged(0, _adapter.getItemCount());
                    }

                }
                else
                {
                    if(_multipleSelect)
                    {
                        _selectedUserIds.remove(selectedUser.ID);
                        _selectedUserNames.remove(selectedUser.FirstName);
                        selectedUser.selected = false;
                        _adapter.notifyItemChanged(pos);
                    }
                    else
                    {
                        _selectedUserId = -1;
                        _selectedUserName = "";
                        selectedUser.selected = false;
                        _adapter.notifyItemRangeChanged(0, _adapter.getItemCount());
                    }


                }

            }
        });

        _rv.setAdapter(_adapter);
        _rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        if(_multipleSelect)
        {
            if(_selectedUserIds.size() > 0)
            {
                Intent returnedData = new Intent();
                ArrayList<Integer> selectedids = new ArrayList<>(_selectedUserIds.values());
                returnedData.putExtra("selected_ids", selectedids);
                returnedData.putExtra("selected_names", _selectedUserNames);

                setResult(RESULT_OK, returnedData);
                finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        else
        {
            if(_selectedUserId > -1)
            {
                Intent returnedData = new Intent();
                returnedData.putExtra("selected_id", _selectedUserId);
                returnedData.putExtra("selected_name", _selectedUserName);

                setResult(RESULT_OK, returnedData);
                finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_select_users, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.toolbar_selected_users:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void OnSuccess(RequestType requestType, ArrayList<Person> users)
    {
        _users.addAll(users);

        if(_selectedUserIds.size() > 0)
        {
            ArrayList<Integer> ids = new ArrayList<>(_selectedUserIds.values());
            for (int id : ids) {
                for (Person user:_users) {
                    if(user.ID == id)
                    {
                        user.selected = true;
                        _selectedUserNames.add(user.FirstName);
                    }
                }
            }
        }

        if(_selectedUserId > -1)
        {
            for (Person user : _users) {
                if(user.ID == _selectedUserId)
                {
                    user.selected = true;
                    _selectedUserName = user.FirstName;
                }
            }
        }

        _adapter.AddUsers(_users);
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        _failedToGetUsers.setVisibility(View.VISIBLE);
    }
}
