package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdEndpoint;
import hoppingvikings.housefinancemobile.Services.SaltVault.House.HouseholdInviteEndpoint;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.HouseFinanceClass;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class ViewHouseholdActivity extends AppCompatActivity implements CommunicationCallback
{
    private HouseholdInviteEndpoint _householdInviteEndpoint;

    CoordinatorLayout _layout;
    Button _leftButton;
    Button _rightButton;
    TextView _houseNameText;

    TextView _inviteLinkDesc;
    CardView _inviteCodeBackground;
    TextView _inviteCode;

    boolean _hasHousehold;
    boolean _addingDeletingHouse;
    boolean _joiningHouse;
    boolean _joinedHouse = false;
    private HouseholdEndpoint _householdEndpoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _householdEndpoint = HouseFinanceClass.GetHouseholdComponent().GetHouseholdEndpoint();
        _householdInviteEndpoint = HouseFinanceClass.GetHouseholdComponent().GetHouseholdInviteEndpoint();

        setContentView(R.layout.activity_household);
        _layout = findViewById(R.id.householdCoordLayout);
        _houseNameText = findViewById(R.id.houseNameText);
        _inviteLinkDesc = findViewById(R.id.inviteLinkDesc);
        _inviteCode = findViewById(R.id.inviteCode);
        _inviteCodeBackground = findViewById(R.id.inviteCodeBackground);
        Toolbar toolbar = findViewById(R.id.appToolbar);

        toolbar.setTitle("Household");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _inviteLinkDesc.setVisibility(View.GONE);
        _inviteCode.setVisibility(View.GONE);
        _inviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", _inviteCode.getText().toString());
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(ViewHouseholdActivity.this, "Invite link copied", Toast.LENGTH_LONG).show();
            }
        });
        _inviteCodeBackground.setVisibility(View.GONE);

        _leftButton = findViewById(R.id.leftButton);
        _rightButton = findViewById(R.id.rightButton);

        if(getIntent().hasExtra("HasHousehold"))
            _hasHousehold = getIntent().getBooleanExtra("HasHousehold", false);
        else
        {

        }

        SetupPage();
    }

    private void SetupPage()
    {
        if(_hasHousehold)
        {
            JSONObject house;
            String houseName = "";
            try {
                house = new JSONObject(FileIOHandler.Instance().ReadFileAsString("CurrentHousehold"));
                if(house.has("name"))
                {
                    houseName = house.getString("name");
                }
            } catch (JSONException je)
            {

            }

            _houseNameText.setText(houseName);

            SetupButtons();
        }
        else
        {
            _houseNameText.setText("Create or join a household below");
            SetupButtons();
        }
    }

    private void SetupButtons()
    {
        if(_hasHousehold)
        {
            _leftButton.setText("Get Invite Code");
            _rightButton.setText("Delete Household");

            // TODO Enable this once proper checking is in place
            _rightButton.setEnabled(false);

            // Invite Tenant
            _leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    _householdInviteEndpoint.Get(ViewHouseholdActivity.this, ViewHouseholdActivity.this);
                }
            });

            // Delete Household
            _rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        _addingDeletingHouse = true;
                        JSONObject house = new JSONObject();
                        house.put("KeepHousehold", false);
                        _householdEndpoint.Delete(ViewHouseholdActivity.this, ViewHouseholdActivity.this, house);
                    } catch (JSONException je)
                    {

                    }
                }
            });
        }
        else
        {
            _leftButton.setText("Create Household");
            _rightButton.setText("Join Household");

            // Add Household
            _leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View createHouseDialog = getLayoutInflater().inflate(R.layout.dialog_create_house, null);
                    final AlertDialog createHouseAlert = new AlertDialog.Builder(ViewHouseholdActivity.this).create();
                    createHouseAlert.setTitle("Create Household");
                    createHouseAlert.setCancelable(true);

                    final TextInputEditText houseNameText = createHouseDialog.findViewById(R.id.createHouseEntryText);

                    createHouseAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(houseNameText.getText().toString().length() > 0)
                            {
                                try
                                {
                                    _addingDeletingHouse = true;
                                    JSONObject house = new JSONObject();
                                    house.put("Name", houseNameText.getText().toString());
                                    _householdEndpoint.Post(ViewHouseholdActivity.this, ViewHouseholdActivity.this, house);
                                    createHouseAlert.dismiss();
                                }
                                catch (JSONException je)
                                {

                                }
                            }
                            else
                            {
                                houseNameText.setError("Please enter an invite code");
                            }
                        }
                    });

                    createHouseAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createHouseAlert.dismiss();
                        }
                    });

                    createHouseAlert.setView(createHouseDialog);
                    createHouseAlert.show();
                }
            });

            // Join Household
            _rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View joinHouseDialog = getLayoutInflater().inflate(R.layout.dialog_join_house, null);
                    final AlertDialog joinHouseAlert = new AlertDialog.Builder(ViewHouseholdActivity.this).create();
                    joinHouseAlert.setTitle("Join Household");
                    joinHouseAlert.setCancelable(true);

                    final TextInputEditText inviteInputText = joinHouseDialog.findViewById(R.id.joinHouseEntryText);

                    joinHouseAlert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(inviteInputText.getText().toString().length() > 0)
                            {
                                try {
                                    _joiningHouse = true;
                                    JSONObject joinHouse = new JSONObject();
                                    joinHouse.put("InviteLink", inviteInputText.getText().toString());
                                    _householdInviteEndpoint.Post(ViewHouseholdActivity.this, ViewHouseholdActivity.this, joinHouse);
                                    joinHouseAlert.dismiss();
                                } catch (JSONException je)
                                {

                                }
                            }
                            else
                            {
                                inviteInputText.setError("Please enter an invite code");
                            }
                        }
                    });

                    joinHouseAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            joinHouseAlert.dismiss();
                        }
                    });

                    joinHouseAlert.setView(joinHouseDialog);
                    joinHouseAlert.show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(_joinedHouse)
            setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billentrytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void OnSuccess(RequestType requestType, Object o) {
        if(requestType == RequestType.DELETE)
        {
            _hasHousehold = false;
            setResult(20);

            FileIOHandler.Instance().WriteToFile("CurrentHousehold", new JSONObject().toString());
            finish();
            return;
        }
        else if(requestType == RequestType.GET)
        {
            if(_addingDeletingHouse || _joiningHouse)
            {
                _addingDeletingHouse = false;
                _joiningHouse = false;
                _joinedHouse = true;
                _hasHousehold = true;
                SetupPage();
                return;
            }

            String inviteCode = o.toString();
            _inviteCode.setText(inviteCode);
            _inviteCode.setVisibility(View.VISIBLE);
            _inviteLinkDesc.setVisibility(View.VISIBLE);
            _inviteCodeBackground.setVisibility(View.VISIBLE);
        }
        else
        {
            if(_addingDeletingHouse || _joiningHouse)
            {
                _householdEndpoint.Get(this, this);
                return;
            }
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message) {
        Snackbar.make(_layout, message, Snackbar.LENGTH_LONG).show();
    }
}
