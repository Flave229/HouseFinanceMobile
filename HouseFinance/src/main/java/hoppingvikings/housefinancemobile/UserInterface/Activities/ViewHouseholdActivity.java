package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class ViewHouseholdActivity extends AppCompatActivity implements CommunicationCallback {

    CoordinatorLayout _layout;
    Button _leftButton;
    Button _rightButton;
    TextView _houseName;

    boolean _hasHousehold;

    boolean _addingDeletingHouse;
    boolean _joiningHouse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        _layout = findViewById(R.id.householdCoordLayout);
        _houseName = findViewById(R.id.houseNameText);
        _leftButton = findViewById(R.id.leftButton);
        _rightButton = findViewById(R.id.rightButton);

        setResult(RESULT_CANCELED);

        if(getIntent().hasExtra("HasHousehold"))
            _hasHousehold = getIntent().getBooleanExtra("HasHousehold", false);

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

            _houseName.setText(houseName);

            SetupButtons();
        }
        else
        {
            _houseName.setText("Join or create a house below");
            SetupButtons();
        }
    }

    private void SetupButtons()
    {
        if(_hasHousehold)
        {
            _leftButton.setText("Invite Tenants");
            _rightButton.setText("Delete Household");

            // Invite Tenant
            _leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                        WebHandler.Instance().DeleteHousehold(ViewHouseholdActivity.this, house, ViewHouseholdActivity.this);
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
                    try {
                        _addingDeletingHouse = true;
                        JSONObject house = new JSONObject();
                        house.put("Name", "Test");
                        WebHandler.Instance().UploadNewItem(ViewHouseholdActivity.this, house, ViewHouseholdActivity.this, ItemType.HOUSEHOLD);
                    } catch (JSONException je)
                    {

                    }

                }
            });

            // Join Household
            _rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o) {
        if(requestType == RequestType.DELETE)
        {
            _hasHousehold = false;
            setResult(RESULT_OK);

            FileIOHandler.Instance().WriteToFile("CurrentHousehold", new JSONObject().toString());
            SetupPage();
            return;
        }
        else if(requestType == RequestType.GET)
        {
            if(!o.toString().equals(""))
            {
                _hasHousehold = true;
                SetupPage();
            }
        }
        else
        {
            if(_addingDeletingHouse)
            {
                _addingDeletingHouse = false;
                WebHandler.Instance().GetHousehold(this, this);
                return;
            }
        }
    }

    @Override
    public void OnFail(RequestType requestType, String message) {

    }
}
