package hoppingvikings.housefinancemobile.UserInterface.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hoppingvikings.housefinancemobile.GlobalObjects;
import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Items.ShoppingListObject;
import hoppingvikings.housefinancemobile.WebService.UploadCallback;

/**
 * Created by iView on 11/08/2017.
 */

public class EditShoppingItemActivity extends AppCompatActivity implements UploadCallback {

    Button submitButton;

    TextInputLayout itemNameLayout;
    TextInputEditText shoppingItemNameEntry;

    TextView selectUsers;
    ImageButton editPeople;

    CheckBox editName;
    CheckBox editFor;

    String itemName;

    CoordinatorLayout layout;

    ShoppingListObject item = null;

    ArrayList<Integer> _selectedUserIds = new ArrayList<>();
    ArrayList<String> _selectedUserNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editshoppingitem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        toolbar.setTitle("Edit Shopping item");
        toolbar.setSubtitle("Tick the fields you wish to edit");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().hasExtra("id"))
        {
            item = GlobalObjects.ShoppingRepository.GetFromId(getIntent().getIntExtra("id", -1));
        }

        layout = (CoordinatorLayout) findViewById(R.id.coordlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton = (Button) findViewById(R.id.submitBill);
        itemNameLayout = (TextInputLayout)  findViewById(R.id.itemNameLayout);
        shoppingItemNameEntry = (TextInputEditText)  findViewById(R.id.ShoppingItemNameEntry);
        itemNameLayout.setEnabled(false);
        shoppingItemNameEntry.setEnabled(false);

        selectUsers = (TextView) findViewById(R.id.selectUsers);
        editPeople = (ImageButton) findViewById(R.id.editPeople);
        editPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectusers = new Intent(EditShoppingItemActivity.this, SelectUsersActivity.class);
                selectusers.putExtra("multiple_user_selection", true);
                if(_selectedUserIds.size() > 0)
                {
                    selectusers.putExtra("currently_selected_ids", _selectedUserIds);
                }
                startActivityForResult(selectusers, 0);
            }
        });
        editPeople.setEnabled(false);
        editPeople.setAlpha(0.3f);

        if(item != null)
        {
            shoppingItemNameEntry.setText(item.ItemName);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Shopping item not found", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        editName = (CheckBox) findViewById(R.id.editNameCheck);
        editFor = (CheckBox) findViewById(R.id.editForCheck);

        SetEditCheckListeners();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editName.isChecked() && !editFor.isChecked() )
                {
                    Snackbar.make(layout, "Please edit at least one field to submit", Snackbar.LENGTH_LONG).show();
                    return;
                }

                SubmitEditedShoppingItem();
            }
        });
    }

    private void SetEditCheckListeners()
    {
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.isChecked())
                {
                    itemNameLayout.setEnabled(true);
                    shoppingItemNameEntry.setEnabled(true);
                    shoppingItemNameEntry.requestFocus();
                }
                else
                {
                    itemNameLayout.setEnabled(false);
                    shoppingItemNameEntry.setEnabled(false);
                }
            }
        });

        editFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editFor.isChecked())
                {
                    editPeople.setEnabled(true);
                    editPeople.setAlpha(1.0f);
                }
                else
                {
                    editPeople.setEnabled(false);
                    editPeople.setAlpha(0.3f);
                }
            }
        });

    }

    private void SubmitEditedShoppingItem()
    {
        if(!ValidateFields()) {
            return;
        }

        final AlertDialog confirmcancel = new AlertDialog.Builder(this).create();
        confirmcancel.setMessage("Submit edit? Check that all details are correct before submitting");
        confirmcancel.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmcancel.dismiss();
            }
        });

        confirmcancel.setButton(DialogInterface.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmcancel.dismiss();
                JSONObject editedBill = new JSONObject();
                try {
                    editedBill.put("Id", item.Id);
                    if(editName.isChecked())
                        editedBill.put("Name", shoppingItemNameEntry.getText().toString());

                    if(editFor.isChecked())
                    {
                        JSONArray people = new JSONArray();
                        for (int id : _selectedUserIds) {
                            people.put(id);
                        }

                        editedBill.put("ItemFor", people);
                    }

                    GlobalObjects.WebHandler.EditItem(EditShoppingItemActivity.this, editedBill, EditShoppingItemActivity.this, GlobalObjects.ITEM_TYPE_SHOPPING);

                } catch (Exception e)
                {
                    Snackbar.make(layout, "Failed to create JSON", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        confirmcancel.show();
    }

    private boolean ValidateFields()
    {
        if(shoppingItemNameEntry.getText().length() > 0) {
            itemName = shoppingItemNameEntry.getText().toString();
            itemNameLayout.setError(null);
        }
        else {
            shoppingItemNameEntry.requestFocus();
            itemNameLayout.setError("Please enter a valid Bill name");
            return false;
        }

        if(editFor.isChecked())
        {
            if(_selectedUserIds.size() < 1)
            {
                Snackbar.make(layout, "Please select at least one person for this bill", Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case RESULT_OK:
                if(data != null)
                {
                    _selectedUserIds = data.getIntegerArrayListExtra("selected_ids");
                    _selectedUserNames = data.getStringArrayListExtra("selected_names");

                    String namesString = "";
                    int index = 0;
                    for (String name:_selectedUserNames) {
                        if(index != _selectedUserNames.size() - 1)
                            namesString += (name + ", ");
                        else
                            namesString += name;

                        index++;
                    }
                    selectUsers.setText(namesString);
                }
                break;

            case RESULT_CANCELED:

                break;
        }
    }

    @Override
    public void OnSuccessfulUpload() {
        Toast.makeText(getApplicationContext(), "Edit successfully uploaded", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void OnFailedUpload(String failReason) {
        Snackbar.make(layout, failReason, Snackbar.LENGTH_LONG).show();
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
}
