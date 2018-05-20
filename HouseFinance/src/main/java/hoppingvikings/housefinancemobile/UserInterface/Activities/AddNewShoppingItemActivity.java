package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hoppingvikings.housefinancemobile.FileName;
import hoppingvikings.housefinancemobile.ItemType;
import hoppingvikings.housefinancemobile.FileIOHandler;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.AddShoppingItemFragment;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.Interfaces.ButtonPressedCallback;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.ShoppingCartFragment;
import hoppingvikings.housefinancemobile.WebService.CommunicationCallback;
import hoppingvikings.housefinancemobile.WebService.RequestType;
import hoppingvikings.housefinancemobile.WebService.WebHandler;

public class AddNewShoppingItemActivity extends AppCompatActivity implements CommunicationCallback {

    Button submitButton;

    CoordinatorLayout layout;

    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    TextView selectUsers;
    ImageButton editPeople;

    String itemName;

    ArrayList<Integer> _selectedUserIds = new ArrayList<>();
    ArrayList<String> _selectedUserNames = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("user_names",_selectedUserNames);
        outState.putIntegerArrayList("user_ids", _selectedUserIds);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewshoppingitem);

        if(savedInstanceState != null)
        {
            _selectedUserIds = savedInstanceState.getIntegerArrayList("user_ids");
            _selectedUserNames = savedInstanceState.getStringArrayList("user_names");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        toolbar.setTitle("Add Shopping Item");
        setSupportActionBar(toolbar);
        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        submitButton = (Button) findViewById(R.id.submitShoppingItem);

        itemNameLayout = findViewById(R.id.itemNameLayout);
        shoppingItemNameEntry = findViewById(R.id.ShoppingItemNameEntry);

        selectUsers = findViewById(R.id.selectUsers);
        editPeople = findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(AddNewShoppingItemActivity.this, SelectUsersActivity.class);
                selectusers.putExtra("multiple_user_selection", true);
                if(_selectedUserIds.size() > 0)
                {
                    selectusers.putExtra("currently_selected_ids", _selectedUserIds);
                }
                startActivityForResult(selectusers, 0);
            }
        });

        if(_selectedUserNames.size() > 0)
        {
            StringBuilder namesString = new StringBuilder();
            int index = 0;
            for (String name:_selectedUserNames) {
                if(index != _selectedUserNames.size() - 1)
                    namesString.append(name).append(", ");
                else
                    namesString.append(name);

                index++;
            }
            selectUsers.setText(namesString);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidateFields())
                {
                    ReenableElements();
                    return;
                }

                final AlertDialog confirmCancel = new AlertDialog.Builder(AddNewShoppingItemActivity.this).create();

                confirmCancel.setTitle("Submit Item?");
                confirmCancel.setMessage("Please check all details are correct before continuing");

                confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();
                    }
                });

                confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCancel.dismiss();

                        shoppingItemNameEntry.setEnabled(false);
                        editPeople.setEnabled(false);

                        JSONObject newItem = new JSONObject();

                        try{
                            newItem.put("Name", itemName);

                            JSONArray people = new JSONArray();

                            for (int id : _selectedUserIds) {
                                people.put(id);
                            }

                            newItem.put("ItemFor", people);

                            // Add the item to a file on the device
                            //GlobalObjects.WriteToFile(getContext(), newItem.toString());

                            WebHandler.Instance().UploadNewItem(AddNewShoppingItemActivity.this, newItem, AddNewShoppingItemActivity.this, ItemType.SHOPPING);
                        } catch (JSONException je)
                        {
                            Snackbar.make(layout, "Failed to create Json", Snackbar.LENGTH_LONG).show();
                            ReenableElements();
                        }
                    }
                });

                confirmCancel.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {

        if(shoppingItemNameEntry.getText().length() == 0
                && _selectedUserIds.size() == 0) {

        }
        final AlertDialog confirmCancel = new AlertDialog.Builder(this).create();

        confirmCancel.setTitle("Cancel item entry?");
        confirmCancel.setMessage("Any entered details will be lost.");

        confirmCancel.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmCancel.dismiss();
                setResult(RESULT_CANCELED);
                finish();
                //AddNewShoppingItemActivity.super.onBackPressed();
            }
        });

        confirmCancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Stay Here", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmCancel.dismiss();
            }
        });

        confirmCancel.show();

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

    public void ReenableElements()
    {
        shoppingItemNameEntry.setEnabled(true);

        editPeople.setEnabled(true);
        submitButton.setEnabled(true);
    }

    private boolean ValidateFields()
    {
        if(shoppingItemNameEntry.getText().length() > 0) {
            itemName = shoppingItemNameEntry.getText().toString();
            itemNameLayout.setError(null);
        } else {
            itemNameLayout.setError("Please enter a valid Item name");
            return false;
        }

        if(_selectedUserIds.size() < 1)
        {
            Snackbar.make(layout, "Please select at least one person for this item", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void OnSuccess(RequestType requestType, Object o)
    {
        Snackbar.make(layout, "Item successfully added", Snackbar.LENGTH_LONG).show();
        shoppingItemNameEntry.setText("");
        ReenableElements();
    }

    @Override
    public void OnFail(RequestType requestType, String message)
    {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
        ReenableElements();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 0:
                switch (resultCode)
                {
                    case RESULT_OK:
                        if(data != null)
                        {
                            _selectedUserIds = data.getIntegerArrayListExtra("selected_ids");
                            _selectedUserNames = data.getStringArrayListExtra("selected_names");

                            StringBuilder namesString = new StringBuilder();
                            int index = 0;
                            for (String name:_selectedUserNames) {
                                if(index != _selectedUserNames.size() - 1)
                                    namesString.append(name).append(", ");
                                else
                                    namesString.append(name);

                                index++;
                            }
                            selectUsers.setText(namesString);
                        }
                        break;

                    case RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    private class LoadRecentItemsAsync extends AsyncTask<Void, Void, ArrayList<JSONObject>>
    {
        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params) {
            return new FileIOHandler().ReadFile(FileName.SHOPPING_RECENT_ITEMS);
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> arrayList) {
            if(arrayList != null)
            {
                //_recentShoppingItems = arrayList;
            }
            else
            {
                //_recentShoppingItems = new ArrayList<>();
            }
        }
    }
}
